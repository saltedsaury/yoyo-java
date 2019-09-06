package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.InsuranceInfo;
import cn.idachain.finance.batch.common.enums.ProductStatus;
import cn.idachain.finance.batch.common.mapper.InsuranceInfoMapper;
import cn.idachain.finance.batch.service.dao.IInsuranceInfoDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
    public List<InsuranceInfo> getInsurenceListByProductNo(String productNo, Page page){
        EntityWrapper<InsuranceInfo> wrapper = new EntityWrapper<InsuranceInfo>();
        wrapper.eq("product_no",productNo);
        wrapper.eq("status",ProductStatus.FOR_SALE.getCode());
        return insuranceInfoMapper.selectPage(page,wrapper);
    }

    @Override
    public List<InsuranceInfo> getInsuranceByDate(Date current){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.le("compensation_end",current);
        List<InsuranceInfo> result = insuranceInfoMapper.selectList(wrapper);

        return result;
    }

}
