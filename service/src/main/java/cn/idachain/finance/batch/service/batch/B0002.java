package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.common.enums.BatchCode;
import cn.idachain.finance.batch.common.enums.ProductStatus;
import cn.idachain.finance.batch.common.model.Product;
import cn.idachain.finance.batch.service.dao.IInvestDao;
import cn.idachain.finance.batch.service.dao.IProductDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        afterExecute();
        return true;
    }
}
