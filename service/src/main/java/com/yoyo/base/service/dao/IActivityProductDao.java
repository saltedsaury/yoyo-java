package com.yoyo.base.service.dao;

import com.yoyo.base.common.dataobject.ActivityProduct;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IActivityProductDao {

    List<ActivityProduct> getProductList(String activityId);

    @Transactional
    void setActivityProduct(String activityId, List<String> products);
}
