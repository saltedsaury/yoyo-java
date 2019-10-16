package cn.idachain.finance.batch.task.task;

import cn.idachain.finance.batch.common.enums.ProductStatus;
import cn.idachain.finance.batch.common.model.Product;
import cn.idachain.finance.batch.service.dao.IProductDao;
import cn.idachain.finance.batch.service.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProductForSale {

    @Autowired
    private IProductDao productDao;

    @Scheduled(cron = "${task.financing.push-prod-status}")
    public boolean execute(){
        Date currentDate = new Date(System.currentTimeMillis());
        log.info("push product status to for_sale on date :{}",currentDate);
        List<String> status = new ArrayList<String>();
        status.add(ProductStatus.INIT.getCode());
        List<Product> initProduct = productDao.getProductsByStatus(status,null);
        log.info("init product list :{}",initProduct);
        for (Product prod:initProduct){
            if (prod.getEffectiveDate().compareTo(currentDate)<=0){
                productDao.updateProductByObj(prod,ProductStatus.FOR_SALE.getCode());
            }
        }
        log.info("push init product to for sale success");
        status = new ArrayList<String>();
        status.add(ProductStatus.FOR_SALE.getCode());
        List<Product> saleProduct = productDao.getProductsByStatus(status,null);
        log.info("for sale product list :{}",saleProduct);
        for (Product prod:saleProduct){
            if (prod.getExpiryDate().compareTo(currentDate)<=0){
                productDao.updateProductByObj(prod,ProductStatus.OFF_SHELVE.getCode());
            }
        }
        log.info("push product status finished!");
        return true;
    }
}
