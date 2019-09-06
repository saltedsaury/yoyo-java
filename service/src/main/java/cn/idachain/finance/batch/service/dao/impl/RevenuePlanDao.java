package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.RevenuePlan;
import cn.idachain.finance.batch.common.mapper.RevenuePlanMapper;
import cn.idachain.finance.batch.service.dao.IRevenuePlanDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class RevenuePlanDao implements IRevenuePlanDao {
    @Autowired
    private RevenuePlanMapper revenuePlanMapper;

    @Override
    public RevenuePlan selectPlanByNo(String planNo){
        RevenuePlan wrapper = new RevenuePlan();
        wrapper.setPlanNo(planNo);
        RevenuePlan revenuePlan = revenuePlanMapper.selectOne(wrapper);
        return revenuePlan;
    }

    @Override
    public void updatePlanStatusByObj(RevenuePlan revenuePlan, String status){
        EntityWrapper<RevenuePlan> wrapper = new EntityWrapper<RevenuePlan>();
        wrapper.eq("plan_no",revenuePlan.getPlanNo());
        wrapper.eq("status",revenuePlan.getStatus());

        revenuePlan.setStatus(status);
        revenuePlan.setModifiedTime(new Date(System.currentTimeMillis()));
        revenuePlanMapper.update(revenuePlan,wrapper);
    }

    @Override
    public List<RevenuePlan> selectPlanForBatch(String procuctNo, String status){
        EntityWrapper<RevenuePlan> wrapper = new EntityWrapper<RevenuePlan>();
        wrapper.eq("product_no",procuctNo);
        wrapper.eq("status",status);

        List<RevenuePlan> plans = revenuePlanMapper.selectList(wrapper);
        return plans;
    }

    @Override
    public void updatePlanById(RevenuePlan revenuePlan){
        revenuePlanMapper.updateById(revenuePlan);
    }

    @Override
    public void saveRevenuePlan(RevenuePlan revenuePlan){
        revenuePlanMapper.insert(revenuePlan);
    }

    @Override
    public List<RevenuePlan> getRevenuePlanByCustomer(String customerNo, List<String> status) {
        EntityWrapper<RevenuePlan> wrapper = new EntityWrapper<RevenuePlan>();
        wrapper.eq("customer_no",customerNo);
        wrapper.in("status",status);
        wrapper.orderBy("effective_date",false);
        List<RevenuePlan> revenuePlans = revenuePlanMapper.selectList(wrapper);
        return revenuePlans;
    }

}
