package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.AccountOrg;

import java.util.List;

public interface IAccountOrgDao {
    List<AccountOrg> getAccountListByType(String accountType);

    AccountOrg getOrgAccountByCustomerNo(String customerNo, String currency, String accountType);
}
