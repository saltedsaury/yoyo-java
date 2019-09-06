package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.InsuranceTrade;

import java.util.List;

public interface IInsuranceTradeDao {
    void saveInsuranceTrade(InsuranceTrade insuranceTrade);

    void updateInsuranceTradeStatusByObj(InsuranceTrade trade, String status);

    InsuranceTrade selectInsuranceTradeByTradeNoAndStatus(String tradeNo, String status, String customerNo);

    InsuranceTrade getTradeByInvestNo(String investNo,String status);

    List<InsuranceTrade> selectInsuranceTradeByCustomer(String curtomerNo,String insuranceNo);

    void updateInsuranceSubStatusByObj(InsuranceTrade trade, String status);
}
