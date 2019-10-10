package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.RecBalanceSnapshot;

import java.util.Collection;
import java.util.List;

/**
 * @author kun
 * @version 2019/9/29 17:31
 */
public interface IRecBalanceSnapshotDao {

    List<RecBalanceSnapshot> lastSnapshot();

    int insertSnapshotBatch(Collection<RecBalanceSnapshot> snapshots);

}
