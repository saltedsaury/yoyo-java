package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.common.dataobject.BonusOrder;
import cn.idachain.finance.batch.common.dataobject.RevenuePlan;
import cn.idachain.finance.batch.common.enums.BatchCode;
import cn.idachain.finance.batch.common.enums.BonusStatus;
import cn.idachain.finance.batch.common.enums.PlanStatus;
import cn.idachain.finance.batch.service.dao.IBonusOrderDao;
import cn.idachain.finance.batch.service.dao.IProductDao;
import cn.idachain.finance.batch.service.dao.IRevenuePlanDao;
import cn.idachain.finance.batch.service.service.IBalanceDetialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 日终 1
 * 分红 对待发放的分红进行登账发放
 */
@Slf4j
@Service
public class B1003 extends BaseBatch {

    @Autowired
    private IBonusOrderDao bonusOrderDao;
    @Autowired
    private IRevenuePlanDao revenuePlanDao;
    @Autowired
    private IProductDao productDao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private IBalanceDetialService balanceDetialService;


    /**
     * 分红
     * @throws Exception
     */
    public boolean execute() throws Exception {
        log.info("Batch 1003 begin.");
        beforeExcute(BatchCode.B1003.getCode());

        if (!this.checkStatus()){
            return false;
        }
        Date currentDate = new Date(System.currentTimeMillis());
        //打捞待处理的分红记录
        List<BonusOrder> orders = bonusOrderDao.selectBonusByStatus(currentDate,
                BonusStatus.PREPARE.getCode());  //按日期查
        log.info("do batch B1003 on date :{} for bonus list:{},list size:{}",
                currentDate.toString() ,orders,orders.size());
        for (final BonusOrder order : orders){
            final RevenuePlan revenuePlan = revenuePlanDao.selectPlanByNo(order.getPlanNo());
            log.info("pay bonus for plan :{}",revenuePlan);
            if (PlanStatus.REDEMPT.getCode().equals(revenuePlan.getStatus())){
                log.info("cancel bonus because of redeem ,orderNo:{}",order.getTradeNo());
                bonusOrderDao.updateBonusByStatus(order,BonusStatus.CANCEL.getCode());
                continue;
            }
            BigDecimal amount = revenuePlan.getPayingInterest().add(order.getAmount());
            revenuePlan.setLastBonusDate(order.getBonusDate());
            revenuePlan.setPayingInterest(amount);
            revenuePlan.setModifiedTime(new Date(System.currentTimeMillis()));

            //调用account接口记录分红
            //fixme  放一个事务
            if (balanceDetialService.payBonus(
                    order.getCustomerNo(),order.getCcy(),order.getAmount(),order.getTradeNo())) {
                log.info("pay bonus success,orderNo:{},amount:{}",order.getTradeNo(),order.getAmount());
                //分红成功修改状态和收益计划表
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        order.setModifiedTime(new Date(System.currentTimeMillis()));
                        bonusOrderDao.updateBonusByStatus(order, BonusStatus.FINISH.getCode());
                        revenuePlanDao.updatePlanById(revenuePlan);
                    }
                });
            }

        }

        afterExecute();
        log.info("Batch 1003 end.");
        return true;
    }

    @Override
    public boolean checkStatus(){
        /*if(!BatchStatus.PREPARE.getCode().equals(systemBatch.getStatus())){
            log.info("batch 1003 is not prepared.");
            return false;
        }*/
        return super.checkStatus();
    }
}
