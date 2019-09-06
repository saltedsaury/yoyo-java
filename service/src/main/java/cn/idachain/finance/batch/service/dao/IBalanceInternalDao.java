package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.BalanceInternal;

import java.math.BigDecimal;

public interface IBalanceInternalDao {
    int updateBalance(BalanceInternal balancePerson, BigDecimal amount);

    int updateFreeze(BalanceInternal balancePerson, BigDecimal amount);

    BalanceInternal getBalance(String accountNo, String currency);
}
