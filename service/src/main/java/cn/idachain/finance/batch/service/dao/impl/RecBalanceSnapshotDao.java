package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.RecBalanceSnapshot;
import cn.idachain.finance.batch.common.mapper.RecBalanceSnapshotMapper;
import cn.idachain.finance.batch.service.dao.IRecBalanceSnapshotDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author kun
 * @version 2019/9/29 17:31
 */
@Repository
public class RecBalanceSnapshotDao implements IRecBalanceSnapshotDao {

    @Autowired
    private RecBalanceSnapshotMapper snapshotMapper;

    @Override
    public List<RecBalanceSnapshot> lastSnapshot() {
        return snapshotMapper.getLastSnapshot();
    }

    @Override
    @Transactional
    public int insertSnapshotBatch(Collection<RecBalanceSnapshot> snapshots) {
        return snapshotMapper.insertSnapshotBatch(snapshots);
    }

}
