package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.BalancePerson;

import java.math.BigDecimal;

public interface IBalancePersonDao {
    int updateBalance(BalancePerson balancePerson, BigDecimal amount);

    int updateFreeze(BalancePerson balancePerson, BigDecimal amount);

    BalancePerson getBalance(String accountNo, String currency);

    int saveBalancePerson(BalancePerson balancePerson);
}
