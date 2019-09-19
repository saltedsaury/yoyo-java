package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.AccountOrg;
import cn.idachain.finance.batch.common.dataobject.AccountPerson;
import cn.idachain.finance.batch.common.dataobject.BalancePerson;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.service.dao.IAccountOrgDao;
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
    @Autowired
    private IAccountOrgDao accountOrgDao;

    @Override
    public AccountPerson getCustomerAccount(String customerNo, String currency){
        AccountPerson accountPerson =  accountPersonDao.getAccountByCustomerNo(customerNo,currency);
        if (null != accountPerson){
            return accountPerson;
        }
        accountPerson = AccountConvert.convertToAccountPerson(customerNo,currency);

        AccountPerson finalAccountPerson = accountPerson;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                accountPersonDao.saveAccount(finalAccountPerson);
            }
        });

        return accountPerson;

    }

    @Override
    public AccountOrg getOrgAccount(String customerNo, String currency, String accountType){
        AccountOrg accountOrg =  accountOrgDao.getOrgAccountByCustomerNo(customerNo,currency,accountType);
        if (null == accountOrg){
            throw new BizException(BizExceptionEnum.ORG_ACCOUNT_NOT_EXIST);
        }

        return accountOrg;

    }
}
