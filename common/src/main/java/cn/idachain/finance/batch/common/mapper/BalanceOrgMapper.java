package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.BalanceOrg;
import org.apache.ibatis.annotations.Select;

import java.util.Arrays;
import java.util.List;

public interface BalanceOrgMapper extends SuperMapper<BalanceOrg> {

    @Select("select currency, sum(balance) as balance from balance_org group by currnecy")
    List<BalanceOrg> getBalanceGroupByCcy();

}
