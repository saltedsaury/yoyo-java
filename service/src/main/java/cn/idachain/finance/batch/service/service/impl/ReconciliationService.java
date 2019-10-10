package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.enums.AccountType;
import cn.idachain.finance.batch.common.enums.Direction;
import cn.idachain.finance.batch.common.enums.TransferOrderStatus;
import cn.idachain.finance.batch.common.enums.TransferProcessStatus;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.service.dao.impl.TransferOrderDao;
import cn.idachain.finance.batch.service.service.IReconciliationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author kun
 * @version 2019/10/9 17:49
 */
@Service
public class ReconciliationService implements IReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);

    private static final String IN_CODE = Direction.IN.getCode();
    private static final String SEPARATOR = ":";

    @Autowired
    private IRecBalanceSnapshotDao balanceSnapshotDao;
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

    @Override
    public long buildBalanceSnapshot() {
        long end = System.currentTimeMillis() - 10 * 1000L;
        List<RecBalanceSnapshot> snapshots = balanceSnapshotDao.lastSnapshot();
        long start = snapshots.stream().mapToLong(RecBalanceSnapshot::getSnapshotTime).findAny().orElse(0);
        if (start >= end) {
            log.info("now {} snapshot is fresh", start);
            return start;
        }
        Map<String, RecBalanceSnapshot> snapshotMap = snapshots.stream()
                .collect(Collectors.toMap(RecBalanceSnapshot::getCurrency, Function.identity()));

        List<TransferOrder> orders = transferOrderDao.getTransferOrderBetween(start + 1, end);
        String ccy;
        boolean in;
        for (TransferOrder order : orders) {
            ccy = order.getCcy();
            in = Direction.IN.getCode().equals(order.getDeriction());
            RecBalanceSnapshot snapshot = snapshotMap.get(ccy);
            if (snapshot == null) {
                snapshot = new RecBalanceSnapshot();
                snapshot.setCurrency(ccy);
                if (in) {
                    snapshot.setInAmount(order.getAmount());
                    snapshot.setOutAmount(BigDecimal.ZERO);
                } else {
                    snapshot.setInAmount(BigDecimal.ZERO);
                    snapshot.setOutAmount(order.getAmount());
                }
                snapshot.setSnapshotTime(end);
                snapshotMap.put(ccy, snapshot);
            } else {
                if (in) {
                    snapshot.setInAmount(snapshot.getInAmount().add(order.getAmount()));
                } else {
                    snapshot.setOutAmount(snapshot.getOutAmount().add(order.getAmount()));
                }
            }
        }
        balanceSnapshotDao.insertSnapshotBatch(snapshotMap.values());
        return end;
    }

    /**
     * 比对内部账户总额是否等于机构账户和用户账户之和
     *
     * @deprecated 不再全表扫描 balancePerson + balanceOrg，
     * {@link ReconciliationService#checkBalanceInternal(Map, List)} 比较 Internal 快照是否和 Person + Org 一致
     */
    @Deprecated
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void checkBalanceInternal() {
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
        log.info("check internal balance success!");
    }

    /**
     * 比较 balanceSnapshot 是否等于内部账户
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void checkBalanceSnapshot() {
        // 通过查询 transferOrder 开启快照。固定最新 id
        Long lastId = transferOrderDao.lastId();
        // 查询 balanceSnapshot，该表的幻读问题通过单线程线程池串行保证（尽管由于时间间隔幻读可能很小）
        List<RecBalanceSnapshot> snapshots = balanceSnapshotDao.lastSnapshot();
        // balanceInternal 表不考虑幻读问题
        Map<String, BigDecimal> deltaMap = getSnapshotDelta(snapshots);

        if (deltaMap.isEmpty()) {
            log.info("check balance snapshot success!");
            return;
        }

        // 有差值时需要确认 transferOrder 处理中的订单，按币种聚合
        Map<String, BigDecimal> processingMap = transferOrderDao
                .getTransferOrderByStatusBeforeId(
                        TransferOrderStatus.PROCESSING.getCode(),
                        Arrays.asList(
                                TransferProcessStatus.CHARGEBACK_SUCCESS.getCode(),
                                TransferProcessStatus.TRANSFERED_FAILED.getCode()
                        ),
                        lastId
                ).stream()
                .collect(Collectors.toMap(TransferOrder::getCcy, TransferOrder::getAmount, BigDecimal::add));
        if (processingMap.size() != deltaMap.size()) {
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }

        boolean errFlag = false;
        BigDecimal deltaAmount;
        for (Map.Entry<String, BigDecimal> entry : deltaMap.entrySet()) {
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
        log.info("check balance snapshot success!");
    }

    /**
     * 计算 balanceSnapshot 与 balanceInternal 差异
     * balanceInternal 表新增情况少，不考虑幻读导致的问题
     *
     * @return 差异币种-金额
     */
    private Map<String, BigDecimal> getSnapshotDelta(List<RecBalanceSnapshot> snapshots) {
        Map<String, BigDecimal> snapshotCcyAmount = snapshots.stream().collect(
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

    /**
     * order == balance
     * internal == org + person
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void checkOrderDetail() {
        Long nowTime = System.currentTimeMillis() - 10 * 1000L;
        Long previousTime = accountSnapshotDao.getLastSnapshotTime() + 1L;

        // step three check detail
        // k: account:ccy, v: amount
        Map<Token, BigDecimal> internalAmount = new HashMap<>(16);
        Map<Token, BigDecimal> personAmount = new HashMap<>(1024);
        Map<Token, BigDecimal> orgAmount = new HashMap<>(32);
        // 收集与上一次对账期间的订单
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

        // 比较 internal 余额和 person + org
        checkBalanceInternal(internalAmount, Arrays.asList(personAmount, orgAmount));

        // 确认预期值（前一快照结果 + 最近差值）是否等于实际余额
        List<String> accountList = Stream
                .of(personAmount, internalAmount, orgAmount)
                .flatMap(map -> map.keySet().stream())
                .map(Token::getAccountNo)
                .collect(Collectors.toList());
        List<RecAccountSnapshot> accountSnapshots = accountSnapshotDao.getSnapshotByAccounts(accountList);
        accountSnapshots.forEach(s -> choseMap(s.getAccountType(), internalAmount, personAmount, orgAmount)
                .computeIfPresent(
                        new Token(s.getAccount(), s.getCcy()), (token, balance) -> balance.add(s.getBalance())
                )
        );

        boolean ok = checkBalanceDetail(internalAmount, AccountType.INTERNAL) &
                checkBalanceDetail(personAmount, AccountType.PERSON) &
                checkBalanceDetail(orgAmount, AccountType.ORG);

        if (ok) {
            log.info("new account snapshots are inserting.");
            // 插入新的快照
            accountSnapshotDao.insertBatch(buildSnapshot(internalAmount, AccountType.INTERNAL, nowTime));
            accountSnapshotDao.insertBatch(buildSnapshot(personAmount, AccountType.PERSON, nowTime));
            accountSnapshotDao.insertBatch(buildSnapshot(orgAmount, AccountType.ORG, nowTime));
            log.info("detail check success");
            return;
        }
        throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
    }

    private void checkBalanceInternal(Map<Token, BigDecimal> internal, List<Map<Token, BigDecimal>> others) {
//        Map<String, BigDecimal> target =
//                new HashMap<>(others.stream().mapToInt(Map::size).map(i -> i << 1).max().orElse(16));
//        internal.forEach((i, j) -> target.put(i.getCurrency(), j));
        Map<String, BigDecimal> target = new HashMap<>();
        for (Map.Entry<Token, BigDecimal> entry : internal.entrySet()) {
            target.put(entry.getKey().getCurrency(), entry.getValue());
        }

        others.stream().flatMap(map -> map.entrySet().stream())
                .forEach(e -> target.put(
                        e.getKey().getCurrency(),
                        target.getOrDefault(e.getKey().getCurrency(), BigDecimal.ZERO).subtract(e.getValue())
                ));
        boolean err = false;
        for (Map.Entry<String, BigDecimal> entry : target.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) != 0) {
                err = true;
                log.error("internal balance check failed! ccy: {}, delta: {}", entry.getKey(), entry.getValue());
            }
        }
        if (err) {
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
        log.info("check balance internal success!");
    }

    /**
     * 收集不同交易类型产生的金额明细
     *
     * @param detailQuery    查询方案
     * @param internalAmount 内部明细
     * @param personAmount   用户明细
     * @param orgAmount      机构明细
     */
    private void detailToMap(Supplier<List<BalanceDetail>> detailQuery,
                             Map<Token, BigDecimal> internalAmount,
                             Map<Token, BigDecimal> personAmount,
                             Map<Token, BigDecimal> orgAmount) {
        detailQuery.get().forEach(d -> {
            choseMap(d.getAccountType(), internalAmount, personAmount, orgAmount).merge(
                    new Token(d.getAccountNo(), d.getCurrency()),
                    IN_CODE.equals(d.getTransType()) ? d.getAmount() : d.getAmount().negate(),
                    BigDecimal::add
            );
        });
    }

    /**
     * 确认金额
     *
     * @param accountAmount key->account:ccy, value->balance. 预期值
     * @param accountType   账户类型
     * @return 正确否
     */
    private boolean checkBalanceDetail(Map<Token, BigDecimal> accountAmount, AccountType accountType) {
        if (accountAmount == null || accountAmount.isEmpty()) {
            return true;
        }
        Set<String> accounts = accountAmount.keySet().stream()
                .map(Token::getAccountNo)
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
        long errCount = balanceList.stream().filter(b -> {
            BigDecimal actualBalance = accountAmount.get(new Token(b.getAccountNo(), b.getCurrency()));
            if (actualBalance == null) {
                // 该币种未变动
                return false;
            }
            if (b.getBalance().compareTo(actualBalance) != 0) {
                log.error("internal balance check failed! account: {}, ccy: {}, balance: {}, expect: {}",
                        b.getAccountNo(), b.getCurrency(), b.getBalance(), actualBalance);
                return true;
            }
            return false;
        }).count();

        return errCount == 0;
    }

    private Map<Token, BigDecimal> choseMap(String accountTypeCode,
                                            Map<Token, BigDecimal> internalAmount,
                                            Map<Token, BigDecimal> personAmount,
                                            Map<Token, BigDecimal> orgAmount) {
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

    /**
     * 构建当下新的快照
     *
     * @param accountAmount key->account:ccy, value:balance
     * @param accountType   账户类型
     * @param snapshotTime  快照时间
     * @return 新快照集合
     */
    private List<RecAccountSnapshot> buildSnapshot(Map<Token, BigDecimal> accountAmount,
                                                   AccountType accountType,
                                                   Long snapshotTime) {
        return accountAmount.entrySet().stream()
                .map(e -> {
                    RecAccountSnapshot s = new RecAccountSnapshot();
                    s.setAccount(e.getKey().getAccountNo());
                    s.setCcy(e.getKey().getCurrency());
                    s.setAccountType(accountType.name());
                    s.setBalance(e.getValue());
                    s.setSnapshotTime(snapshotTime);
                    return s;
                })
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Token {
        private String accountNo;
        private String currency;

        private String combine() {
            return accountNo + SEPARATOR + currency;
        }
    }
}
