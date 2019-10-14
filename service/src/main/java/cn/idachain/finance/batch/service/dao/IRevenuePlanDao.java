package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.RevenuePlan;

import java.util.Collection;
import java.util.List;

public interface IRevenuePlanDao {
    RevenuePlan selectPlanByNo(String planNo);

    void updatePlanStatusByObj(RevenuePlan revenuePlan, String status);

    List<RevenuePlan> selectPlanForBatch(String procuctNo, String status);

    void updatePlanById(RevenuePlan revenuePlan);

    void saveRevenuePlan(RevenuePlan revenuePlan);

    List<RevenuePlan> getRevenuePlanByCustomer(String customerNo, List<String> status);

    void markReconciled(Collection<String> orderNos);
}
