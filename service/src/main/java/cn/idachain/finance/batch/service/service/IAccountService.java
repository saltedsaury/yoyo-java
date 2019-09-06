package cn.idachain.finance.batch.service.service;

import cn.idachain.finance.batch.common.dataobject.AccountPerson;

public interface IAccountService {
    AccountPerson getCustomerAccount(String customerNo, String currency);
}
