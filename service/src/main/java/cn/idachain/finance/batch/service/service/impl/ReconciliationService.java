package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.enums.AccountType;
import cn.idachain.finance.batch.common.enums.Direction;
import cn.idachain.finance.batch.common.enums.TransferOrderStatus;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.service.service.IReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author kun
 * @version 2019/10/9 17:49
 */
@Service
public class ReconciliationService implements IReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);

    private static final long CHECK_RANGE = 1000L * 60 * 10;
    private static final long PACK_RANGE = CHECK_RANGE + 1000L * 60 * 10;
    private static final String IN_CODE = Direction.IN.getCode();

    @Autowired
    private IRecBalanceSnapshotDao balanceSnapshotDao;
    @Autowired
    private IBalanceInternalDao balanceInternalDao;
    @Autowired
    private ITransferOrderDao transferOrderDao;
    @Autowired
    private IRevenuePlanDao revenuePlanDao;
    @Autowired
    private IInvestDao investDao;
    @Autowired
    private ICompensateTradeDao compensateTradeDao;
    @Autowired
    private IBonusOrderDao bonusOrderDao;
    @Autowired
    private IRedemptionTradeDao redemptionTradeDao;
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
        List<TransferOrder> orders = transferOrderDao.getOrderByRange(startTime, lastId);

        // 1.在时间阈值前的订单需要全部确认为成功；出入金需要汇总
        List<String> notSuccessOrdersBeforeLatch = new ArrayList<>();
        long checkLatch = System.currentTimeMillis() - CHECK_RANGE;
        long packLatch = System.currentTimeMillis() - PACK_RANGE;
        Map<Boolean, List<TransferOrder>> parts = orders.stream()
                .peek(o -> {
                    // check orders not succeed
                    if (o.getCreateTime().getTime() < checkLatch &&
                            !TransferOrderStatus.SUCCESS.getCode().equals(o.getStatus())) {
                        notSuccessOrdersBeforeLatch.add(o.getOrderNo());
                    }
                })
                .collect(Collectors.partitioningBy(o ->
                        o.getTransferTime() != null && o.getTransferTime() <= packLatch));

        // 1.1 未及时完成的订单信息输出
        if (!notSuccessOrdersBeforeLatch.isEmpty()) {
            log.error("[build balance snapshot task]some orders before {} are not success yet: {}",
                    checkLatch, notSuccessOrdersBeforeLatch);
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
        log.info("[build balance snapshot task]transfer order before {} are all succeed.", checkLatch);

        // 2.外部确认的出入金 + 处理中资金 == balanceInternal
        Map<String, RecBalanceSnapshot> snapshotMap = snapshots.stream()
                .collect(Collectors.toMap(RecBalanceSnapshot::getCurrency, Function.identity()));
        parts.get(Boolean.TRUE).forEach(o -> mergeSnapshotInfo(snapshotMap, o));

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

        Map<String, BigDecimal> errCcy = new HashMap<>();
        balanceInternalDao.getAllCcyBalance().forEach((ccy, b) -> {
            if (internalAmountDelta.getOrDefault(ccy, BigDecimal.ZERO).compareTo(b) != 0) {
                errCcy.put(ccy, b);
            }
        });
        if (!errCcy.isEmpty()) {
            errCcy.forEach((k, v) ->
                    log.error("ccy {} balance is {}, but expected {}",
                            k, v, internalAmountDelta.getOrDefault(k, BigDecimal.ZERO)));
            throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
        log.info("[build balance snapshot task]balance snapshot amount is equal to internal balance.");

        // 3.更新 snapshotTime 保存新快照
        snapshotMap.values().forEach(s -> s.setSnapshotTime(packLatch));
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
    public boolean checkOrderDetail() {
        log.info("[check order balance task]start to check orders and balance.");
        Long lastId = balanceDetailDao.getLastId();

        // step three check detail
        // k: account:ccy, v: amount
        Map<String, Map<String, BigDecimal>> internalAmount = new HashMap<>(16);
        Map<String, Map<String, BigDecimal>> personAmount = new HashMap<>(1024);
        Map<String, Map<String, BigDecimal>> orgAmount = new HashMap<>(32);
        // 收集与上一次对账期间的订单
        List<BalanceDetail> transferDetails = balanceDetailDao.getDetailsByTransferToReconcile(lastId);
        List<BalanceDetail> bonusDetails = balanceDetailDao.getDetailsByBonusOrderToReconcile(lastId);
        List<BalanceDetail> revenueDetails = balanceDetailDao.getDetailsByRevenuePlanToReconcile(lastId);
        List<BalanceDetail> compensationDetails = balanceDetailDao.getDetailsByCompensationToReconcile(lastId);
        List<BalanceDetail> investDetails = balanceDetailDao.getDetailsByInvestInfoToReconcile(lastId);
        List<BalanceDetail> redemptionDetails = balanceDetailDao.getDetailsByRedemptionToReconcile(lastId);

        detailToMap(internalAmount, personAmount, orgAmount, Stream.of(transferDetails,
                bonusDetails, revenueDetails, compensationDetails, investDetails, redemptionDetails));

        // 比较 internal 余额和 person + org
        boolean checkSum = checkBalanceInternal(internalAmount, Arrays.asList(personAmount, orgAmount));

        // 确认预期值（前一快照结果 + 最近差值）是否等于实际余额
        List<String> accountList = Stream
                .of(personAmount, internalAmount, orgAmount)
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toList());
        List<RecAccountSnapshot> accountSnapshots = accountSnapshotDao.getSnapshotByAccounts(accountList);
        accountSnapshots.forEach(s ->
                choseMap(s.getAccountType(), internalAmount, personAmount, orgAmount)
                        .get(s.getAccount())
                        .computeIfPresent(s.getCcy(), (ccy, b) -> b.add(s.getBalance()))
        );

        boolean ok = checkSum &
                checkBalanceDetail(internalAmount, AccountType.INTERNAL) &
                checkBalanceDetail(personAmount, AccountType.PERSON) &
                checkBalanceDetail(orgAmount, AccountType.ORG);

        if (ok) {
            // 插入新的快照并标记已对账订单
            log.info("[check order balance task]new account snapshots are inserting.");
            transferOrderDao.markReconciled(transferDetails.stream().map(BalanceDetail::getTradeNo).collect(Collectors.toList()));
            revenuePlanDao.markReconciled(revenueDetails.stream().map(BalanceDetail::getTradeNo).collect(Collectors.toList()));
            compensateTradeDao.markReconciled(compensationDetails.stream().map(BalanceDetail::getTradeNo).collect(Collectors.toList()));
            redemptionTradeDao.markReconciled(redemptionDetails.stream().map(BalanceDetail::getTradeNo).collect(Collectors.toList()));
            investDao.markReconciled(investDetails.stream().map(BalanceDetail::getTradeNo).collect(Collectors.toList()));
            bonusOrderDao.markReconciled(bonusDetails.stream().map(BalanceDetail::getTradeNo).collect(Collectors.toList()));

            long nowTime = System.currentTimeMillis();
            accountSnapshotDao.insertBatch(buildSnapshot(internalAmount, AccountType.INTERNAL, nowTime));
            accountSnapshotDao.insertBatch(buildSnapshot(personAmount, AccountType.PERSON, nowTime));
            accountSnapshotDao.insertBatch(buildSnapshot(orgAmount, AccountType.ORG, nowTime));
            log.info("[check order balance task]detail check success");
            return true;
        }
        log.error("[check order balance task]orders info are not match with balance");
        return false;
    }

    private void detailToMap(Map<String, Map<String, BigDecimal>> internalAmount,
                             Map<String, Map<String, BigDecimal>> personAmount,
                             Map<String, Map<String, BigDecimal>> orgAmount,
                             Stream<List<BalanceDetail>> transferDetails) {
        transferDetails.flatMap(List::stream).forEach(d -> {
            Map<String, Map<String, BigDecimal>> map =
                    choseMap(d.getAccountType(), internalAmount, personAmount, orgAmount);
            map.computeIfAbsent(d.getAccountNo(), k -> new HashMap<>()).merge(
                    d.getCurrency(),
                    IN_CODE.equals(d.getTransType()) ? d.getAmount() : d.getAmount().negate(),
                    BigDecimal::add);
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean checkTotalBalance() {
        // check sum
        Map<String, BigDecimal> personCcyBalance = balancePersonDao.getAllCcyBalance();
        Map<String, BigDecimal> internalCcyBalance = balanceInternalDao.getAllCcyBalance();
        Map<String, BigDecimal> orgCcyBalance = balanceOrgDao.getAllCcyBalance();
        boolean totalBalanceErr = false;
        for (Map.Entry<String, BigDecimal> entry : internalCcyBalance.entrySet()) {
            String ccy = entry.getKey();
            BigDecimal actual = personCcyBalance.getOrDefault(ccy, BigDecimal.ZERO)
                    .add(orgCcyBalance.getOrDefault(ccy, BigDecimal.ZERO));
            if (entry.getValue().compareTo(actual) == 0) {
                continue;
            }
            totalBalanceErr = true;
            log.error("[check order balance task]check total balance failed. ccy {}, except {}, actual {}.",
                    ccy, entry.getValue(), actual);
        }
        if (totalBalanceErr) {
            return false;
        }
        log.info("[check order balance task]check total balance succeed.");
        return true;
    }

    private boolean checkBalanceInternal(Map<String, Map<String, BigDecimal>> internal,
                                         List<Map<String, Map<String, BigDecimal>>> others) {
        Map<String, BigDecimal> ccyBalance = internal.values().stream().map(Map::entrySet).flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, BigDecimal::add));

        others.stream().map(Map::values)
                .flatMap(Collection::stream)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(e -> ccyBalance.put(
                        e.getKey(),
                        ccyBalance.getOrDefault(e.getKey(), BigDecimal.ZERO).subtract(e.getValue())
                ));

        boolean err = false;
        for (Map.Entry<String, BigDecimal> entry : ccyBalance.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) != 0) {
                err = true;
                log.error("[check order balance task]internal balance check failed! ccy: {}, delta: {}",
                        entry.getKey(), entry.getValue());
            }
        }
        if (err) {
            return false;
        }
        log.info("[check order balance task]check balance internal success!");
        return true;
    }

    /**
     * 确认金额
     *
     * @param accountAmount key->account:ccy, value->balance. 预期值
     * @param accountType   账户类型
     * @return 正确否
     */
    private boolean checkBalanceDetail(Map<String, Map<String, BigDecimal>> accountAmount,
                                       AccountType accountType) {
        if (accountAmount == null || accountAmount.isEmpty()) {
            return true;
        }
        Set<String> accounts = accountAmount.keySet();

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
                throw new BizException(BizExceptionEnum.RECONCILE_FAILED);
        }
        long errCount = balanceList.stream().filter(b -> {
            BigDecimal actualBalance = accountAmount.get(b.getAccountNo()).get(b.getCurrency());
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

    private Map<String, Map<String, BigDecimal>> choseMap(String accountTypeCode,
                                                          Map<String, Map<String, BigDecimal>> internalAmount,
                                                          Map<String, Map<String, BigDecimal>> personAmount,
                                                          Map<String, Map<String, BigDecimal>> orgAmount) {
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
    private List<RecAccountSnapshot> buildSnapshot(Map<String, Map<String, BigDecimal>> accountAmount,
                                                   AccountType accountType,
                                                   Long snapshotTime) {
        List<RecAccountSnapshot> newSnapshots = new ArrayList<>();
        accountAmount.forEach((account, map) -> {
            map.forEach((ccy, balance) -> {
                RecAccountSnapshot s = new RecAccountSnapshot();
                s.setAccount(account);
                s.setCcy(ccy);
                s.setAccountType(accountType.name());
                s.setBalance(balance);
                s.setSnapshotTime(snapshotTime);
                newSnapshots.add(s);
            });
        });
        return newSnapshots;
    }

}
