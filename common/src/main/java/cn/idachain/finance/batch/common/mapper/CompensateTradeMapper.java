package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.CompensateTrade;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;

public interface CompensateTradeMapper extends SuperMapper<CompensateTrade> {

    @Update("<script>update compensate_trade set reconciled = 1 where trade_no in " +
            "<foreach collection='collection' item='no' separator=',' open='(' close=')'>" +
            "#{no}</foreach></script>")
    int markReconciled(Collection<String> orderNos);
}
