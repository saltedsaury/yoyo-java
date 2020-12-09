package com.yoyo.base.service.service;

import com.yoyo.base.common.dataobject.ProfitDetail;

import java.util.List;

public interface IActivityMappingService {

    boolean setActivityMapping(String activityId, String productId);

    List<ProfitDetail> getProductsByActivityId(String activityId);
}
