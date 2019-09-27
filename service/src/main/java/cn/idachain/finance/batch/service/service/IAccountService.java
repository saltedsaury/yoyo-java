package cn.idachain.finance.batch.service.service;

import cn.idachain.finance.batch.common.dataobject.AccountOrg;
import cn.idachain.finance.batch.common.dataobject.AccountPerson;

public interface IAccountService {
    AccountPerson getCustomerAccount(String customerNo, String currency);

    AccountOrg getOrgAccount(String currency, String accountNo);

    AccountOrg getOrgAccountByProd(String prodNo, String accountType);

    AccountOrg getOrgAccByInsurance(String insuranceNo, String accountType);
}
