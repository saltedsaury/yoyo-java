package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.service.dao.IBalanceDetailDao;
import cn.idachain.finance.batch.common.dataobject.BalanceDetail;
import cn.idachain.finance.batch.common.mapper.BalanceDetailMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BalanceDetailDao extends ServiceImpl<BalanceDetailMapper,BalanceDetail> implements IBalanceDetailDao {

    @Autowired
    private BalanceDetailMapper balanceDetailMapper;

    @Override
    public int saveBalanceDetail(BalanceDetail balanceDetail){
        return balanceDetailMapper.insert(balanceDetail);
    }

    @Override
    public List<BalanceDetail> getDetailsByBonusOrderToReconcile(Long lastId) {
        return balanceDetailMapper.getDetailsByBonusOrderToReconciled(lastId);
    }

    @Override
    public List<BalanceDetail> getDetailsByTransferToReconcile(Long lastId) {
        return balanceDetailMapper.getDetailsByTransferOrderToReconcile(lastId);
    }


    @Override
    public List<BalanceDetail> getDetailsByInvestInfoToReconcile(Long lastId) {
        return balanceDetailMapper.getDetailsByInvestInfoToReconcile(lastId);
    }

    @Override
    public List<BalanceDetail> getDetailsByCompensationToReconcile(Long lastId) {
        return balanceDetailMapper.getDetailsByCompensationToReconcile(lastId);
    }

    @Override
    public List<BalanceDetail> getDetailsByRedemptionToReconcile(Long lastId) {
        return balanceDetailMapper.getDetailsByRedemptionToReconcile(lastId);
    }

    @Override
    public List<BalanceDetail> getDetailsByRevenuePlanToReconcile(Long lastId) {
        return balanceDetailMapper.getDetailsByRevenuePlanToReconcile(lastId);
    }

    @Override
    public Long getLastId() {
        return balanceDetailMapper.getLastId();
    }
}
