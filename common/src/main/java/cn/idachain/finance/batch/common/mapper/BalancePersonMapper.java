package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.dataobject.BalancePerson;
import cn.idachain.finance.batch.common.base.SuperMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BalancePersonMapper extends SuperMapper<BalancePerson> {

    @Select("select currency, sum(balance) as balance from balance_person group by currency")
    List<BalancePerson> getBalanceGroupByCcy();
}
