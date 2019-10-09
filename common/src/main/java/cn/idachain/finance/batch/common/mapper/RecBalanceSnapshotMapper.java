package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.RecBalanceSnapshot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author kun
 * @version 2019/9/29 17:32
 */
public interface RecBalanceSnapshotMapper extends SuperMapper<RecBalanceSnapshot> {

    @Select("select tb.* from (select currency, max(snapshot_time) max_time " +
            "from rec_balance_snapshot GROUP BY currency ) ta " +
            "inner join rec_balance_snapshot tb on ta.currency = tb.currency " +
            "where ta.max_time = tb.snapshot_time")
    List<RecBalanceSnapshot> getLastSnapshot();

    @Select("select tb.* from (select currency, max(snapshot_time) max_time " +
            "from rec_balance_snapshot where snapshot_time <= #{snapshotTime} GROUP BY currency ) ta " +
            "inner join rec_balance_snapshot tb on ta.currency = tb.currency " +
            "where ta.max_time = tb.snapshot_time")
    List<RecBalanceSnapshot> getLastSnapshotBefore(Long snapshotTime);

    @Insert("insert into rec_balance_snapshot (currency, in_amount, out_amount, snapshot_time) " +
            "select currency, in_amount + #{inAmount}, out_amount + #{outAmount}, #{snapshotTime} " +
            "from rec_balance_snapshot where currency = #{currency} order by snapshot_time desc limit 1")
    int insertSnapshot(String currency, BigDecimal inAmount, BigDecimal outAmount, Long snapshotTime);
}
