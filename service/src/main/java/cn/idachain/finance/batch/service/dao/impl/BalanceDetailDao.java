package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.service.dao.IBalanceDetailDao;
import cn.idachain.finance.batch.common.dataobject.BalanceDetail;
import cn.idachain.finance.batch.common.mapper.BalanceDetailMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
    public List<BalanceDetail> getDetailsByBonusOrderBetweenTime(Long start, Long end) {
        return balanceDetailMapper.getDetailsByBonusOrderBetweenTime(start, end);
    }

    @Override
    public List<BalanceDetail> getDetailsByTransferOrderBetweenTime(Long start, Long end) {
        return balanceDetailMapper.getDetailsByTransferOrderBetweenTime(start, end);
    }

    @Override
    public List<BalanceDetail> getDetailsByInvestInfoBetweenTime(Long start, Long end) {
        return balanceDetailMapper.getDetailsByInvestInfoBetweenTime(start, end);
    }

    @Override
    public List<BalanceDetail> getDetailsByCompensationBetweenTime(Long start, Long end) {
        return balanceDetailMapper.getDetailsByCompensationBetweenTime(start, end);
    }

    @Override
    public List<BalanceDetail> getDetailsByRedemptionBetweenTime(Long start, Long end) {
        return balanceDetailMapper.getDetailsByRedemptionBetweenTime(start, end);
    }

    @Override
    public List<BalanceDetail> getDetailsByRevenuePlanBetweenTime(Long start, Long end) {
        return balanceDetailMapper.getDetailsByRevenuePlanBetweenTime(start, end);
    }
}
