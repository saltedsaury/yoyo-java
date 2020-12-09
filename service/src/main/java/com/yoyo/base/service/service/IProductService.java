package com.yoyo.base.service.service;

import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.yoyo.base.common.model.VO.ProductDetail;

import java.util.List;

public interface IActivityProductService {

    void setActivityProduct(String activityId, List<String> products);

    List<ProductDetail> getProductList(String activityId) throws SDKException;
}
