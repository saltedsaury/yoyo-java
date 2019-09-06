package cn.idachain.finance.batch.service.service;

import cn.idachain.finance.batch.common.dataobject.RevenuePlan;

public interface IRevenuePlanService {
    //Product getProductByPlanNo(String planNo);

    RevenuePlan getRevenuePlanByNo(String planNo);
}
