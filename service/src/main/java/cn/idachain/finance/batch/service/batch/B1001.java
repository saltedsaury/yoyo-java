package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.common.dataobject.InvestInfo;
import cn.idachain.finance.batch.common.enums.*;
import cn.idachain.finance.batch.common.model.Product;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.util.DateUtil;
import cn.idachain.finance.batch.service.dao.IInvestDao;
import cn.idachain.finance.batch.service.dao.IProductDao;
import cn.idachain.finance.batch.service.service.IBalanceDetialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日启 1
 * 产品成立  于起息日0点执行
 */
@Slf4j
@Service
public class B1001 extends BaseBatch{
    @Autowired
    private IProductDao productDao;
    @Autowired
    private IInvestDao investDao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private IBalanceDetialService balanceDetialService;

    public boolean execute() throws Exception {
        beforeExcute(BatchCode.B1001.getCode());
        if (!checkStatus()){
            return false;
        }
        //获取产品列表
        List<String> status = new ArrayList<String>();
        status.add(ProductStatus.FOR_SALE.getCode());
        status.add(ProductStatus.PAUSE.getCode());
        status.add(ProductStatus.OFF_SHELVE.getCode());
        List<Product> productList = productDao.getProductsByStatus(status,null);
        log.info("query product list for status for_sale/pause ,list :{}",productList);
        for(final Product product : productList){
            //剩余投资金额为0，产品成立
            if (new BigDecimal("0").compareTo(product.getSurplusAmount())==0){
                log.info("product :{} off shelve due to surplus amount is zero.",product);
                if(investConfirm(product)) { //申购确认全部完成，更新产品状态
                    final Date valueDate = new Date();
                    int investCycle = (product.getInterestCycle().intValue()
                            * TimeUnit.getByCode(product.getCycleType()).getDay())-1;
                    final Date dueDate = DateUtil.offsiteDay(valueDate, investCycle);

                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            productDao.updateProductByObj(product, ProductStatus.OFF_SHELVE.getCode());
                            productDao.updateProductValueDate(product,valueDate,dueDate);
                        }
                    });
                }
                continue;
            }
            //到达原定起息日，产品成立
            if(!BlankUtil.isBlank(product.getValueDate())){
                log.info("product :{} off shelve due to arriving at value date.",product);
                if(new Date(System.currentTimeMillis()).compareTo(product.getValueDate())>0){
                    if(investConfirm(product)) { //申购确认全部完成，更新产品状态
                        productDao.updateProductByObj(product, ProductStatus.OFF_SHELVE.getCode());
                    }
                }
            }
        }

        afterExecute();
        return true;
    }

    private boolean investConfirm(Product product){
        boolean finishFlag = true;
        List<InvestInfo> investInfos = investDao.selectInvestRecordForBatch(product.getProductNo(),
                BizType.INVEST.getCode(), InvestStatus.APPLY_SUCCESS.getCode());
        for (InvestInfo info : investInfos){
            try{
                //调用account解冻扣款
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        balanceDetialService.invest(info.getTradeNo());
                        log.info("invest charged amount success,invest info:{}",info);
                        investDao.updateInvestInfoStatusByObj(info, InvestStatus.INVEST_SUCCESS.getCode());

                    }
                });
            }catch (Exception e){
                log.error("do invest confirm failed, investNo:{}",info.getTradeNo());
                finishFlag = false;
            }
        }

        return finishFlag;
    }
}
