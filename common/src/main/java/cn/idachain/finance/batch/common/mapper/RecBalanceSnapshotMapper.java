package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.RecBalanceSnapshot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @author kun
 * @version 2019/9/29 17:32
 */
public interface RecBalanceSnapshotMapper extends SuperMapper<RecBalanceSnapshot> {

    @Select("select * from rec_balance_snapshot where snapshot_time = (select max(snapshot_time) from rec_balance_snapshot)")
    List<RecBalanceSnapshot> getLastSnapshot();

    @Insert("<script>insert into rec_balance_snapshot (currency, in_amount, out_amount, snapshot_time) values " +
            "<foreach collection='collection' item='snapshot' separator=','>" +
            "(#{snapshot.currency}, #{snapshot.inAmount}, #{snapshot.outAmount}, #{snapshot.snapshotTime})</foreach>" +
            "</script>")
    int insertSnapshotBatch(Collection<RecBalanceSnapshot> snapshots);
}
