package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.common.dataobject.BonusOrder;
import cn.idachain.finance.batch.common.dataobject.InsuranceTrade;
import cn.idachain.finance.batch.common.dataobject.InvestInfo;
import cn.idachain.finance.batch.common.dataobject.RevenuePlan;
import cn.idachain.finance.batch.common.enums.*;
import cn.idachain.finance.batch.common.model.Product;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.service.util.DateUtil;
import cn.idachain.finance.batch.service.util.GenerateIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日启 2
 * 生成收益计划  于产品成立后，对成立的产品进行收益计算
 */
@Slf4j
@Service
public class B1002 extends BaseBatch {
    @Autowired
    private IRevenuePlanDao revenuePlanDao;
    @Autowired
    private IBonusOrderDao bonusOrderDao;
    @Autowired
    private IProductDao productDao;
    @Autowired
    private IInvestDao investDao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private IInsuranceTradeDao insuranceTradeDao;


    /**
     * 分红单生成
     * @throws Exception
     */
    public boolean execute() throws Exception {
        beforeExcute(BatchCode.B1002.getCode());
        if (!checkStatus()){
            return false;
        }

        //获取产品列表
        List<String> status = new ArrayList<String>();
        status.add(ProductStatus.OFF_SHELVE.getCode());
        List<Product> productList = productDao.getProductsByStatus(status,null);
        log.info("query product list for status off shelve,list :{}",productList);
        //分产品打捞投资记录
        for (Product product : productList){
            if (ProductType.FINANCING.getCode().equals(product.getProductType())){
                financingProduct(product);
            }
            if (ProductType.SUBSCRIBE.getCode().equals(product.getProductType())){
                subscribeProduct(product);
            }
        }

        afterExecute();
        return true;
    }

    /**
     * 理财产品
     * @param product
     */
    private void financingProduct(Product product){
        if (InterestMode.PRECYCLE.getCode().equals(product.getInterestMode())){

            List<InvestInfo> investInfos = investDao.selectInvestRecordForBatch(product.getProductNo(),
                    BizType.INVEST.getCode(), InvestStatus.INVEST_SUCCESS.getCode());
            log.info("query invest list for product {} ,list :{}",product.getProductNo(),investInfos);
            for (final InvestInfo investInfo:investInfos){
                //生成收益计划
                BigDecimal totalInterest = investInfo.getAmount().multiply(product.getProfitScale())
                        .setScale(2,RoundingMode.HALF_UP);
                final RevenuePlan revenuePlan = convertToRevenuePlan(investInfo,totalInterest,product);
                log.info("generate plan {} for invest {} ",revenuePlan,investInfo);
                investInfo.setPlanNo(revenuePlan.getPlanNo());

                //生成分红记录
                final List<BonusOrder> bonusOrders = new ArrayList<BonusOrder>();
                for (int i = 1;i<=product.getInterestCycle();i++){
                    //金额四舍五入 已和产品确认
                    Date bonusDate = DateUtil.offsiteDay(product.getValueDate(),
                            (TimeUnit.getByCode(product.getCycleType()).getDay()*i)-1);
                    BigDecimal amount = totalInterest.divide(
                            BigDecimal.valueOf(product.getInterestCycle()),
                            2,RoundingMode.HALF_UP);
                    if (i == product.getInterestCycle()){
                        amount = totalInterest.subtract(
                                amount.multiply(new BigDecimal(i-1)))
                                .setScale(2,RoundingMode.HALF_UP);
                    }

                    BonusOrder order = convertToBonusOrder(investInfo, (long) i, amount, bonusDate);

                    bonusOrders.add(order);
                }
                log.info("generate bonus orders list :{} ",bonusOrders);
                //保险生效
                final InsuranceTrade insuranceTrade = insuranceTradeDao.getTradeByInvestNo(
                        investInfo.getTradeNo(),InsuranceTradeStatus.INIT.getCode());
                log.info("query insurance trade :{} ",insuranceTrade);

                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        revenuePlanDao.saveRevenuePlan(revenuePlan);
                        investDao.updateInvestInfoStatusByObj(investInfo,InvestStatus.GIVE_OUT.getCode());
                        if (null != insuranceTrade) {
                            insuranceTradeDao.updateInsuranceTradeStatusByObj(insuranceTrade,
                                    InsuranceTradeStatus.PREPARE.getCode());
                        }
                        for (BonusOrder order: bonusOrders){
                            bonusOrderDao.saveBonusOrder(order);
                        }
                    }
                });
            }
            log.info("generate bonus finish fro product :{}",product.getProductNo());
            productDao.updateProductByObj(product,ProductStatus.LOCK_IN.getCode());

        }
    }

    /**
     * 认购产品
     * @param product
     */
    private void subscribeProduct(Product product){
        //获取投资记录
        List<InvestInfo> investInfos = investDao.selectInvestRecordForBatch(product.getProductNo(),
                BizType.INVEST.getCode(), InvestStatus.INVEST_SUCCESS.getCode());
        log.info("query invest list for product {} ,list :{}",product.getProductNo(),investInfos);
        for (final InvestInfo investInfo:investInfos){
            //换算还款金额
            BigDecimal paybackAmount = BigDecimal.ZERO;
            paybackAmount = investInfo.getAmount().multiply(product.getSubscribedAmount());
            // 计算应发收益
            BigDecimal totalInterest = investInfo.getAmount().multiply(product.getProfitScale())
                    .setScale(2,RoundingMode.HALF_UP);

            RevenuePlan revenuePlan = convertToRevenuePlan(investInfo,totalInterest,product);
            revenuePlan.setPrincipal(paybackAmount);
            revenuePlan.setActualPrincipal(paybackAmount);
            revenuePlan.setCcy(product.getSubscribedCcy());
            investInfo.setPlanNo(revenuePlan.getPlanNo());

            //生成分红记录
            final List<BonusOrder> bonusOrders = new ArrayList<BonusOrder>();
            //第0期收益率是否为空
            BigDecimal primarAmount = BigDecimal.ZERO;
            if (product.getPrimaryRate().compareTo(BigDecimal.ZERO)>0){
                // 生成第0期收益
                primarAmount = investInfo.getAmount().multiply(product.getPrimaryRate())
                        .setScale(2,RoundingMode.HALF_UP);
                Date primarDate = product.getPrimaryDate();
                BonusOrder primaryBonus = convertToBonusOrder(investInfo,
                        Long.parseLong("0"),primarAmount,primarDate);
                bonusOrders.add(primaryBonus);
                revenuePlan.setInterest(totalInterest.add(primarAmount));
                revenuePlan.setActualInterest(totalInterest.add(primarAmount));
            }


            // 尾期是否计息
            int terms = product.getInterestCycle().intValue();
            if (!Boolean.parseBoolean(product.getLastInterest().toString())){
                terms = terms - 1;
            }
            for (int i = 1;i<=terms;i++){
                Date bonusDate = DateUtil.offsiteDay(product.getValueDate(),
                        (TimeUnit.getByCode(product.getCycleType()).getDay()*i)-1);
                BigDecimal amount = totalInterest.divide(
                        BigDecimal.valueOf(terms), 2, RoundingMode.HALF_UP);
                if (i == terms){
                    amount = totalInterest.subtract(
                            amount.multiply(new BigDecimal(i-1)))
                            .setScale(2,RoundingMode.HALF_UP);
                }

                BonusOrder order = convertToBonusOrder(investInfo,(long)i,amount,bonusDate);
                bonusOrders.add(order);
            }
            log.info("generate bonus orders list :{} ",bonusOrders);

            // 记录入库
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    revenuePlanDao.saveRevenuePlan(revenuePlan);
                    investDao.updateInvestInfoStatusByObj(investInfo,InvestStatus.GIVE_OUT.getCode());
                    for (BonusOrder order: bonusOrders){
                        bonusOrderDao.saveBonusOrder(order);
                    }
                }
            });
        }
        log.info("generate bonus finish fro product :{}",product.getProductNo());
        productDao.updateProductByObj(product,ProductStatus.LOCK_IN.getCode());
    }

    private RevenuePlan convertToRevenuePlan(InvestInfo investInfo, BigDecimal totalInterest,Product product){
        //生成收益计划
        RevenuePlan revenuePlan = new RevenuePlan();
        revenuePlan.setPlanNo(Long.toString(GenerateIdUtil.getId(GenerateIdUtil.ModuleEnum.REVENUEPLAN)));
        revenuePlan.setInvestNo(investInfo.getTradeNo());
        revenuePlan.setProductNo(investInfo.getProductNo());
        revenuePlan.setCustomerNo(investInfo.getCustomerNo());
        revenuePlan.setPrincipal(investInfo.getAmount());
        revenuePlan.setActualPrincipal(investInfo.getAmount());
        revenuePlan.setInterest(totalInterest);
        revenuePlan.setActualInterest(totalInterest);
        revenuePlan.setInterestType(InterestType.BONUS.getCode());
        revenuePlan.setStatus(PlanStatus.INIT.getCode());
        revenuePlan.setPayingInterest(new BigDecimal(0));
        revenuePlan.setEffectiveDate(new Date(System.currentTimeMillis()));
        revenuePlan.setLastBonusDate(new Date(System.currentTimeMillis()));
        revenuePlan.setCreateTime(new Date(System.currentTimeMillis()));
        revenuePlan.setModifiedTime(new Date(System.currentTimeMillis()));
        revenuePlan.setPaybackDate(product.getDueDate());
        revenuePlan.setCcy(product.getCcy());
        return revenuePlan;
    }

    private BonusOrder convertToBonusOrder(InvestInfo investInfo,Long terms,BigDecimal amount,Date bonusDate){
        //生成分红单
        BonusOrder bonusOrder = new BonusOrder();
        bonusOrder.setTradeNo(Long.toString(GenerateIdUtil.getId(GenerateIdUtil.ModuleEnum.BONUS)));
        bonusOrder.setCustomerNo(investInfo.getCustomerNo());
        bonusOrder.setInvestNo(investInfo.getTradeNo());
        bonusOrder.setPlanNo(investInfo.getPlanNo());
        bonusOrder.setPeriods(terms);
        bonusOrder.setCcy(investInfo.getCcy());
        bonusOrder.setAmount(amount);
        bonusOrder.setStatus(BonusStatus.INIT.getCode());
        bonusOrder.setBonusDate(bonusDate);
        bonusOrder.setCreateTime(new Date(System.currentTimeMillis()));
        bonusOrder.setModifiedTime(new Date(System.currentTimeMillis()));
        bonusOrder.setProductNo(investInfo.getProductNo());

        return bonusOrder;
    }
}
