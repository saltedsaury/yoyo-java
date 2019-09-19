package cn.idachain.finance.batch.service.service;

import cn.idachain.finance.batch.common.dataobject.BalancePerson;

public interface IBalanceService {
    BalancePerson getBalance(String customerNo, String currency);

    BalancePerson getAccBalance(String accountNo, String currency);

}
