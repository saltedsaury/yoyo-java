package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.RecAccountSnapshot;
import cn.idachain.finance.batch.common.mapper.RecAccountSnapshotMapper;
import cn.idachain.finance.batch.service.dao.IRecAccountSnapshotDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author kun
 * @version 2019/10/8 14:49
 */
@Repository
public class RecAccountSnapshotDao implements IRecAccountSnapshotDao {

    @Autowired
    private RecAccountSnapshotMapper snapshotMapper;

    @Override
    public void insertBatch(Collection<RecAccountSnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) {
            return;
        }
        snapshotMapper.insertBatch(snapshots);
    }

    @Override
    public Long getLastSnapshotTime() {
        Long lastSnapshotTime = snapshotMapper.lastSnapshotTime();
        return lastSnapshotTime == null ? 0 : lastSnapshotTime;
    }

    @Override
    public List<RecAccountSnapshot> getSnapshotByAccounts(Collection<String> accounts) {
        if (accounts.isEmpty()) {
            return Collections.emptyList();
        }
        return snapshotMapper.getLatestSnapshot(accounts);
    }

}
