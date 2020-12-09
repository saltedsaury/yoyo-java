package com.yoyo.base.service.service.impl;

import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.service.dao.IProfitDetailDao;
import com.yoyo.base.service.service.IActivityMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActivityMappingService implements IActivityMappingService {

    @Autowired
    private IProfitDetailDao activityMappingDao;

    @Override
    public boolean setActivityMapping(String activityId, String productId) {
        if (activityMappingDao.setActivityMapping(activityId,productId)>0){
            return true;
        }
        return false;
    }

    @Override
    public List<ProfitDetail> getProductsByActivityId(String activityId) {
        return activityMappingDao.getProductsByActivityId(activityId);
    }
}