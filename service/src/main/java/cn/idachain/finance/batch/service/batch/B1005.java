package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.enums.*;
import cn.idachain.finance.batch.common.model.Product;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.service.service.IBalanceDetialService;
import cn.idachain.finance.batch.service.service.IInsuranceInfoService;
import cn.idachain.finance.batch.service.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 日终 3
 * 到期还本，到期还本处理
 */
@Slf4j
@Service
public class B1005 extends BaseBatch {
    @Autowired
    private IInvestDao investDao;
    @Autowired
    private IRevenuePlanDao revenuePlanDao;
    @Autowired
    private IBonusOrderDao bonusOrderDao;
    @Autowired
    private IInsuranceTradeDao insuranceTradeDao;
    @Autowired
    private IInsuranceInfoService insuranceInfoService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private IProductDao productDao;
    @Autowired
    private IBalanceDetialService balanceDetialService;
    @Autowired
    private IExchangeRateDao exchangeRateDao;

    /**
     * 到期还本
     * @throws Exception
     */
    public boolean execute() throws Exception {
        log.info("Batch 1005 begin.");
        beforeExcute(BatchCode.B1005.getCode());
        if (!checkStatus()){
            return false;
        }
        List<String> status = new ArrayList<String>();
        status.add(ProductStatus.OPEN.getCode());
        List<Product> products = productDao.getProductsByStatus(status,null);//需修改 ，查询开放日的产品
        log.info("query product list for status open,list :{}",products);
        //分产品打捞投资记录
        for (Product product : products){
            List<RevenuePlan> records =  revenuePlanDao.selectPlanForBatch(
                    product.getProductNo(),PlanStatus.INIT.getCode());//按产品，状态 查询收益计划
            log.info("query revenue plan for product {},list:{}",product.getProductNo(),records);
            for (final RevenuePlan record : records){
                //分红记录是否全部完成
                int expectCount = product.getInterestCycle().intValue();
                if (BoolType.FALSE.getCode().equals(product.getLastInterest().toString())){
                    expectCount = expectCount - 1;
                }
                if (product.getPrimaryRate().compareTo(BigDecimal.ZERO) > 0){
                    expectCount = expectCount + 1;
                }
                int terms = bonusOrderDao.countBonusByPlanAndStatus(record.getPlanNo(),BonusStatus.FINISH.getCode());
                if(terms != expectCount){
                    log.error("bonus haven't finished，plan_no：{}",record.getPlanNo());
                    //增加报警信息
                    continue;
                }
                //获取投资记录
                final InvestInfo investInfo = investDao.selectInvestInfoByTradeNoAndStatus(record.getInvestNo(),
                        InvestStatus.GIVE_OUT.getCode(),record.getCustomerNo());
                log.info("query invest info over due :{}",investInfo);
                //获取投保记录
                final InsuranceTrade insuranceTrade = insuranceTradeDao.getTradeByInvestNo(
                        investInfo.getTradeNo(),InsuranceTradeStatus.PREPARE.getCode());
                log.info("query insurance trade prepared :{}",insuranceTrade);
                if (!BlankUtil.isBlank(insuranceTrade)){
                    //获取保险产品
                    InsuranceInfo insuranceInfo = insuranceInfoService.getInsuranceInfoBy(
                            insuranceTrade.getInsuranceNo());
                    //更新投保记录的索赔截至时间
                    int date = insuranceInfo.getTimeLimit() * TimeUnit.getByCode(insuranceInfo.getLimitUnit()).getDay();
                    insuranceTrade.setCompensateEnd(DateUtil.offsiteDay(product.getDueDate(),date));

                    //获取到期兑换比例
                    BigDecimal rate = insuranceInfo.getCompensation();
                    //获取当前实际兑换比例
                    BigDecimal currentRate = exchangeRateDao
                            .getCurrentRateByPairs(insuranceInfo.getTransactionPairs(),product.getProductNo()).getRate();

                    //当前实际兑换比例 < 保值兑换比例
                    if(currentRate.compareTo(rate)<0){
                        //保险生效
                        //调用account进行赎回
                        log.info("pay principal with insurance,planNo:{}",record.getPlanNo());
                        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                                balanceDetialService.payPrincipal(record.getCustomerNo(),
                                        record.getCcy(),record.getPrincipal(),
                                        record.getPlanNo(),insuranceTrade.getTradeNo(),
                                        true,investInfo.getProductNo());
                                insuranceTradeDao.updateInsuranceTradeStatusByObj(insuranceTrade,
                                        InsuranceTradeStatus.WAIT_COMPENSATION.getCode());
                                insuranceTradeDao.updateInsuranceSubStatusByObj(insuranceTrade,
                                        InsuranceTradeSubStatus.NO_APPLICATION.getCode());
                                investDao.updateInvestInfoStatusByObj(investInfo,InvestStatus.OVER_DUE.getCode());
                                record.setPaidTime(System.currentTimeMillis());
                                revenuePlanDao.updatePlanStatusByObj(record,PlanStatus.FINISH.getCode());
                            }
                        });

                    }else{
                        //保险不生效
                        //调用account进行赎回
                        log.info("pay principal without insurance,insurance not effective,planNo:{}",record.getPlanNo());
                        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                            balanceDetialService.payPrincipal(record.getCustomerNo(),
                                    record.getCcy(),record.getPrincipal(),
                                    record.getPlanNo(),null,false,investInfo.getProductNo());
                            insuranceTradeDao.updateInsuranceTradeStatusByObj(
                                    insuranceTrade, InsuranceTradeStatus.FINISH.getCode());
                            investDao.updateInvestInfoStatusByObj(investInfo,InvestStatus.OVER_DUE.getCode());
                            record.setPaidTime(System.currentTimeMillis());
                            revenuePlanDao.updatePlanStatusByObj(record,PlanStatus.FINISH.getCode());
                            }
                        });
                    }
                }else{
                    //未购买保险
                    //调用account进行赎回
                    log.info("pay principal without insurance,planNo:{}",record.getPlanNo());
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            balanceDetialService.payPrincipal(record.getCustomerNo(),
                                    record.getCcy(),record.getPrincipal(),
                                    record.getPlanNo(),null,false,investInfo.getProductNo());
                            investDao.updateInvestInfoStatusByObj(investInfo,InvestStatus.OVER_DUE.getCode());
                            record.setPaidTime(System.currentTimeMillis());
                            revenuePlanDao.updatePlanStatusByObj(record,PlanStatus.FINISH.getCode()); //
                        }
                    });
                }

            }
        }
        afterExecute();
        log.info("Batch 1005 end.");
        return true;
    }

}
