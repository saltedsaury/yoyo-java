package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.AccountOrg;
import cn.idachain.finance.batch.common.mapper.AccountOrgMapper;
import cn.idachain.finance.batch.service.dao.IAccountOrgDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountOrgDao implements IAccountOrgDao {

    @Autowired
    private AccountOrgMapper accountOrgMapper;

    @Override
    public List<AccountOrg> getAccountListByType(String accountType){
        EntityWrapper<AccountOrg> wrapper = new EntityWrapper<>();
        wrapper.eq("account_type",accountType);
        return accountOrgMapper.selectList(wrapper);
    }

    @Override
    public AccountOrg getOrgAccountByAccNo(String currency,String accountNo) {
        AccountOrg condition = new AccountOrg();
        if (null != currency) {
            condition.setCurrency(currency);
        }
        condition.setAccountNo(accountNo);
        return accountOrgMapper.selectOne(condition);
    }
}
