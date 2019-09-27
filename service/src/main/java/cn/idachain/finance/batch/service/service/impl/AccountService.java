package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.service.dao.*;
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
    private TransactionTemplate transactionTemplate;
    @Autowired
    private IAccountOrgDao accountOrgDao;
    @Autowired
    private IAccProdDao accProdDao;
    @Autowired
    private IAccInsuranceDao accInsuranceDao;

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
    public AccountOrg getOrgAccount(String currency, String accountNo){
        AccountOrg accountOrg =  accountOrgDao.getOrgAccountByAccNo(currency,accountNo);
        if (null == accountOrg){
            throw new BizException(BizExceptionEnum.ORG_ACCOUNT_NOT_EXIST);
        }

        return accountOrg;

    }

    @Override
    public AccountOrg getOrgAccountByProd(String prodNo, String accountType){
        AccProd accProd = accProdDao.getAccByProd(prodNo,accountType);
        if (null == accProd){
            throw new BizException(BizExceptionEnum.ORG_ACCOUNT_NOT_EXIST);
        }
        AccountOrg accountOrg = accountOrgDao.getOrgAccountByAccNo(null,accProd.getAccountNo());
        return accountOrg;
    }

    @Override
    public AccountOrg getOrgAccByInsurance(String insuranceNo, String accountType){
        AccInsurance accInsurance = accInsuranceDao.getAccByInsurance(insuranceNo,accountType);
        if (null == accInsurance){
            throw new BizException(BizExceptionEnum.ORG_ACCOUNT_NOT_EXIST);
        }
        AccountOrg accountOrg = accountOrgDao.getOrgAccountByAccNo(null,accInsurance.getAccountNo());
        return accountOrg;
    }
}
