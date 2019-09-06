package cn.idachain.finance.batch.service.service;

import cn.idachain.finance.batch.common.dataobject.InsuranceInfo;

public interface IInsuranceInfoService {
    InsuranceInfo getInsuranceInfoByNoAndProduct(String insuranceNo,String productNo);
}
