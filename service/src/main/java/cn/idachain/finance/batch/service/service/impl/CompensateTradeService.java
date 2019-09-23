package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.CompensateTrade;
import cn.idachain.finance.batch.common.dataobject.InsuranceTrade;
import cn.idachain.finance.batch.common.dataobject.InvestInfo;
import cn.idachain.finance.batch.common.dataobject.RevenuePlan;
import cn.idachain.finance.batch.common.enums.CompensationStatus;
import cn.idachain.finance.batch.common.enums.InsuranceTradeStatus;
import cn.idachain.finance.batch.common.enums.InsuranceTradeSubStatus;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.service.service.IBalanceDetialService;
import cn.idachain.finance.batch.service.service.ICompensateTradeService;
import cn.idachain.finance.batch.service.service.IInsuranceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Service
public class CompensateTradeService extends BaseService implements ICompensateTradeService {

    @Autowired
    private ICompensateTradeDao compensateTradeDao;
    @Autowired
    private IInsuranceTradeDao insuranceTradeDao;
    @Autowired
    private IInvestDao investDao;
    @Autowired
    private IInsuranceInfoService insuranceInfoService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private IProductDao productDao;
    @Autowired
    private IRevenuePlanDao revenuePlanDao;
    @Autowired
    private IBalanceDetialService balanceDetialService;

    @Override
    public CompensateTrade compensateConfirm(final CompensateTrade trade) throws Exception {
        final InsuranceTrade insurance = insuranceTradeDao.selectInsuranceTradeByTradeNoAndStatus(trade.getInsuranceTrade(),
                InsuranceTradeStatus.WAIT_COMPENSATION.getCode(),trade.getCustomerNo());
        log.info("query insurance trade :{}",insurance);
        InvestInfo info = investDao.selectInvestInfoByTradeNoAndStatus(
                insurance.getInvestNo(),null,insurance.getCustomerNo());
        log.info("query invest info :{}",info);
        //调用account解冻扣款
        log.info("compensate confirm keeping account finish ,compensate tradeNo:{}",trade.getTradeNo());
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetialService.compensate(trade.getCustomerNo(),trade.getCcy(),trade.getCompensateCcy(),
                        trade.getEffectiveAmount(),trade.getCompensateAmount(),trade.getTradeNo(),insurance.getTradeNo());
                log.info("compensate confirm keeping account finish ,compensate tradeNo:{}",trade.getTradeNo());
                compensateTradeDao.updateCompensateTradeStatusByObj(trade, CompensationStatus.FINISH.getCode());
                insuranceTradeDao.updateInsuranceTradeStatusByObj(insurance, InsuranceTradeStatus.FINISH.getCode());
                insuranceTradeDao.updateInsuranceSubStatusByObj(insurance, InsuranceTradeSubStatus.FINISHI_COMPENSATION.getCode());
            }
        });

        return trade;
    }

    @Override
    public List<CompensateTrade> getTradesWaitingConfirm(){
        return compensateTradeDao.selectCompensateTradeByStatus(CompensationStatus.COMPENSATION.getCode());
    }

    @Override
    public CompensateTrade getCompensateTradeByNo(String tradeNo){
        return compensateTradeDao.selectTradeByInsuranceTradeNo(tradeNo);
    }

}
