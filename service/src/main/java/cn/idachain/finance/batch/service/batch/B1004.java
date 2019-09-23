package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.enums.*;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.service.IBalanceDetialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * 日终 2
 * 提前赎回 打捞提前赎回申请单，进行登账并发放
 */
@Slf4j
@Service
public class B1004 extends BaseBatch {
    @Autowired
    private IInvestDao investDao;
    @Autowired
    private IRedemptionTradeDao redemptionTradeDao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private IInsuranceTradeDao insuranceTradeDao;
    @Autowired
    private IRevenuePlanDao revenuePlanDao;
    @Autowired
    private IBonusOrderDao bonusOrderDao;
    @Autowired
    private IBalanceDetialService balanceDetialService;

    /**
     * 提前赎回处理
     * @throws Exception
     */
    public boolean execute() throws Exception {
        log.info("Batch 1004 begin.");
        beforeExcute(BatchCode.B1004.getCode());
        if (!this.checkStatus()){
            return false;
        }
        //打捞待处理的提前赎回单
        List<RedemptionTrade> trades = redemptionTradeDao.selectRedemptionByStatus(
                null, RedemptionStatus.REDEEMING.getCode()); //状态
        log.info("do batch B1004 for redemption list :{}",trades);
        for(final RedemptionTrade trade : trades){
            //获取投资记录
            final InvestInfo investInfo = investDao.selectInvestInfoByTradeNoAndStatus(trade.getInvestNo(),
                    InvestStatus.REDEEMING.getCode(),trade.getCustomerNo());
            log.info("query invest info :{}",investInfo);
            final RevenuePlan revenuePlan = revenuePlanDao.selectPlanByNo(investInfo.getPlanNo());
            log.info("query revenue plan :{}",revenuePlan);
            revenuePlan.setActualPrincipal(trade.getAmount().subtract(trade.getFine()).subtract(trade.getBonus()));
            revenuePlan.setActualInterest(BigDecimal.ZERO);

            //修改提前赎回记录状态 以及 投资记录状态 防止自动赎回流程重复捞单
            //调用account接口记赎回
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    balanceDetialService.redemption(trade.getCustomerNo(),trade.getCcy(),
                            trade.getTradeNo(),trade.getAmount(),trade.getFine(),trade.getBonus());
                    log.info("pay redemption success,orderNo:{},amount:{}",trade.getTradeNo(),trade.getAmount());
                    investDao.updateInvestInfoStatusByObj(investInfo, InvestStatus.ALREADY_REDEEMED.getCode());
                    redemptionTradeDao.updateTradeStatusByObj(trade, RedemptionStatus.FINISH.getCode());
                    revenuePlanDao.updatePlanStatusByObj(revenuePlan, PlanStatus.ALREADY_REDEEMED.getCode());
                    //购买保险产品需将保险产品失效
                    InsuranceTrade insuranceTrade = insuranceTradeDao.getTradeByInvestNo(
                            investInfo.getTradeNo(), InsuranceTradeStatus.INIT.getCode());
                    if (!BlankUtil.isBlank(insuranceTrade)) {
                        insuranceTradeDao.updateInsuranceTradeStatusByObj(insuranceTrade,
                                InsuranceTradeStatus.FINISH.getCode());
                    }
                    //分红记录失效
                    BonusOrder bonusOrder = new BonusOrder();
                    bonusOrder.setInvestNo(investInfo.getTradeNo());
                    bonusOrder.setStatus(BonusStatus.INIT.getCode());
                    bonusOrderDao.updateBonusByStatus(bonusOrder, BonusStatus.CANCEL.getCode());
                }
            });

        }

        afterExecute();
        log.info("Batch 1004 end.");
        return true;
    }

    @Override
    public boolean checkStatus(){
        //待审核的提前赎回单
        /*List<RedemptionTrade> trades = redemptionTradeDao.selectRedemptionByStatus(
                null,RedemptionStatus.AUDITING.getCode()); //状态
        if (BlankUtil.isBlank(trades)){
            log.info("no early redemption orders auditing.");
            return true;
        }
        if(!BatchStatus.PREPARE.getCode().equals(systemBatch.getStatus())){
            log.info("batch 1004 is not prepared.");
            return false;
        }*/
        return super.checkStatus();
    }
}
