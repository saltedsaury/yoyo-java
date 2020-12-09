package com.yoyo.base.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.youzan.cloud.open.sdk.core.client.auth.Token;
import com.youzan.cloud.open.sdk.core.client.core.DefaultYZClient;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import com.youzan.cloud.open.sdk.core.oauth.token.TokenParameter;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.YouzanItemGet;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanItemGetParams;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanItemGetResult;
import com.yoyo.base.common.dataobject.ActivityProduct;
import com.yoyo.base.common.dataobject.Product;
import com.yoyo.base.common.mapper.ProductMapper;
import com.yoyo.base.common.model.VO.ProductDetail;
import com.yoyo.base.service.dao.IActivityProductDao;
import com.yoyo.base.service.service.IActivityProductService;
import com.yoyo.base.service.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> implements IProductService {



}