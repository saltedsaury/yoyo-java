package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.AccountOrg;

import java.util.List;

public interface IAccountOrgDao {
    List<AccountOrg> getAccountListByType(String accountType);

    AccountOrg getOrgAccountByAccNo(String currency, String accountNo);
}
