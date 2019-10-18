package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.RecAccountSnapshot;

import java.util.Collection;
import java.util.List;

/**
 * @author kun
 * @version 2019/10/8 14:49
 */
public interface IRecAccountSnapshotDao {

    void insertBatch(Collection<RecAccountSnapshot> snapshots);

    Long getLastSnapshotTime();

    List<RecAccountSnapshot> getSnapshotByAccounts(Collection<String> accounts);
}
