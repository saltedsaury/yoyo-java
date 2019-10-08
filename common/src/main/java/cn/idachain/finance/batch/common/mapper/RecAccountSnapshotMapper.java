package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.RecAccountSnapshot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * @author kun
 * @version 2019/10/8 14:48
 */
public interface RecAccountSnapshotMapper extends SuperMapper<RecAccountSnapshot> {


    @Insert("<script>insert into rec_account_snapshot (account, account_type, balance, snapshot_time) " +
            "values <foreach collection='status' item='item' separator=','>" +
            "(#{item.account}, #{item.accountType}, #{item.balance}, #{item.snapshotTime})" +
            "</foreach></script>")
    void insertBatch(Collection<RecAccountSnapshot> snapshots);

    @Select("select max(snapshot_time) from rec_account_snapshot")
    Long lastSnapshotTime();

    @Select("select * from (select account, max(snapshot_time) max_time from rec_account_snapshot " +
            "where account in (#{accounts}) group by account) ta " +
            "inner join rec_account_snapshot tb on ta.account = tb.account where ta.max_time = tb.snapshot_time")
    List<RecAccountSnapshot> getLatestSnapshot(Collection<String> accounts);
}
