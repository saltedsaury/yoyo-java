package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.enums.AccountTransType;
import cn.idachain.finance.batch.common.enums.Direction;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.service.service.IAccountService;
import cn.idachain.finance.batch.service.service.IBalanceDetialService;
import cn.idachain.finance.batch.service.util.convert.AccountConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BalanceDetailService implements IBalanceDetialService {

    @Autowired
    private IBalanceDetailDao balanceDetailDao;
    @Autowired
    private IFreezeDetailDao freezeDetailDao;
    @Autowired
    private IAccountInternalDao accountInternalDao;
    @Autowired
    private IBalancePersonDao balancePersonDao;
    @Autowired
    private IBalanceInternalDao balanceInternalDao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private IAccountService accountService;

    /**
     * 划转
     * @param customerNo
     * @param currency
     * @param direction
     * @param tradeNo
     * @param amount
     * @return
     */
    @Override
    public boolean transfer(String customerNo, String currency, String direction, String tradeNo, BigDecimal amount){
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,currency);
        AccountInternal finAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.FINANCING.getCode(),currency);

        final BalancePerson userBalance = balancePersonDao.getBalance(userAccount.getAccountNo(),currency);
        final BalanceInternal finBalance = balanceInternalDao.getBalance(finAccount.getAccountNo(),currency);

        final BigDecimal userAmount;
        final BigDecimal finAmount;

        //计算余额
        if (Direction.IN.getCode().equals(direction)){
            userAmount = userBalance.getBalance().add(amount);
            finAmount = finBalance.getBalance().add(amount);
        }else{
            userAmount = userBalance.getBalance().subtract(amount);
            finAmount = finBalance.getBalance().subtract(amount);
        }

        final BalanceDetail userDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,direction,userAccount.getAccountNo(),currency,
                amount,userBalance.getBalance(),"资金划转");
        final BalanceDetail finDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,direction,finAccount.getAccountNo(),currency,
                amount,userBalance.getBalance(),"资金划转");

        if (userAmount.subtract(userBalance.getFreeze()).compareTo(BigDecimal.ZERO)<0){
            throw new BizException(BizExceptionEnum.USER_BALANCE_NOT_ENOUGH);
        }
        if (finAmount.compareTo(BigDecimal.ZERO)<0){
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
               balanceDetailDao.saveBalanceDetail(userDetail);
               balanceDetailDao.saveBalanceDetail(finDetail);
               if (balanceInternalDao.updateBalance(finBalance,finAmount)<=0){
                   throw new BizException(BizExceptionEnum.DB_ERROR);
               }
               if(balancePersonDao.updateBalance(userBalance,userAmount)<=0){
                   throw new BizException(BizExceptionEnum.DB_ERROR);
               }
            }
        });

        return true;
    }

    /**
     * 投资冻结
     * @param customerNo
     * @param currency
     * @param tradeNo
     * @param principal
     * @param insuranceFee
     * @return
     */
    @Override
    public boolean investFreeze(String customerNo, String currency, String tradeNo, final BigDecimal principal, final BigDecimal insuranceFee){
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,currency);

        final BalancePerson userBalance = balancePersonDao.getBalance(userAccount.getAccountNo(),currency);
        if (userBalance.getBalance().subtract(userBalance.getFreeze()).compareTo(principal.add(insuranceFee))<0){
            throw new BizException(BizExceptionEnum.USER_BALANCE_NOT_ENOUGH);
        }

        final List<FreezeDetail> freezeDetails = new ArrayList<FreezeDetail>();

        FreezeDetail investFreeze = AccountConvert.convertToFreezeDetail(tradeNo,tradeNo,
                userAccount.getAccountNo(),currency,principal,new BigDecimal("0"),
                "投资金额冻结",AccountTransType.PAYABLE.getCode());
        freezeDetails.add(investFreeze);
        if (insuranceFee.compareTo(BigDecimal.ZERO)>0) {
            FreezeDetail insuranceFreeze = AccountConvert.convertToFreezeDetail(tradeNo, tradeNo,
                    userAccount.getAccountNo(), currency, insuranceFee, new BigDecimal("0"),
                    "保费冻结", AccountTransType.INSURANCE_FEE.getCode());
            freezeDetails.add(insuranceFreeze);
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                freezeDetailDao.insertBatch(freezeDetails);
                if (balancePersonDao.updateFreeze(userBalance,
                        userBalance.getFreeze().add(principal).add(insuranceFee))<=0){
                    throw new BizException(BizExceptionEnum.DB_ERROR);
                }
            }
        });

        return true;
    }

    /**
     * 短款
     * @param currency
     * @param amount
     * @param tradeNo
     * @return
     */
    @Override
    public boolean loan(String currency, final BigDecimal amount, String tradeNo){
        final AccountInternal finAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.FINANCING.getCode(),currency);
        AccountInternal loanAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.LOAN.getCode(),currency);

        final BalanceInternal finBalance = balanceInternalDao.getBalance(finAccount.getAccountNo(),currency);
        final BalanceInternal loanBalance = balanceInternalDao.getBalance(loanAccount.getAccountNo(),currency);

        final BalanceDetail finDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),finAccount.getAccountNo(),currency,
                amount,finBalance.getBalance(),"短款转入");
        final BalanceDetail loanDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),finAccount.getAccountNo(),currency,
                amount,loanBalance.getBalance(),"短款转入");

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.saveBalanceDetail(finDetail);
                balanceDetailDao.saveBalanceDetail(loanDetail);
                if (balanceInternalDao.updateBalance(finBalance,finBalance.getBalance().add(amount))<=0){
                    throw new BizException(BizExceptionEnum.DB_ERROR);
                }
                if (balanceInternalDao.updateBalance(loanBalance,loanBalance.getBalance().add(amount))<=0){
                    throw new BizException(BizExceptionEnum.DB_ERROR);
                }
            }
        });

        return true;
    }

    /**
     * 发放收益
     * @param customerNo
     * @param currency
     * @param amount
     * @param tradeNo
     * @return
     */
    @Override
    public boolean payBonus(String customerNo, String currency, BigDecimal amount, String tradeNo){
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,currency);
        AccountInternal bonusAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.BONUS.getCode(),currency);

        final BalancePerson userBalance = balancePersonDao.getBalance(userAccount.getAccountNo(),currency);
        final BalanceInternal bonusBalance = balanceInternalDao.getBalance(bonusAccount.getAccountNo(),currency);
        if (bonusBalance.getBalance().subtract(amount).compareTo(BigDecimal.ZERO)<0){
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }
        final BalanceDetail userDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),userAccount.getAccountNo(),currency,
                amount,userBalance.getBalance(),"收益发放");
        final BalanceDetail bonusDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),bonusAccount.getAccountNo(),currency,
                amount,bonusBalance.getBalance(),"收益发放");

        final BigDecimal userAmount = userBalance.getBalance().add(amount);
        final BigDecimal bonusAmount = bonusBalance.getBalance().subtract(amount);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.saveBalanceDetail(userDetail);
                balanceDetailDao.saveBalanceDetail(bonusDetail);
                if (balancePersonDao.updateBalance(userBalance,userAmount)<=0){
                    throw new BizException(BizExceptionEnum.DB_ERROR);
                }
                if (balanceInternalDao.updateBalance(bonusBalance,bonusAmount)<=0){
                    throw new BizException(BizExceptionEnum.DB_ERROR);
                }
            }
        });

        return true;
    }

    /**
     * 到期还本
     * @param customerNo
     * @param currency
     * @param amount
     * @param tradeNo
     * @return
     */
    @Override
    public boolean payPrincipal(String customerNo, String currency, BigDecimal amount, String tradeNo,String freezeCode, boolean freeze){
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,currency);
        AccountInternal payableAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.PAYABLE.getCode(),currency);

        final BalancePerson userBalance = balancePersonDao.getBalance(userAccount.getAccountNo(),currency);
        final BalanceInternal payableBalance = balanceInternalDao.getBalance(payableAccount.getAccountNo(),currency);

        final BalanceDetail userDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),userAccount.getAccountNo(),currency,
                amount,userBalance.getBalance(),"到期还本");
        final BalanceDetail payableDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),payableAccount.getAccountNo(),currency,
                amount,payableBalance.getBalance(),"到期还本");

        final BigDecimal userAmount = userBalance.getBalance().add(amount);
        final BigDecimal payableAmount = payableBalance.getBalance().subtract(amount);
        if (payableAmount.compareTo(BigDecimal.ZERO)<0){
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }

        FreezeDetail freezeDetail = null;
        BigDecimal userFreeze = BigDecimal.ZERO;
        if (freeze){
            freezeDetail = AccountConvert.convertToFreezeDetail(tradeNo,freezeCode,
                    userAccount.getAccountNo(),currency,amount,new BigDecimal("0"),
                    "理赔金额冻结",AccountTransType.COMPENSATION_IN.getCode());
            userFreeze = userBalance.getFreeze().add(amount);
        }
        FreezeDetail finalFreezeDetail = freezeDetail;
        BigDecimal finalUserFreeze = userFreeze;

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.saveBalanceDetail(userDetail);
                balanceDetailDao.saveBalanceDetail(payableDetail);
                if (balancePersonDao.updateBalance(userBalance,userAmount)<=0){
                    throw new BizException(BizExceptionEnum.DB_ERROR);
                }
                if (balanceInternalDao.updateBalance(payableBalance,payableAmount)<=0){
                    throw new BizException(BizExceptionEnum.DB_ERROR);
                }
                if (freeze){
                    freezeDetailDao.saveFreezeDetail(finalFreezeDetail);
                    if (balancePersonDao.updateFreeze(userBalance, finalUserFreeze)<=0){
                        throw new BizException(BizExceptionEnum.DB_ERROR);
                    }
                }
            }
        });

        return true;
    }

    /**
     * 理赔
     * @param customerNo
     * @param currency
     * @param compensateCcy
     * @param amount
     * @param compensateAmt
     * @param tradeNo
     * @return
     */
    @Override
    public boolean compensate(String customerNo, String currency, String compensateCcy,
                              BigDecimal amount, BigDecimal compensateAmt, String tradeNo, String freezeCode){
        //理赔收款
        AccountPerson userAccountOut = accountService.getCustomerAccount(customerNo,currency);
        AccountInternal cmpAccountIn = accountInternalDao.getAccountByTransType(
                AccountTransType.COMPENSATION_IN.getCode(),currency);
        final BalancePerson userBalanceOut = balancePersonDao.getBalance(userAccountOut.getAccountNo(),currency);
        final BalanceInternal cmpBalanceIn = balanceInternalDao.getBalance(cmpAccountIn.getAccountNo(),currency);
        final BalanceDetail userDetailOut = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),userAccountOut.getAccountNo(),currency,
                amount,userBalanceOut.getBalance(),"理赔收款");
        final BalanceDetail cmpDetailIn = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),cmpAccountIn.getAccountNo(),currency,
                amount,cmpBalanceIn.getBalance(),"理赔收款");
        List<FreezeDetail>  freezeDetails = freezeDetailDao.getFreezeByCode(freezeCode);
        //todo 需要加金额校验以及冻结信息正确
        List<FreezeDetail>  unfreezeDetails = new ArrayList<>();
        for (FreezeDetail detail : freezeDetails){
            final FreezeDetail unFreeze = AccountConvert.convertToFreezeDetail(tradeNo,freezeCode,
                    detail.getAccountNo(),detail.getCurrency(),new BigDecimal("0"),detail.getFreezeAmt(),
                    "解冻扣款",detail.getFreezeType());
            unfreezeDetails.add(unFreeze);
        }
        final BigDecimal userAmountOut = userBalanceOut.getBalance().subtract(amount);
        final BigDecimal cmpAmountIn = cmpBalanceIn.getBalance().add(amount);
        final BigDecimal userFreeze = userBalanceOut.getFreeze().subtract(amount);
        //理赔支付
        AccountPerson userAccountIn = accountService.getCustomerAccount(customerNo,compensateCcy);
        AccountInternal cmpAccountOut = accountInternalDao.getAccountByTransType(
                AccountTransType.COMPENSATION_OUT.getCode(),compensateCcy);
        final BalancePerson userBalanceIn = balancePersonDao.getBalance(userAccountIn.getAccountNo(),compensateCcy);
        final BalanceInternal cmpBalanceOut = balanceInternalDao.getBalance(cmpAccountOut.getAccountNo(),compensateCcy);
        final BalanceDetail userDetailIn = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),userAccountIn.getAccountNo(),currency,
                compensateAmt,userBalanceIn.getBalance(),"理赔放款");
        final BalanceDetail cmpDetailOut = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),cmpAccountOut.getAccountNo(),currency,
                compensateAmt,cmpBalanceIn.getBalance(),"理赔放款");
        final BigDecimal userAmountIn = userBalanceIn.getBalance().add(compensateAmt);
        final BigDecimal cmpAmountOut = cmpBalanceOut.getBalance().add(compensateAmt);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.saveBalanceDetail(userDetailIn);
                balanceDetailDao.saveBalanceDetail(userDetailOut);
                balanceDetailDao.saveBalanceDetail(cmpDetailIn);
                balanceDetailDao.saveBalanceDetail(cmpDetailOut);
                freezeDetailDao.insertBatch(unfreezeDetails);
                balancePersonDao.updateBalance(userBalanceIn,userAmountIn);
                balancePersonDao.updateBalance(userBalanceOut,userAmountOut);
                balancePersonDao.updateFreeze(userBalanceOut,userFreeze);
                balanceInternalDao.updateBalance(cmpBalanceIn,cmpAmountIn);
                balanceInternalDao.updateBalance(cmpBalanceOut,cmpAmountOut);
            }
        });

        return true;
    }

    /**
     * 投资解冻扣款
     * @param tradeNo
     * @return
     */
    @Override
    public boolean invest(String tradeNo){
        final List<FreezeDetail>  freezeDetails = freezeDetailDao.getFreezeByCode(tradeNo);
        final List<BalanceDetail> balanceDetails = new ArrayList<BalanceDetail>();
        List<FreezeDetail> unfreezeDetails = new ArrayList<FreezeDetail>();
        BalancePerson userBal = null;
        BigDecimal userAmount = BigDecimal.ZERO;
        final Map<BalanceInternal,BigDecimal> map = new HashMap<BalanceInternal,BigDecimal>();
        for (FreezeDetail detail : freezeDetails){
            AccountInternal acc = accountInternalDao.getAccountByTransType(detail.getFreezeType(),detail.getCurrency());
            BalanceInternal bal = balanceInternalDao.getBalance(acc.getAccountNo(),acc.getCurrency());
            userBal = balancePersonDao.getBalance(detail.getAccountNo(),detail.getCurrency());
            userAmount = userAmount.add(detail.getFreezeAmt());
            map.put(bal,detail.getFreezeAmt());

            final BalanceDetail userDtl = AccountConvert.convertToBalanceDetail(
                    tradeNo,Direction.OUT.getCode(),detail.getAccountNo(),detail.getCurrency(),
                    detail.getFreezeAmt(),userBal.getBalance(),"投资扣款");
            final BalanceDetail finDtl = AccountConvert.convertToBalanceDetail(
                    tradeNo,Direction.IN.getCode(),acc.getAccountNo(),acc.getCurrency(),
                    detail.getFreezeAmt(),bal.getBalance(),"投资收款");
            final FreezeDetail unFreeze = AccountConvert.convertToFreezeDetail(tradeNo,tradeNo,
                    detail.getAccountNo(),detail.getCurrency(),new BigDecimal("0"),detail.getFreezeAmt(),
                    "解冻扣款",detail.getFreezeType());
            balanceDetails.add(userDtl);
            balanceDetails.add(finDtl);
            unfreezeDetails.add(unFreeze);
        }

        final BalancePerson finalUserBal = userBal;
        final BigDecimal finalUserAmount = userAmount;
        final BalancePerson finalUserBal1 = userBal;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.insertBatch(balanceDetails);
                freezeDetailDao.insertBatch(unfreezeDetails);
                balancePersonDao.updateBalance(finalUserBal, finalUserBal1.getBalance().subtract(finalUserAmount));
                balancePersonDao.updateFreeze(finalUserBal, finalUserBal1.getFreeze().subtract(finalUserAmount));

                map.forEach( (k,v)->{
                    balanceInternalDao.updateBalance(k,k.getBalance().add(v));
                } );
            }
        });
        return true;
    }

    /**
     * 提前赎回
     * @param customerNo
     * @param currency
     * @param tradeNo
     * @param amount
     * @param fine
     * @param bonus
     * @return
     */
    @Override
    public boolean redemption(String customerNo,String currency, String tradeNo, BigDecimal amount, BigDecimal fine, BigDecimal bonus){
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,currency);
        AccountInternal bonusAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.BONUS.getCode(),currency);
        AccountInternal fineAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.FINE.getCode(),currency);
        AccountInternal payableAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.PAYABLE.getCode(),currency);

        final BalancePerson userBalance = balancePersonDao.getBalance(userAccount.getAccountNo(),currency);
        final BalanceInternal bonusBalance = balanceInternalDao.getBalance(bonusAccount.getAccountNo(),currency);
        final BalanceInternal fineBalance = balanceInternalDao.getBalance(fineAccount.getAccountNo(),currency);
        final BalanceInternal payableBalance = balanceInternalDao.getBalance(payableAccount.getAccountNo(),currency);

        BigDecimal actralAmount = amount.subtract(bonus).subtract(fine);
        List<BalanceDetail>  details = new ArrayList<>();
        BalanceDetail userDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),userAccount.getAccountNo(),currency,
                actralAmount,userBalance.getBalance(),"提前赎回");
        BalanceDetail bonusDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),bonusAccount.getAccountNo(),currency,
                bonus,bonusBalance.getBalance(),"提前赎回");
        BalanceDetail fineDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),fineAccount.getAccountNo(),currency,
                fine,fineBalance.getBalance(),"提前赎回");
        BalanceDetail payableDetail1 = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),payableAccount.getAccountNo(),currency,
                actralAmount,payableBalance.getBalance(),"提前赎回");
        BalanceDetail payableDetail2 = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),payableAccount.getAccountNo(),currency,
                bonus,payableBalance.getBalance(),"提前赎回");
        BalanceDetail payableDetail3 = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),payableAccount.getAccountNo(),currency,
                fine,payableBalance.getBalance(),"提前赎回");
        details.add(fineDetail);
        details.add(bonusDetail);
        details.add(userDetail);
        details.add(payableDetail1);
        details.add(payableDetail2);
        details.add(payableDetail3);

        final BigDecimal userAmount = userBalance.getBalance().add(actralAmount);
        final BigDecimal fineAmount = fineBalance.getBalance().add(fine);
        final BigDecimal bonusAmount = bonusBalance.getBalance().subtract(bonus);
        final BigDecimal payableAmount = payableBalance.getBalance().subtract(amount);
        if (bonusAmount.compareTo(BigDecimal.ZERO)<0){
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }
        if (payableAmount.compareTo(BigDecimal.ZERO)<0){
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.insertBatch(details);
                balancePersonDao.updateBalance(userBalance,userAmount);
                balanceInternalDao.updateBalance(bonusBalance,bonusAmount);
                balanceInternalDao.updateBalance(fineBalance,fineAmount);
                balanceInternalDao.updateBalance(payableBalance,payableAmount);
            }
        });

        return true;
    }

    @Override
    public boolean giveUpCompensation(String freezeCode,String tradeNo,String customerNo,String currency){
        AccountPerson accountPerson = accountService.getCustomerAccount(customerNo,currency);
        BalancePerson balancePerson = balancePersonDao.getBalance(accountPerson.getAccountNo(),currency);

        List<FreezeDetail>  freezeDetails = freezeDetailDao.getFreezeByCode(freezeCode);
        //todo 需要加金额校验以及冻结信息正确
        List<FreezeDetail>  unfreezeDetails = new ArrayList<>();
        BigDecimal freeze = balancePerson.getFreeze();
        for (FreezeDetail detail : freezeDetails){
            final FreezeDetail unFreeze = AccountConvert.convertToFreezeDetail(tradeNo,freezeCode,
                    detail.getAccountNo(),detail.getCurrency(),new BigDecimal("0"),detail.getFreezeAmt(),
                    "解冻扣款",detail.getFreezeType());
            freeze = freeze.subtract(detail.getFreezeAmt());
            if (freeze.compareTo(BigDecimal.ZERO)<0){
                throw new BizException(BizExceptionEnum.USER_BALANCE_NOT_ENOUGH);
            }
            unfreezeDetails.add(unFreeze);
        }
        BigDecimal finalFreeze = freeze;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                freezeDetailDao.insertBatch(unfreezeDetails);
                balancePersonDao.updateFreeze(balancePerson, finalFreeze);

            }
        });
        return true;
    }
}
