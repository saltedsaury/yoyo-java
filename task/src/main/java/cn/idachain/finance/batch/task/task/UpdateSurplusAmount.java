package cn.idachain.finance.batch.task.task;

import cn.idachain.finance.batch.common.enums.InvestStatus;
import cn.idachain.finance.batch.common.enums.ProductStatus;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.common.model.Product;
import cn.idachain.finance.batch.common.util.BaseCacheClient;
import cn.idachain.finance.batch.common.util.DateUtil;
import cn.idachain.finance.batch.service.dao.IInvestDao;
import cn.idachain.finance.batch.service.dao.IProductDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UpdateSurplusAmount {

    @Autowired
    private IProductDao productDao;
    @Autowired
    private BaseCacheClient baseCacheClient;
    @Autowired
    private IInvestDao investDao;

    @Scheduled(fixedRateString = "${task.financing.transfer-confirm}")
    public void execute() {
        List<String> status = new ArrayList<>();
        status.add(ProductStatus.FOR_SALE.getCode());
        List<Product> products = productDao.getProductsByStatus(status,null);
        for (Product product : products) {
            //取缓存
            String surplus = baseCacheClient.getStringValue("surplusAmount"+product.getProductNo());
            //缓存取不到，从数据库获取数据并更新缓存
            if ("null" == surplus){
                log.info("");
                BigDecimal tmp = investDao.getSurplusAmount(InvestStatus.APPLY_SUCCESS.getCode()
                        , product.getProductNo());
                BigDecimal surplusAmount = product.getRaisedAmount().subtract(tmp);
                long expire = DateUtil.diff(new Date(),product.getExpiryDate(),DateUtil.MS);
                boolean add = baseCacheClient.addValueNX("surplusAmount"+product.getProductNo()
                        ,surplusAmount,expire);
                if (!add){
                    log.error("unknown surplus amount,productNo:{}",product.getProductNo());
                    throw new BizException(BizExceptionEnum.SUBSCRIBE_TIMES_NOT_ENOUGH);
                }
                continue;
            }

            BigDecimal surplusAmount = new BigDecimal(surplus);
            if (surplusAmount.equals(product.getSurplusAmount())) {
                continue;
            }
            //同步到数据库
            productDao.updateSurplusAmount(surplusAmount, product.getProductNo());
        }
    }
}
