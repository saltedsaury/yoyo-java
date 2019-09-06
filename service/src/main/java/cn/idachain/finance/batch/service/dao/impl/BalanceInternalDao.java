package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.BalanceInternal;
import cn.idachain.finance.batch.common.mapper.BalanceInternalMapper;
import cn.idachain.finance.batch.service.dao.IBalanceInternalDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceInternalDao implements IBalanceInternalDao {

    @Autowired
    private BalanceInternalMapper balanceInternalMapper;

    @Override
    public int updateBalance(BalanceInternal balancePerson, BigDecimal amount){
        EntityWrapper<BalanceInternal> condition = new EntityWrapper<BalanceInternal>();
        condition.eq("account_no",balancePerson.getAccountNo());
        condition.eq("balance",balancePerson.getBalance());

        BalanceInternal newBalance = new BalanceInternal();
        newBalance.setBalance(amount);
        return balanceInternalMapper.update(newBalance,condition);
    }

    @Override
    public int updateFreeze(BalanceInternal balancePerson, BigDecimal amount){
        EntityWrapper<BalanceInternal> condition = new EntityWrapper<BalanceInternal>();
        condition.eq("account_no",balancePerson.getAccountNo());
        condition.eq("currency",balancePerson.getCurrency());
        condition.eq("freeze",balancePerson.getFreeze());

        BalanceInternal newBalance = new BalanceInternal();
        newBalance.setFreeze(amount);
        return balanceInternalMapper.update(newBalance,condition);
    }

    @Override
    public BalanceInternal getBalance(String accountNo, String currency){
        BalanceInternal condition = new BalanceInternal();
        condition.setAccountNo(accountNo);
        condition.setCurrency(currency);

        return balanceInternalMapper.selectOne(condition);
    }
}
