package com.yoyo.base.service.dao.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.mapper.ProfitDetailMapper;
import com.yoyo.base.service.dao.IProfitDetailDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProfitDetailDao implements IProfitDetailDao {

    @Autowired
    private ProfitDetailMapper profitDetailMapper;

   /* public List<com.yoyo.base.common.dataobject.ProfitDetail> getProductsByActivityId(String activityId) {
        EntityWrapper<com.yoyo.base.common.dataobject.ProfitDetail> wrapper = new EntityWrapper<com.yoyo.base.common.dataobject.ProfitDetail>();
        wrapper.eq("activity_id",activityId);
        activityMappingMapper.selectList(wrapper);
        return activityMappingMapper.selectList(wrapper);
    }*/


    @Override
    public Integer setProfitDetail(ProfitDetail profitDetail) {

        return profitDetailMapper.insert(profitDetail);
    }
}
