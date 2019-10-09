package cn.idachain.finance.batch.task.task;

import cn.idachain.finance.batch.service.service.IReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kun
 * @version 2019/10/8 09:55
 */
@Component
public class ReconcileTask {

    private static final Logger log = LoggerFactory.getLogger(ReconcileTask.class);

    @Autowired
    private IReconciliationService reconciliationService;
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
     *
     * important: 需要rr级别，单个步骤分别开启事务，当前不能开启事务
     */
    @Scheduled(cron = "${task.financing.reconciliation}")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void reconcile() {
        reconciliationService.checkBalanceInternal();
        reconciliationService.checkBalanceSnapshot();
        reconciliationService.checkOrderDetail();
    }

}
