package cn.idachain.finance.batch.common.mapper;


import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.RevenuePlan;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;

public interface RevenuePlanMapper extends SuperMapper<RevenuePlan> {

    @Update("<script>update revenue_plan set reconciled = 1 where plan_no in " +
            "<foreach collection='collection' item='no' separator=',' open='(' close=')'>" +
            "#{no}</foreach></script>")
    int markReconciled(Collection<String> orderNos);
}