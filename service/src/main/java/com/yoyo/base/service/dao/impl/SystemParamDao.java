package com.yoyo.base.service.dao.impl;

import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.dataobject.SystemParam;
import com.yoyo.base.common.mapper.ProfitDetailMapper;
import com.yoyo.base.common.mapper.SystemParamMapper;
import com.yoyo.base.service.dao.IProfitDetailDao;
import com.yoyo.base.service.dao.ISystemParamDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SystemParamDao implements ISystemParamDao {

    @Autowired
    private SystemParamMapper systemParamMapper;


    @Override
    public Integer updateSystemParam(SystemParam systemParam) {
        return systemParamMapper.updateById(systemParam);
    }

    @Override
    public SystemParam getSystemParam(String paramId) {
        SystemParam where = new SystemParam();
        where.setParamId(paramId);
        return systemParamMapper.selectOne(where);
    }
}
