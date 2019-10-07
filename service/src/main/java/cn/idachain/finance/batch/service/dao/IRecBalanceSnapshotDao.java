package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.RecBalanceSnapshot;
import cn.idachain.finance.batch.common.enums.Direction;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author kun
 * @version 2019/9/29 17:31
 */
public interface IRecBalanceSnapshotDao {

    List<RecBalanceSnapshot> lastSnapshot(Long snapshotTime);

    void insertSnapshot(String ccy, BigDecimal amount, Direction direction, Long snapshotTime);
}
