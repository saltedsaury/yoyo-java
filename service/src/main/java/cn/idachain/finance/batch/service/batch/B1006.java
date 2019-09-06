package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.common.enums.*;
import cn.idachain.finance.batch.common.dataobject.InsuranceInfo;
import cn.idachain.finance.batch.common.dataobject.InsuranceTrade;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.service.service.IBalanceDetialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class B1006 extends BaseBatch {

    @Autowired
    private IInsuranceInfoDao insuranceInfoDao;
    @Autowired
    private IInsuranceTradeDao insuranceTradeDao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private IBalanceDetialService balanceDetialService;

    /**
     * 保险产品逾期
     * @throws Exception
     */
    public boolean execute(){
        log.info("Batch 1006 begin.");
        beforeExcute(BatchCode.B1006.getCode());
        if (!checkStatus()){
            return false;
        }

        Date currentDate = new Date(System.currentTimeMillis());
        //获取到理赔截至日启的保险产品
        List<InsuranceInfo> products = insuranceInfoDao.getInsuranceByDate(currentDate);
        log.info("do batch B1006 on date :{} for insurance product list:{}",currentDate.toString(),products);
        for (InsuranceInfo info : products){
            List<InsuranceTrade> insuranceTrades = insuranceTradeDao.selectInsuranceTradeByCustomer(
                    null,info.getInsuranceNo());
            log.info("get insurance trade need finish for product {},trade list :{},size:{}",
                    info.getInsuranceNo(),insuranceTrades,insuranceTrades.size());
            for (InsuranceTrade trade :insuranceTrades){
                //账务解冻金额
                if(balanceDetialService.giveUpCompensation(trade.getTradeNo(),
                        trade.getTradeNo(),trade.getCustomerNo(),
                        info.getTransactionPairs().split(":")[0])) {

                    log.info("compensate asset unfreeze success:{}", trade.getTradeNo());
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            insuranceTradeDao.updateInsuranceTradeStatusByObj(trade,
                                    InsuranceTradeStatus.FINISH.getCode());
                            insuranceTradeDao.updateInsuranceSubStatusByObj(trade,
                                    InsuranceTradeSubStatus.BE_OVERDUE.getCode());
                        }
                    });
                }
            }
        }


        afterExecute();
        log.info("Batch 1006 end.");
        return true;
    }
}
