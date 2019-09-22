package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.service.dao.IAccountInternalDao;
import cn.idachain.finance.batch.common.dataobject.AccountInternal;
import cn.idachain.finance.batch.common.mapper.AccountInternalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountInternalDao implements IAccountInternalDao {
    @Autowired
    private AccountInternalMapper accountInternalMapper;

    @Override
    public AccountInternal getAccountByTransType(String transType, String currency){
        AccountInternal condition = new AccountInternal();
        condition.setTransType(transType);
        if (null != currency) {
            condition.setCurrency(currency);
        }
        return accountInternalMapper.selectOne(condition);
    }

    @Override
    public int saveAccount(AccountInternal accountPerson){
        return accountInternalMapper.insert(accountPerson);
    }
}
