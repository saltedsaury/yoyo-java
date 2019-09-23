package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.InsuranceTrade;

import java.util.Date;
import java.util.List;

public interface IInsuranceTradeDao {

    void updateInsuranceTradeStatusByObj(InsuranceTrade trade, String status);

    InsuranceTrade selectInsuranceTradeByTradeNoAndStatus(String tradeNo, String status, String customerNo);

    InsuranceTrade getTradeByInvestNo(String investNo,String status);

    void updateInsuranceSubStatusByObj(InsuranceTrade trade, String status);

    List<InsuranceTrade> getInsuranceTradeOverDue(String insuranceNo, String status, Date currentDate);
}
