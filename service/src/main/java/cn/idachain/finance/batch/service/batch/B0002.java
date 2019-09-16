package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.common.enums.BatchCode;
import cn.idachain.finance.batch.common.enums.ProductStatus;
import cn.idachain.finance.batch.common.model.Product;
import cn.idachain.finance.batch.service.dao.IInvestDao;
import cn.idachain.finance.batch.service.dao.IProductDao;
import cn.idachain.finance.batch.service.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class B0002 extends BaseBatch{

    @Autowired
    private IProductDao productDao;
    @Autowired
    private IInvestDao investDao;

    public boolean execute() throws Exception {
        beforeExcute(BatchCode.B0002.getCode());
        if (!checkStatus()){
            return false;
        }
        Date currentDate = new Date(System.currentTimeMillis());
        List<Product> products = productDao.getProductsByDate(ProductStatus.LOCK_IN.getCode()
                ,currentDate);
        log.info("do batch B0002 on date :{} for product list:{}",currentDate.toString() ,products);
        for (Product product:products){
            /*List<InvestInfo> investInfos = investDao.selectInvestRecordForBatch(
                    product.getProductNo(), BizType.INVEST.getCode(), InvestStatus.GIVE_OUT.getCode());
            for (InvestInfo info : investInfos){
                log.info("update invest info :{}",info);
                investDao.updateInvestInfoStatusByObj(info,InvestStatus.OVER_DUE.getCode());
            }*/
            log.info("update product:{}",product);
            productDao.updateProductByObj(product,ProductStatus.OPEN.getCode());
        }

        List<String> status = new ArrayList<>();
        status.add(ProductStatus.INIT.getCode());
        List<Product> initProduct = productDao.getProductsByStatus(status,null);
        for (Product prod:initProduct){
            if (DateUtil.isSameDay(prod.getEffectiveDate(),currentDate)){
                productDao.updateProductByObj(prod,ProductStatus.FOR_SALE.getCode());
            }
        }
        afterExecute();
        return true;
    }
}
