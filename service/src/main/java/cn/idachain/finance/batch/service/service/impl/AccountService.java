package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.AccountPerson;
import cn.idachain.finance.batch.common.dataobject.BalancePerson;
import cn.idachain.finance.batch.service.dao.IAccountPersonDao;
import cn.idachain.finance.batch.service.dao.IBalancePersonDao;
import cn.idachain.finance.batch.service.service.IAccountService;
import cn.idachain.finance.batch.service.util.convert.AccountConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class AccountService implements IAccountService {

    @Autowired
    private IAccountPersonDao accountPersonDao;
    @Autowired
    private IBalancePersonDao balancePersonDao;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public AccountPerson getCustomerAccount(String customerNo, String currency){
        AccountPerson accountPerson =  accountPersonDao.getAccountByCustomerNo(customerNo,currency);
        if (null != accountPerson){
            return accountPerson;
        }
        accountPerson = AccountConvert.convertToAccountPerson(customerNo,currency);
        BalancePerson balancePerson = AccountConvert.convertToBalancePerson(accountPerson.getAccountNo(),currency);

        AccountPerson finalAccountPerson = accountPerson;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                accountPersonDao.saveAccount(finalAccountPerson);
                balancePersonDao.saveBalancePerson(balancePerson);
            }
        });

        return accountPerson;

    }
}
