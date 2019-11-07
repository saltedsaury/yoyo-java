package cn.idachain.finance.batch.service.util;

import cn.idachain.finance.batch.common.enums.InvestStatus;
import cn.idachain.finance.batch.common.enums.ProductStatus;
import cn.idachain.finance.batch.common.model.Product;
import cn.idachain.finance.batch.common.util.BaseCacheClient;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.common.util.DateUtil;
import cn.idachain.finance.batch.service.dao.IInvestDao;
import cn.idachain.finance.batch.service.dao.IProductDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class BizCacheUtil {
    @Resource
    private BaseCacheClient baseCacheClient;
    @Autowired
    private IInvestDao investDao;
    @Autowired
    private IProductDao productDao;

    @PostConstruct
    public void productSurplusAmountInit(){
        List<String> status = new ArrayList<>();
        status.add(ProductStatus.FOR_SALE.getCode());
        List<Product> products = productDao.getProductsByStatus(status,null);
        baseCacheClient.getRedisTemplate().setValueSerializer(new GenericToStringSerializer<Float>(Float.class));
        for (Product product : products) {
            String surplus = baseCacheClient.getStringValue("surplusAmount"+product.getProductNo());

            //剩余投资金额未初始化的情况
            if ("null" == surplus){
                long expire = DateUtil.diff(new Date(),product.getExpiryDate(),DateUtil.MS);
                BigDecimal tmp = investDao.getSurplusAmount(InvestStatus.APPLY_SUCCESS.getCode()
                        , product.getProductNo());
                if (BlankUtil.isBlank(tmp)){
                    baseCacheClient.addValueNX("surplusAmount"+product.getProductNo()
                            ,product.getSurplusAmount().floatValue(),expire);
                    continue;
                }
                BigDecimal surplusAmount = product.getRaisedAmount().subtract(tmp);
                baseCacheClient.addValueNX("surplusAmount"+product.getProductNo(),
                        surplusAmount.floatValue(),expire);
            }
        }
        baseCacheClient.getRedisTemplate().setValueSerializer(new StringRedisSerializer());
    }

}
