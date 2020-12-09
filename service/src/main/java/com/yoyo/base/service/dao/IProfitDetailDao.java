package com.yoyo.base.service.dao;

import com.yoyo.base.common.dataobject.ProfitDetail;

import java.util.List;

public interface IActivityMappingDao {
    List<ProfitDetail> getProductsByActivityId(String activityId);

    Integer setActivityMapping(String activityId, String productId);

}
