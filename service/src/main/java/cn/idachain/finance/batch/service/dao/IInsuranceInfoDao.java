package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.InsuranceInfo;

import java.util.List;

public interface IInsuranceInfoDao {
    InsuranceInfo getInsurenceInfoByEntity(InsuranceInfo entity);

    List<InsuranceInfo> getAllInsurance();
}
