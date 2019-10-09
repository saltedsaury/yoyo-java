package cn.idachain.finance.batch.task.task;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.enums.AccountType;
import cn.idachain.finance.batch.common.enums.Direction;
import cn.idachain.finance.batch.common.enums.TransferOrderStatus;
import cn.idachain.finance.batch.common.enums.TransferProcessStatus;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.service.dao.impl.TransferOrderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author kun
 * @version 2019/10/8 09:55
 */
@Component
public class ReconcileTask {

    private static final Logger log = LoggerFactory.getLogger(ReconcileTask.class);
    private static final String IN_CODE = Direction.IN.getCode();

    @Autowired
    private IRecBalanceSnapshotDao snapshotDao;
    @Autowired
    private IBalanceInternalDao balanceInternalDao;
    @Autowired
    private TransferOrderDao transferOrderDao;
    @Autowired
    private IBalancePersonDao balancePersonDao;
    @Autowired
    private IBalanceOrgDao balanceOrgDao;
    @Autowired
    private IRecAccountSnapshotDao accountSnapshotDao;
    @Autowired
    private IBalanceDetailDao balanceDetailDao;

    /**
     * 对账
     * 对账逻辑：
     * 1.资金总账与机构账户和用户账户 balanceInternal = balanceOrgAll + balancePerson
     * 2.资金快照与资金总账户 snapshot(in - out) = balanceInternal
     * 3.划转出入 transferOrder
     * 3.收益订单 bonusOrder -> personAmount / orgFinancingAmount
     * 4.到期还本 revenuePlan -> personAmount / orgFinancingAmount
     * 5.理赔记录 compensateTrade -> personAmount / orgInsuranceAmount (in / out)
     * 6.投资成功 investInfo -> personAmount / orgFinancingAmount
     * 7.提前赎回 redemptionTrade -> personAmount / orgFinancingAmount / orgFeeAmount
     */
    @Transactional
    public void reconcile() {
        Long previousTime = accountSnapshotDao.getLastSnapshotTime() + 1L;
        Long nowTime = System.currentTimeMillis() - 1000L;

        checkBalanceInternal();
        log.info("check internal balance success!");
        checkBalanceSnapshot();
        log.info("check balance snapshot success!");

        // k: account:ccy, v: amount
        Map<String, BigDecimal> internalAmount = new HashMap<>(16);
        Map<String, BigDecimal> personAmount = new HashMap<>(1024);
        Map<String, BigDecimal> orgAmount = new HashMap<>(32);
        detailToMap(() -> balanceDetailDao.getDetailsByTransferOrderBetweenTime(previousTime, nowTime),
                internalAmount, personAmount, orgAmount);
        detailToMap(() -> balanceDetailDao.getDetailsByBonusOrderBetweenTime(previousTime, nowTime),
                internalAmount, personAmount, orgAmount);
        detailToMap(() -> balanceDetailDao.getDetailsByRevenuePlanBetweenTime(previousTime, nowTime),
                internalAmount, personAmount, orgAmount);
        detailToMap(() -> balanceDetailDao.getDetailsByCompensationBetweenTime(previousTime, nowTime),
                internalAmount, personAmount, orgAmount);
        detailToMap(() -> balanceDetailDao.getDetailsByInvestInfoBetweenTime(previousTime, nowTime),
                internalAmount, personAmount, orgAmount);
        detailToMap(() -> balanceDetailDao.getDetailsByRedemptionBetweenTime(previousTime, nowTime),
                internalAmount, personAmount, orgAmount);

        // 前一快照之后没有新资金变动
        if (internalAmount.isEmpty() && personAmount.isEmpty() && orgAmount.isEmpty()) {
            log.info("reconcile success! no fresh orders ");
            return;
        }
        // 数量过多时需要优化
        if (personAmount.size() > 10000) {
            log.info("reconcile batch size: {}", personAmount.size());
        }
        // 前一快照结果 + 最近差值 ?= balance
        Set<String> accountSet = new HashSet<>(personAmount.keySet());
        accountSet.addAll(internalAmount.keySet());
        accountSet.addAll(orgAmount.keySet());
        List<RecAccountSnapshot> accountSnapshots = accountSnapshotDao.getSnapshotByAccounts(accountSet);
        accountSnapshots.forEach(s ->
                choseMap(s.getAccountType(), internalAmount, personAmount, orgAmount)
                        .merge(s.getAccount(), s.getBalance(), BigDecimal::add)
        );

        boolean ok = checkBalanceDetail(internalAmount, AccountType.INTERNAL) &
                checkBalanceDetail(personAmount, AccountType.PERSON) &
                checkBalanceDetail(orgAmount, AccountType.ORG);

        if (ok) {
            log.info("new account snapshots are inserting");
            // 插入新的快照
            accountSnapshotDao.insertBatch(buildSnapshot(internalAmount, AccountType.INTERNAL, nowTime));
            accountSnapshotDao.insertBatch(buildSnapshot(personAmount, AccountType.PERSON, nowTime));
            accountSnapshotDao.insertBatch(buildSnapshot(orgAmount, AccountType.ORG, nowTime));
            log.info("reconcile success! detail checked");
            return;
        }
        throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
    }

    private boolean checkBalanceDetail(Map<String, BigDecimal> accountAmount, AccountType accountType) {
        if (accountAmount == null || accountAmount.isEmpty()) {
            return true;
        }
        Set<String> accounts = accountAmount.keySet().stream()
                .map(account -> account.substring(0, account.indexOf(":")))
                .collect(Collectors.toSet());

        List<? extends BaseBalance> balanceList;
        switch (accountType) {
            case INTERNAL:
                balanceList = balanceInternalDao.getBalanceSpecial(accounts);
                break;
            case PERSON:
                balanceList = balancePersonDao.getBalanceSpecial(accounts);
                break;
            case ORG:
                balanceList = balanceOrgDao.getBalanceSpecial(accounts);
                break;
            default:
                // never occur
                log.error("account type err");
                return false;
        }
        List<? extends BaseBalance> errList = balanceList.stream().filter(b -> {
            BigDecimal actualBalance = accountAmount.get(b.getAccountNo());
            if (actualBalance == null) {
                // 该币种未变动
                return false;
            }
            return b.getBalance().compareTo(actualBalance) != 0;
        }).collect(Collectors.toList());

        if (!errList.isEmpty()) {
            errList.forEach(e ->
                    log.error("internal balance check failed! account: {}, balance: {}, expect: {}",
                            e.getAccountNo(), e.getBalance(), accountAmount.get(e.getAccountNo()))
            );
            return false;
        }
        return true;
    }

    private void checkBalanceInternal() {
        Map<String, BigDecimal> internalCcyAmount = balanceInternalDao.getAllBalance().stream().collect(
                Collectors.toMap(BalanceInternal::getCurrency, BalanceInternal::getBalance, BigDecimal::add)
        );

        Map<String, BigDecimal> personBalance = balancePersonDao.getAllCcyBalance();
        Map<String, BigDecimal> orgBalance = balanceOrgDao.getAllCcyBalance();
        personBalance.forEach((ccy, amount) -> orgBalance.merge(ccy, amount, BigDecimal::add));

        if (orgBalance.size() != internalCcyAmount.size()) {
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }

        boolean errFlag = false;
        BigDecimal amount;
        for (Map.Entry<String, BigDecimal> entry : orgBalance.entrySet()) {
            if ((amount = internalCcyAmount.get(entry.getKey())) == null || amount.compareTo(entry.getValue()) != 0) {
                errFlag = true;
                log.error("check internal balance failed! ccy {} internal: {}, sum: {}",
                        entry.getKey(), amount, entry.getValue());
            }
        }
        if (errFlag) {
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
    }

    private void checkBalanceSnapshot() {
        Map<String, BigDecimal> snapshotDelta = getSnapshotDelta();
        if (snapshotDelta.isEmpty()) {
            return;
        }

        Map<String, BigDecimal> processingMap = transferOrderDao.getTransferOrderByStatus(
                TransferOrderStatus.PROCESSING.getCode(),
                Arrays.asList(
                        TransferProcessStatus.CHARGEBACK_SUCCESS.getCode(),
                        TransferProcessStatus.TRANSFERED_FAILED.getCode()
                )
        ).stream().collect(Collectors.toMap(TransferOrder::getCcy, TransferOrder::getAmount, BigDecimal::add));
        if (processingMap.size() != snapshotDelta.size()) {
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }

        boolean errFlag = false;
        BigDecimal deltaAmount;
        for (Map.Entry<String, BigDecimal> entry : snapshotDelta.entrySet()) {
            if ((deltaAmount = processingMap.get(entry.getKey())) == null ||
                    deltaAmount.compareTo(entry.getValue()) != 0) {
                errFlag = true;
                log.error("balance snapshot check failed! ccy {} snapshot delta {}, order delta {}",
                        entry.getKey(), entry.getValue(), deltaAmount);
            }
        }
        if (errFlag) {
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
    }

    private Map<String, BigDecimal> getSnapshotDelta() {
        Map<String, BigDecimal> snapshotCcyAmount = snapshotDao.lastSnapshot().stream().collect(
                Collectors.toMap(RecBalanceSnapshot::getCurrency, s -> s.getInAmount().subtract(s.getOutAmount()))
        );
        Map<String, BigDecimal> internalCcyAmount = balanceInternalDao.getAllBalance().stream().collect(
                Collectors.toMap(BalanceInternal::getCurrency, BalanceInternal::getBalance, BigDecimal::add)
        );
        if (snapshotCcyAmount.size() != internalCcyAmount.size()) {
            log.error("balance snapshot check failed! snapshot: {}, internal: {}", snapshotCcyAmount, internalCcyAmount);
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }

        boolean stopFlag = false;
        String ccy;
        BigDecimal snapshotAmount;
        Map<String, BigDecimal> deltaMap = new HashMap<>(4);
        for (Map.Entry<String, BigDecimal> entry : snapshotCcyAmount.entrySet()) {
            ccy = entry.getKey();
            BigDecimal internalAmount = internalCcyAmount.get(ccy);
            if (internalAmount == null) {
                log.error("balance snapshot check failed! ccy {} not match!", ccy);
                stopFlag = true;
                continue;
            }
            snapshotAmount = entry.getValue();
            int equal = snapshotAmount.compareTo(internalAmount);
            if (equal == 0) {
                // good
                continue;
            }
            if (equal < 0) {
                log.error("balance snapshot check failed! ccy {} snapshot {} internal {}",
                        ccy, snapshotAmount, internalAmount);
                stopFlag = true;
                continue;
            }
            // snapshotAmount > internalAmount 需要检查中间态 transferOrder 数据
            deltaMap.put(ccy, snapshotAmount.subtract(internalAmount));
        }
        if (stopFlag) {
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
        return deltaMap;
    }

    private void detailToMap(Supplier<List<BalanceDetail>> detailQuery,
                             Map<String, BigDecimal> internalAmount,
                             Map<String, BigDecimal> personAmount,
                             Map<String, BigDecimal> orgAmount) {
        detailQuery.get().forEach(d -> {
            choseMap(d.getAccountType(), internalAmount, personAmount, orgAmount).merge(
                    d.getAccountNo() + ":" + d.getCurrency(),
                    IN_CODE.equals(d.getTransType()) ? d.getAmount() : d.getAmount().negate(),
                    BigDecimal::add
            );
        });
    }

    private Map<String, BigDecimal> choseMap(String accountTypeCode,
                                             Map<String, BigDecimal> internalAmount,
                                             Map<String, BigDecimal> personAmount,
                                             Map<String, BigDecimal> orgAmount) {
        switch (AccountType.getByName(accountTypeCode)) {
            case INTERNAL:
                return internalAmount;
            case PERSON:
                return personAmount;
            case ORG:
                return orgAmount;
            default:
                log.error("account {} type err", accountTypeCode);
                throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
    }

    private List<RecAccountSnapshot> buildSnapshot(Map<String, BigDecimal> accountAmount,
                                                   AccountType accountType,
                                                   Long snapshotTime) {
        return accountAmount.entrySet().stream()
                .map(e -> {
                    RecAccountSnapshot s = new RecAccountSnapshot();
                    s.setAccount(e.getKey());
                    s.setAccountType(accountType.name());
                    s.setBalance(e.getValue());
                    s.setSnapshotTime(snapshotTime);
                    return s;
                })
                .collect(Collectors.toList());
    }

}
