package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.service.dao.IAccountPersonDao;
import cn.idachain.finance.batch.common.dataobject.AccountPerson;
import cn.idachain.finance.batch.common.mapper.AccountPersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountPersonDao implements IAccountPersonDao {
    @Autowired
    private AccountPersonMapper accountPersonMapper;

    @Override
    public AccountPerson getAccountByCustomerNo(String customerNo, String currency){
        AccountPerson condition = new AccountPerson();
        condition.setCustomerNo(customerNo);
        if (null != currency){
            condition.setCurrency(currency);
        }
        return accountPersonMapper.selectOne(condition);
    }

    @Override
    public int saveAccount(AccountPerson accountPerson){
        return accountPersonMapper.insert(accountPerson);
    }
}
