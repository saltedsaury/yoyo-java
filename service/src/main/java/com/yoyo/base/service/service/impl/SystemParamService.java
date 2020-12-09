package com.yoyo.base.service.service.impl;

import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.dataobject.SystemParam;
import com.yoyo.base.service.dao.IProfitDetailDao;
import com.yoyo.base.service.dao.ISystemParamDao;
import com.yoyo.base.service.service.IProfitDetailService;
import com.yoyo.base.service.service.ISystemParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SystemParamService implements ISystemParamService {

    @Autowired
    private ISystemParamDao systemParamDao;

    @Override
    public boolean updateSystemParam(SystemParam systemParam) {
        if (systemParamDao.updateSystemParam(systemParam)>0){
            return true;
        }
        return false;
    }

    @Override
    public SystemParam getSystemParam(String paramId) {

        return systemParamDao.getSystemParam(paramId);
    }

}