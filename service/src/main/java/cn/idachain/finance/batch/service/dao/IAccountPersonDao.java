package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.AccountPerson;

public interface IAccountPersonDao {
    AccountPerson getAccountByCustomerNo(String customerNo, String currency);

    int saveAccount(AccountPerson accountPerson);
}
