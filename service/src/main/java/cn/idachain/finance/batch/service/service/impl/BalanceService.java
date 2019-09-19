package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.service.dao.IBalancePersonDao;
import cn.idachain.finance.batch.common.dataobject.AccountPerson;
import cn.idachain.finance.batch.common.dataobject.BalancePerson;
import cn.idachain.finance.batch.service.service.IAccountService;
import cn.idachain.finance.batch.service.service.IBalanceService;
import cn.idachain.finance.batch.service.util.convert.AccountConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BalanceService extends BaseService implements IBalanceService {

    @Autowired
    private IBalancePersonDao balancePersonDao;
    @Autowired
    private IAccountService accountService;


    @Override
    public BalancePerson getBalance(String customerNo, String currency){
        AccountPerson acc = accountService.getCustomerAccount(customerNo,null);
        BalancePerson bal = balancePersonDao.getBalance(acc.getAccountNo(),currency);
        BalancePerson balancePerson = new BalancePerson();
        if (null == bal){
            balancePerson = AccountConvert.convertToBalancePerson(acc.getAccountNo(),currency);
            balancePersonDao.saveBalancePerson(balancePerson);
        }
        return balancePerson;
    }

    @Override
    public BalancePerson getAccBalance(String accountNo, String currency){
        BalancePerson bal = balancePersonDao.getBalance(accountNo,currency);
        BalancePerson balancePerson = new BalancePerson();
        if (null == bal){
            balancePerson = AccountConvert.convertToBalancePerson(accountNo,currency);
            balancePersonDao.saveBalancePerson(balancePerson);
        }
        return balancePerson;
    }
}
