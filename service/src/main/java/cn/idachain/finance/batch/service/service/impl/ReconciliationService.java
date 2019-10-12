package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.enums.AccountType;
import cn.idachain.finance.batch.common.enums.Direction;
import cn.idachain.finance.batch.common.enums.TransferOrderStatus;
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

    private static final long RANGE = 1000L * 60 * 60;
    private static final String IN_CODE = Direction.IN.getCode();

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void buildBalanceSnapshot() {
        log.info("[build balance snapshot task]start to build balance snapshot.");
        Long lastId = transferOrderDao.lastId();
        List<RecBalanceSnapshot> snapshots = balanceSnapshotDao.lastSnapshot();
        long startTime = snapshots.stream().mapToLong(RecBalanceSnapshot::getSnapshotTime).findAny().orElse(0L);
        // success orders + processing orders
        List<TransferOrder> orders = transferOrderDao.getOrderByRange(startTime, lastId);

        Map<String, RecBalanceSnapshot> snapshotMap = snapshots.stream()
                .collect(Collectors.toMap(RecBalanceSnapshot::getCurrency, Function.identity()));

        // 1.在时间阈值前的订单需要全部确认为成功；出入金需要汇总
        List<String> notSuccessOrdersBeforeLatch = new ArrayList<>();
        long snapshotLatch = System.currentTimeMillis() - RANGE;
        Map<Boolean, List<TransferOrder>> parts = orders.stream()
                .collect(Collectors.partitioningBy(o -> {
                    if (o.getTransferTime() != null) {
                        return o.getTransferTime() <= snapshotLatch;
                    }
                    assert !TransferOrderStatus.SUCCESS.getCode().equals(o.getStatus());
                    if (o.getChargeTime() < snapshotLatch) {
                        notSuccessOrdersBeforeLatch.add(o.getOrderNo());
                    }
                    return false;
                }));

        // 1.1 未及时完成的订单信息输出
        if (!notSuccessOrdersBeforeLatch.isEmpty()) {
            log.error("[build balance snapshot task]some orders before {} are not success yet: {}",
                    RANGE, notSuccessOrdersBeforeLatch);
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
        log.info("[build balance snapshot task]transfer order before {} are all succeed.", snapshotLatch);

        // 2.外部确认的出入金 + 处理中资金 == balanceInternal
        parts.get(Boolean.TRUE).forEach(o -> {
            // below == o.getChargeTime != null
            if (!TransferOrderStatus.SUCCESS.getCode().equals(o.getStatus())) {
                notSuccessOrdersBeforeLatch.add(o.getOrderNo());
                return;
            }
            mergeSnapshotInfo(snapshotMap, o);
        });

        Map<String, BigDecimal> internalAmountDelta = snapshotMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().getInAmount().subtract(e.getValue().getOutAmount())
        ));
        parts.get(Boolean.FALSE).forEach(o ->
                internalAmountDelta.merge(
                        o.getCcy(),
                        Direction.IN.getCode().equals(o.getDeriction()) ? o.getAmount() : o.getAmount().negate(),
                        BigDecimal::add
                ));
        List<BalanceInternal> internalList = balanceInternalDao.getAllBalance();
        Map<String, BigDecimal> errCcy = internalList.stream()
                .filter(i -> internalAmountDelta.getOrDefault(i.getCurrency(), BigDecimal.ZERO)
                        .compareTo(i.getBalance()) != 0)
                .collect(Collectors.toMap(BaseBalance::getCurrency, BaseBalance::getBalance));
        if (!errCcy.isEmpty()) {
            errCcy.forEach((k, v) ->
                    log.error("ccy {} balance is {}, but expected {}",
                            k, v, internalAmountDelta.getOrDefault(k, BigDecimal.ZERO)));
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
        log.info("[build balance snapshot task]balance snapshot amount is equal to internal balance.");

        // 3.更新 snapshotTime 保存新快照
        snapshotMap.values().forEach(s -> s.setSnapshotTime(snapshotLatch));
        balanceSnapshotDao.insertSnapshotBatch(snapshotMap.values());
        log.info("[build balance snapshot task]snapshot build succeed.");
    }

    private void mergeSnapshotInfo(Map<String, RecBalanceSnapshot> snapshotMap, TransferOrder order) {
        boolean in = Direction.IN.getCode().equals(order.getDeriction());
        RecBalanceSnapshot snapshot = snapshotMap.get(order.getCcy());
        if (snapshot != null) {
            if (in) {
                snapshot.setInAmount(snapshot.getInAmount().add(order.getAmount()));
            } else {
                snapshot.setOutAmount(snapshot.getOutAmount().add(order.getAmount()));
            }
            return;
        }
        snapshotMap.put(order.getCcy(), new RecBalanceSnapshot()
                .setCurrency(order.getCcy())
                .setInAmount(in ? order.getAmount() : BigDecimal.ZERO)
                .setOutAmount(in ? BigDecimal.ZERO : order.getAmount()));
    }

    /**
     * order == balance
     * internal == org + person
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkOrderDetail() {
        log.info("[check order balance task]start to check orders and balance.");
        Long nowTime = System.currentTimeMillis() - 60 * 1000L;
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
            log.info("[check order balance task]reconcile success! no fresh orders ");
            return;
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
            log.info("[check order balance task]new account snapshots are inserting.");
            // 插入新的快照
            accountSnapshotDao.insertBatch(buildSnapshot(internalAmount, AccountType.INTERNAL, nowTime));
            accountSnapshotDao.insertBatch(buildSnapshot(personAmount, AccountType.PERSON, nowTime));
            accountSnapshotDao.insertBatch(buildSnapshot(orgAmount, AccountType.ORG, nowTime));
            log.info("[check order balance task]detail check success");
            return;
        }
        log.error("[check order balance task]orders info are not match with balance");
        throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
    }

    private void checkBalanceInternal(Map<Token, BigDecimal> internal, List<Map<Token, BigDecimal>> others) {
        Map<String, BigDecimal> target = new HashMap<>(internal.size() << 1);
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
                log.error("[check order balance task]internal balance check failed! ccy: {}, delta: {}",
                        entry.getKey(), entry.getValue());
            }
        }
        if (err) {
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
        log.info("[check order balance task]check balance internal success!");
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
    }

}
