package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.BalancePerson;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IBalancePersonDao {
    int updateBalance(BalancePerson balancePerson, BigDecimal amount);

    int updateFreeze(BalancePerson balancePerson, BigDecimal amount);

    BalancePerson getBalance(String accountNo, String currency);

    int saveBalancePerson(BalancePerson balancePerson);

    Map<String, BigDecimal> getAllCcyBalance();

    List<BalancePerson> getBalanceSpecial(Collection<String> accounts);
}
