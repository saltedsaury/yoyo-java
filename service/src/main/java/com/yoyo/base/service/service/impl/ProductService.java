package com.yoyo.base.service.service.impl;

import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.youzan.cloud.open.sdk.core.client.auth.Token;
import com.youzan.cloud.open.sdk.core.client.core.DefaultYZClient;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import com.youzan.cloud.open.sdk.core.oauth.token.TokenParameter;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.YouzanItemGet;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanItemGetParams;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanItemGetResult;
import com.yoyo.base.common.dataobject.ActivityProduct;
import com.yoyo.base.common.model.VO.ProductDetail;
import com.yoyo.base.service.dao.IActivityProductDao;
import com.yoyo.base.service.service.IActivityProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ActivityProductService implements IActivityProductService {

    @Autowired
    private IActivityProductDao activityProductDao;
    @Autowired
    private DefaultYZClient yzClient;

    @Override
    public void setActivityProduct(String activityId, List<String> products) {
        activityProductDao.setActivityProduct(activityId,products);
    }

    @Override
    public List<ProductDetail> getProductList(String activityId) throws SDKException {
        List<ActivityProduct> list = activityProductDao.getProductList(activityId);
        List<ProductDetail> result = new ArrayList<>();
        //获取有赞云凭证access_token
        TokenParameter tokenParameter = TokenParameter.self()
                .clientId("3beba2d95f888e14d9")
                .clientSecret("55e4b827c35e595b4db8e2cd5cc5022a")
                .grantId("91393221")
                .refresh(true)
                .build();
        OAuthToken oAuthToken = yzClient.getOAuthToken(tokenParameter);
        Token token = new Token(oAuthToken.getAccessToken());

        for (ActivityProduct product : list){
            //获取商品信息
            YouzanItemGet youzanItemGet = new YouzanItemGet();
            YouzanItemGetParams youzanItemGetParams = new YouzanItemGetParams();
            youzanItemGetParams.setItemId(Long.parseLong(product.getItemId()));
            youzanItemGet.setAPIParams(youzanItemGetParams);

            YouzanItemGetResult goods = yzClient.invoke(youzanItemGet, token, YouzanItemGetResult.class);
            YouzanItemGetResult.YouzanItemGetResultItem yzItem = goods.getData().getItem();
            ProductDetail productDetail = new ProductDetail();

            productDetail.setProductId(yzItem.getItemId().toString());
            productDetail.setProductName(yzItem.getTitle());
            productDetail.setPrice(new BigDecimal(yzItem.getPrice()).divide(new BigDecimal(100)).toString());
            productDetail.setPicUrl(yzItem.getPicUrl());
            productDetail.setShareUrl(yzItem.getAlias());
            productDetail.setVoucherUrl(product.getVoucherUrl());
            result.add(productDetail);
        }
        return result;
    }

}