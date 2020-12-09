package com.yoyo.base.task.task;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.youzan.cloud.open.sdk.core.client.auth.Token;
import com.youzan.cloud.open.sdk.core.client.core.DefaultYZClient;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import com.youzan.cloud.open.sdk.core.oauth.token.TokenParameter;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.YouzanItemsOnsaleGet;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.*;
import com.yoyo.base.common.dataobject.Product;
import com.yoyo.base.service.service.impl.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GetProductDetail {


    @Autowired
    private DefaultYZClient yzClient;
    @Autowired
    private ProductService productService;


    /**
     * @throws Exception
     */
    @Scheduled(cron = "${task.getProduct}")
    public boolean execute() throws Exception {
        log.info("insurance over due task begin.");

        //获取有赞云凭证access_token
        TokenParameter tokenParameter = TokenParameter.self()
                .clientId("3beba2d95f888e14d9")
                .clientSecret("55e4b827c35e595b4db8e2cd5cc5022a")
                .grantId("91393221")
                .refresh(true)
                .build();
        OAuthToken oAuthToken = yzClient.getOAuthToken(tokenParameter);
        Token token = new Token(oAuthToken.getAccessToken());

        try {
            //获取全部商品（分页获取）

            //创建参数对象,并设置参数
            YouzanItemsOnsaleGetParams youzanItemsOnsaleGetParams = new YouzanItemsOnsaleGetParams();
            youzanItemsOnsaleGetParams.setPageNo(1);

            dealProduct(youzanItemsOnsaleGetParams,token);


        }catch (Exception e){
            log.error("insurance over due task failed.");
            throw e;
        }

        log.info("insurance over due task end.");
        return true;
    }

    private void dealProduct(YouzanItemsOnsaleGetParams youzanItemsOnsaleGetParams,Token token) throws SDKException {
        YouzanItemsOnsaleGet youzanItemsOnsaleGet = new YouzanItemsOnsaleGet();
        youzanItemsOnsaleGet.setAPIParams(youzanItemsOnsaleGetParams);

        YouzanItemsOnsaleGetResult result = yzClient.invoke(youzanItemsOnsaleGet, token, YouzanItemsOnsaleGetResult.class);

        log.info("deal product for page {},data size: {}",youzanItemsOnsaleGetParams.getPageNo(),result.getData().getCount());

        //计算订单收益，订单时间，频道id 生成收益明细记入数据库
        if(result.getData().getItems()!=null
                && result.getData().getItems().size()>0){
            List<Product> addList = new ArrayList<>();
            List<Product> updateList = new ArrayList<>();
            for (YouzanItemsOnsaleGetResult.YouzanItemsOnsaleGetResultItems item
                    : result.getData().getItems()){

                Product product = new Product();
                product.setItemId(item.getItemId().toString());
                product.setItemName(item.getTitle());
                product.setPrice(item.getPrice().toString());
                product.setPicUrl(item.getImage());
                product.setAlias(item.getAlias());

                EntityWrapper<Product> where = new EntityWrapper<>();
                where.eq("item_id",product.getItemId());
                Product old = productService.selectOne(where);
                if (null == old){
                    addList.add(product);
                    continue;
                }

                if (!product.getPicUrl().equals(old.getPicUrl())
                        || !product.getItemName().equals(old.getItemName())
                        || !product.getPrice().equals(old.getPrice())){
                    old.setPicUrl(product.getPicUrl());
                    old.setPrice(product.getPrice());
                    old.setItemName(product.getItemName());
                    updateList.add(old);
                }

            }
            if (addList.size()>0) {
                productService.insertBatch(addList);
            }
            if (updateList.size()>0) {
                productService.updateBatchById(updateList);
            }
            youzanItemsOnsaleGetParams.setPageNo(youzanItemsOnsaleGetParams.getPageNo()+1);
            dealProduct(youzanItemsOnsaleGetParams,token);
        }

    }
}
