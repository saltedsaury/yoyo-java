package com.yoyo.base.service.dao.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yoyo.base.common.mapper.ProfitDetailMapper;
import com.yoyo.base.service.dao.IActivityMappingDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ActivityMappingDao implements IActivityMappingDao {

    @Autowired
    private ProfitDetailMapper activityMappingMapper;

    public List<com.yoyo.base.common.dataobject.ProfitDetail> getProductsByActivityId(String activityId) {
        EntityWrapper<com.yoyo.base.common.dataobject.ProfitDetail> wrapper = new EntityWrapper<com.yoyo.base.common.dataobject.ProfitDetail>();
        wrapper.eq("activity_id",activityId);
        activityMappingMapper.selectList(wrapper);
        return activityMappingMapper.selectList(wrapper);
    }


    @Override
    public Integer setActivityMapping(String activityId, String productId) {
        com.yoyo.base.common.dataobject.ProfitDetail activityMapping = new com.yoyo.base.common.dataobject.ProfitDetail();
        activityMapping.setActivityId(activityId);
        activityMapping.setProductId(productId);
        return activityMappingMapper.insert(activityMapping);
    }
}
