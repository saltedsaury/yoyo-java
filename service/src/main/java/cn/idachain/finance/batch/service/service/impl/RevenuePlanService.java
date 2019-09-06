package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.RevenuePlan;
import cn.idachain.finance.batch.service.dao.IRevenuePlanDao;
import cn.idachain.finance.batch.service.service.IRevenuePlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RevenuePlanService implements IRevenuePlanService {

    @Autowired
    private IRevenuePlanDao revenuePlanDao;
    /*@Autowired
    private IProductService productService;

    @Override
    public Product getProductByPlanNo(String planNo){
        ProductVO product = new Product();
        RevenuePlan revenuePlan = revenuePlanDao.selectPlanByNo(planNo);
        try {
            product = productService.getProductByNo(revenuePlan.getProductNo());
        }catch (Exception e ){
            log.error("getProductByPlanNo error,plan no:{}",planNo);
        }
        return product;
    }*/

    @Override
    public RevenuePlan getRevenuePlanByNo(String planNo){
        return revenuePlanDao.selectPlanByNo(planNo);
    }
}
