package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.BalanceOrg;

import java.math.BigDecimal;

public interface IBalanceOrgDao {
    int updateBalance(BalanceOrg balanceOrg, BigDecimal amount);

    int updateFreeze(BalanceOrg balanceOrg, BigDecimal amount);

    BalanceOrg getBalance(String accountNo, String currency);

    int saveBalanceOrg(BalanceOrg balanceOrg);
}
