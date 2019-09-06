package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.InsuranceInfo;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.Date;
import java.util.List;

public interface IInsuranceInfoDao {
    InsuranceInfo getInsurenceInfoByEntity(InsuranceInfo entity);

    List<InsuranceInfo> getInsurenceListByProductNo(String productNo, Page page);

    List<InsuranceInfo> getInsuranceByDate(Date current);
}
