package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.BalanceOrg;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IBalanceOrgDao {
    int updateBalance(BalanceOrg balanceOrg, BigDecimal amount);

    int updateFreeze(BalanceOrg balanceOrg, BigDecimal amount);

    BalanceOrg getBalance(String accountNo, String currency);

    int saveBalanceOrg(BalanceOrg balanceOrg);

    Map<String, BigDecimal> getAllCcyBalance();

    List<BalanceOrg> getBalanceSpecial(Collection<String> accounts);
}
