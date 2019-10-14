package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.InsuranceInfo;
import cn.idachain.finance.batch.common.enums.InsuranceStatus;
import cn.idachain.finance.batch.common.mapper.InsuranceInfoMapper;
import cn.idachain.finance.batch.service.dao.IInsuranceInfoDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InsuranceInfoDao implements IInsuranceInfoDao {

    @Autowired
    private InsuranceInfoMapper insuranceInfoMapper;

    @Override
    public InsuranceInfo getInsurenceInfoByEntity(InsuranceInfo entity){
        return insuranceInfoMapper.selectOne(entity);
    }

    @Override
    public List<InsuranceInfo> getAllInsurance() {
        EntityWrapper<InsuranceInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("status", InsuranceStatus.ACTIVE.getCode());

        return insuranceInfoMapper.selectList(wrapper);
    }

}
