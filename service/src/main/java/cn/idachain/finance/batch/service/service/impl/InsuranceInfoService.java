package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.InsuranceInfo;
import cn.idachain.finance.batch.common.enums.ProductStatus;
import cn.idachain.finance.batch.service.dao.IInsuranceInfoDao;
import cn.idachain.finance.batch.service.service.IInsuranceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InsuranceInfoService implements IInsuranceInfoService {

    @Autowired
    private IInsuranceInfoDao insuranceInfoDao;

    @Override
    public InsuranceInfo getInsuranceInfoByNoAndProduct(String insuranceNo,String productNo){
        InsuranceInfo insuranceInfo = new InsuranceInfo();
        insuranceInfo.setInsuranceNo(insuranceNo);
        insuranceInfo.setProductNo(productNo);
        insuranceInfo.setStatus(ProductStatus.FOR_SALE.getCode());

        return insuranceInfoDao.getInsurenceInfoByEntity(insuranceInfo);
    }

}
