package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.AccountInternal;

public interface IAccountInternalDao {
    AccountInternal getAccountByTransType(String transType, String currency);

    int saveAccount(AccountInternal accountPerson);
}
