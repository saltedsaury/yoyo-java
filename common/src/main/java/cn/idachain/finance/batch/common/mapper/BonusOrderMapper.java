package cn.idachain.finance.batch.common.mapper;


import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.BonusOrder;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;

public interface BonusOrderMapper extends SuperMapper<BonusOrder> {

    @Update("<script>update bonus_order set reconciled = 1 where trade_no in " +
            "<foreach collection='collection' item='no' separator=',' open='(' close=')'>" +
            "#{no}</foreach></script>")
    int markReconciled(Collection<String> orderNos);
}