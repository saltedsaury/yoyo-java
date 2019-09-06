package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.SystemDate;
import cn.idachain.finance.batch.common.mapper.SystemDateMapper;
import cn.idachain.finance.batch.service.dao.ISystemDateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SystemDateDao implements ISystemDateDao {
    @Autowired
    private SystemDateMapper systemDateMapper;

    @Override
    public SystemDate getSystemDateByType(String type){
        SystemDate wrapper = new SystemDate();
        wrapper.setDateType(type);
        return  systemDateMapper.selectOne(wrapper);
    }

    @Override
    public void updateSystemDate(SystemDate systemDate){
        systemDateMapper.updateById(systemDate);
    }
}
