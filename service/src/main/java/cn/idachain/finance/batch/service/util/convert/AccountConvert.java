package cn.idachain.finance.batch.service.util.convert;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.enums.AccountType;
import cn.idachain.finance.batch.service.util.GenerateIdUtil;

import java.math.BigDecimal;

public class AccountConvert {

    public static BalanceDetail convertToBalanceDetail (String tradeNo, String direction,
                                                        String accountNo, String currency,
                                                        BigDecimal amount, BigDecimal beforeBal,
                                                        AccountType accountType, String remark){
        BalanceDetail detail = new BalanceDetail();
        detail.setAccountType(accountType.name());
        detail.setBizNo(Long.toString(GenerateIdUtil.getId(GenerateIdUtil.ModuleEnum.ACCOUNTDETAIL)));
        detail.setTradeNo(tradeNo);
        detail.setTransType(direction);
        detail.setAccountNo(accountNo);
        detail.setCurrency(currency);
        detail.setAmount(amount);
        detail.setBeforeBal(beforeBal);
        detail.setStatus("0");
        detail.setRemark(remark);

        return detail;
    }

    public static FreezeDetail convertToFreezeDetail (String tradeNo, String freezeCode,
                                                      String accountNo, String currency,
                                                      BigDecimal amount, BigDecimal unFreeze,
                                                      String remark,String freezeType){

        FreezeDetail detail = new FreezeDetail();
        detail.setBizNo(Long.toString(GenerateIdUtil.getId(GenerateIdUtil.ModuleEnum.ACCOUNTDETAIL)));
        detail.setTradeNo(tradeNo);
        detail.setFreezeCode(freezeCode);
        detail.setAccountNo(accountNo);
        detail.setCurrency(currency);
        detail.setFreezeAmt(amount);
        detail.setUnfreezeAmt(unFreeze);
        detail.setFreezeType(freezeType);
        detail.setStatus("0");
        detail.setRemark(remark);


        return detail;
    }

    public static AccountPerson convertToAccountPerson(String customerNo,String currency){
        AccountPerson accountPerson = new AccountPerson();
        accountPerson.setCustomerNo(customerNo);
        accountPerson.setAccountNo(Long.toString(GenerateIdUtil.getId(GenerateIdUtil.ModuleEnum.ACCOUNTDETAIL)));
        if (null == currency){
            accountPerson.setCurrency("DEFAULT");
        }else {
            accountPerson.setCurrency(currency);
        }
        accountPerson.setAccountType("FINANCING");
        accountPerson.setRemark("");

        return accountPerson;
    }

    public static BalancePerson convertToBalancePerson(String accountNo,String currency){
        BalancePerson balancePerson = new BalancePerson();
        balancePerson.setAccountNo(accountNo);
        balancePerson.setCurrency(currency);
        balancePerson.setBalance(BigDecimal.ZERO);
        balancePerson.setFreeze(BigDecimal.ZERO);
        balancePerson.setStatus("0");
        balancePerson.setRemark("");

        return balancePerson;
    }

    public static BalanceOrg convertToBalanceOrg(String accountNo, String currency) {
        BalanceOrg balanceOrg = new BalanceOrg();
        balanceOrg.setAccountNo(accountNo);
        balanceOrg.setCurrency(currency);
        balanceOrg.setBalance(BigDecimal.ZERO);
        balanceOrg.setFreeze(BigDecimal.ZERO);
        balanceOrg.setStatus("0");
        balanceOrg.setRemark("");
        return balanceOrg;
    }
}
