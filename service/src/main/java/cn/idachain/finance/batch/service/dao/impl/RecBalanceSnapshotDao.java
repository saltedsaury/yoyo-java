package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.RecBalanceSnapshot;
import cn.idachain.finance.batch.common.enums.Direction;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.common.mapper.RecBalanceSnapshotMapper;
import cn.idachain.finance.batch.service.dao.IRecBalanceSnapshotDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
    public List<RecBalanceSnapshot> lastSnapshot(Long snapshotTime) {
        return snapshotMapper.getLastSnapshot(snapshotTime);
    }

    @Override
    public void insertSnapshot(String ccy, BigDecimal amount, Direction direction, Long snapshotTime) {
        if (direction == null) {
            throw new BizException(BizExceptionEnum.DERICTION_ERROR);
        }

        BigDecimal inAmount;
        BigDecimal outAmount;
        switch (direction) {
            case IN:
                inAmount = amount;
                outAmount = BigDecimal.ZERO;
                break;
            case OUT:
                inAmount = BigDecimal.ZERO;
                outAmount = amount;
                break;
            default:
                throw new BizException(BizExceptionEnum.DERICTION_ERROR);
        }
        // 存在原有记录
        if (snapshotMapper.insertSnapshot(ccy, inAmount, outAmount, snapshotTime) > 0) {
            return;
        }
        // 不存在
        RecBalanceSnapshot snapshot = new RecBalanceSnapshot();
        snapshot.setCurrency(ccy);
        snapshot.setInAmount(inAmount);
        snapshot.setOutAmount(outAmount);
        snapshot.setSnapshotTime(snapshotTime);
        snapshotMapper.insert(snapshot);
    }

}
