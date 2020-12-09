package com.yoyo.base.service.service;

import com.baomidou.mybatisplus.service.IService;
import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.yoyo.base.common.dataobject.ActivityProduct;
import com.yoyo.base.common.model.VO.ProductDetail;

import java.util.List;

public interface IActivityProductService extends IService<ActivityProduct> {

    void setActivityProduct(String activityId, List<String> products);

    List<ProductDetail> getProductList(String activityId) throws SDKException;
}
