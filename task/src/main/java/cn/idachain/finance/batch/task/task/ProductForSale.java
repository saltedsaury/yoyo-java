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

    @Scheduled(cron = "${task.financing.begin-of-day}")
    public boolean execute(){
        Date currentDate = new Date(System.currentTimeMillis());
        log.info("push product status to for_sale on date :{}",currentDate);
        List<String> status = new ArrayList<String>();
        status.add(ProductStatus.INIT.getCode());
        List<Product> initProduct = productDao.getProductsByStatus(status,null);
        for (Product prod:initProduct){
            if (DateUtil.isSameDay(prod.getEffectiveDate(),currentDate)){
                productDao.updateProductByObj(prod,ProductStatus.FOR_SALE.getCode());
            }
        }
        log.info("push product status finished!");
        return true;
    }
}
