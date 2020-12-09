package com.yoyo.base.service.dao.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yoyo.base.common.dataobject.ActivityProduct;
import com.yoyo.base.common.mapper.ActivityProductMapper;
import com.yoyo.base.service.dao.IActivityProductDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class ActivityProductDao implements IActivityProductDao {

    @Autowired
    private ActivityProductMapper activityProductMapper;

    @Override
    public List<ActivityProduct> getProductList(String activityId) {
        EntityWrapper<ActivityProduct> wrapper = new EntityWrapper<ActivityProduct>();
        wrapper.eq("activity_id",activityId);
        return activityProductMapper.selectList(wrapper);
    }


    @Override
    @Transactional
    public void setActivityProduct(String activityId, List<String> products) {
        EntityWrapper<ActivityProduct> wrapper = new EntityWrapper<ActivityProduct>();
        wrapper.eq("activity_id",activityId);
        activityProductMapper.delete(wrapper);
        for (String itemId : products) {
            ActivityProduct item = new ActivityProduct();
            item.setActivityId(activityId);
            item.setItemId(itemId);
            activityProductMapper.insert(item);
        }
    }
}
