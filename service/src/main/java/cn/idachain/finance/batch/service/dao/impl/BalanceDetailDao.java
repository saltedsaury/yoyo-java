package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.service.dao.IBalanceDetailDao;
import cn.idachain.finance.batch.common.dataobject.BalanceDetail;
import cn.idachain.finance.batch.common.mapper.BalanceDetailMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BalanceDetailDao extends ServiceImpl<BalanceDetailMapper,BalanceDetail> implements IBalanceDetailDao {

    @Autowired
    private BalanceDetailMapper balanceDetailMapper;

    @Override
    public int saveBalanceDetail(BalanceDetail balanceDetail){
        return balanceDetailMapper.insert(balanceDetail);
    }
}
