package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.BalancePerson;
import cn.idachain.finance.batch.common.mapper.BalancePersonMapper;
import cn.idachain.finance.batch.service.dao.IBalancePersonDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalancePersonDao implements IBalancePersonDao {

    @Autowired
    private BalancePersonMapper balancePersonMapper;

    @Override
    public int updateBalance(BalancePerson balancePerson, BigDecimal amount){
        EntityWrapper<BalancePerson> condition = new EntityWrapper<BalancePerson>();
        condition.eq("account_no",balancePerson.getAccountNo());
        condition.eq("balance",balancePerson.getBalance());
        condition.eq("currency",balancePerson.getCurrency());

        BalancePerson newBalance = new BalancePerson();
        newBalance.setBalance(amount);
        return balancePersonMapper.update(newBalance,condition);
    }

    @Override
    public int updateFreeze(BalancePerson balancePerson, BigDecimal amount){
        EntityWrapper<BalancePerson> condition = new EntityWrapper<BalancePerson>();
        condition.eq("account_no",balancePerson.getAccountNo());
        condition.eq("currency",balancePerson.getCurrency());
        condition.eq("freeze",balancePerson.getFreeze());

        BalancePerson newBalance = new BalancePerson();
        newBalance.setFreeze(amount);
        return balancePersonMapper.update(newBalance,condition);
    }

    @Override
    public BalancePerson getBalance(String accountNo,String currency){
        BalancePerson condition = new BalancePerson();
        condition.setAccountNo(accountNo);
        condition.setCurrency(currency);

        return balancePersonMapper.selectOne(condition);
    }

    @Override
    public int saveBalancePerson(BalancePerson balancePerson){
        return balancePersonMapper.insert(balancePerson);
    }
}
