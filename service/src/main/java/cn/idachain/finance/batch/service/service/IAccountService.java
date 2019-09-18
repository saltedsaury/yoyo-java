package cn.idachain.finance.batch.service.service;

import cn.idachain.finance.batch.common.dataobject.AccountOrg;
import cn.idachain.finance.batch.common.dataobject.AccountPerson;

public interface IAccountService {
    AccountPerson getCustomerAccount(String customerNo, String currency);

    AccountOrg getOrgAccount(String customerNo, String currency, String accountType);
}
