package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.*;
import cn.idachain.finance.batch.common.enums.AccountTransType;
import cn.idachain.finance.batch.common.enums.Direction;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.common.exception.TryAgainException;
import cn.idachain.finance.batch.service.dao.*;
import cn.idachain.finance.batch.service.service.IAccountService;
import cn.idachain.finance.batch.service.service.IBalanceDetialService;
import cn.idachain.finance.batch.service.service.IBalanceService;
import cn.idachain.finance.batch.service.util.convert.AccountConvert;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    @Autowired
    private IBalanceOrgDao balanceOrgDao;
    @Autowired
    private IBalanceService balanceService;

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
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,null);
        AccountInternal finAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.FINANCING.getCode(),null);

        final BalancePerson userBalance = balanceService.getAccBalance(userAccount.getAccountNo(),currency);
        log.info("get user balance :{}",userBalance);
        final BalanceInternal finBalance = balanceInternalDao.getBalance(finAccount.getAccountNo(),currency);
        log.info("get financing balance :{}",finBalance);
        final BigDecimal userAmount;
        final BigDecimal finAmount;

        //计算余额
        if (Direction.IN.getCode().equals(direction)){
            log.info("transfer in amount :{}",amount);
            userAmount = userBalance.getBalance().add(amount);
            finAmount = finBalance.getBalance().add(amount);
        }else{
            log.info("transfer out amount :{}",amount);
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
            log.error("user balance is not enough,user id:{}",customerNo);
            throw new BizException(BizExceptionEnum.USER_BALANCE_NOT_ENOUGH);
        }
        if (finAmount.compareTo(BigDecimal.ZERO)<0){
            log.error("financing balance is not enough.");
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
               balanceDetailDao.saveBalanceDetail(userDetail);
               balanceDetailDao.saveBalanceDetail(finDetail);
               if (balanceInternalDao.updateBalance(finBalance,finAmount)<=0){
                   log.error("update financing balance error");
                   throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
               }
               if(balancePersonDao.updateBalance(userBalance,userAmount)<=0){
                   log.error("update user balance error,user id :{}",customerNo);
                   throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
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
   /* @Override
    public boolean investFreeze(String customerNo, String currency, String tradeNo, final BigDecimal principal, final BigDecimal insuranceFee){
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,null);

        final BalancePerson userBalance = balanceService.getAccBalance(userAccount.getAccountNo(),currency);
        log.info("get user balance :{}",userBalance);

        if (userBalance.getBalance().subtract(userBalance.getFreeze()).compareTo(principal.add(insuranceFee))<0){
            log.error("user balance is not enough,user id:{}",customerNo);
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
                    log.error("freeze user balance error,user id:{}",customerNo);
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
                }
            }
        });

        return true;
    }*/

    /**
     * 短款
     * @param currency
     * @param amount
     * @param tradeNo
     * @return
     */
    /*@Override
    public boolean loan(String currency, final BigDecimal amount, String tradeNo){
        final AccountInternal finAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.FINANCING.getCode(),null);
        AccountInternal loanAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.LOAN.getCode(),null);

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
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);                }
                if (balanceInternalDao.updateBalance(loanBalance,loanBalance.getBalance().add(amount))<=0){
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);                }
            }
        });

        return true;
    }*/

    /**
     * 发放收益
     * @param customerNo
     * @param currency
     * @param amount
     * @param tradeNo
     * @return
     */
    @Override
    public boolean payBonus(String customerNo, String currency, BigDecimal amount, String tradeNo,String prod){
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,null);
        AccountOrg accOrg = accountService.getOrgAccountByProd(prod,AccountTransType.FINANCING.getCode());

        final BalancePerson userBalance = balanceService.getAccBalance(userAccount.getAccountNo(),currency);
        log.info("get user balance :{}",userBalance);

        final BalanceOrg bonusBalance = balanceOrgDao.getBalance(accOrg.getAccountNo(),currency);
        log.info("get bonus account balance :{}",bonusBalance);

        if (bonusBalance.getBalance().subtract(amount).compareTo(BigDecimal.ZERO)<0){
            log.error("bonus account balance is not enough.");
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }
        final BalanceDetail userDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),userAccount.getAccountNo(),currency,
                amount,userBalance.getBalance(),"收益发放");
        final BalanceDetail bonusDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),accOrg.getAccountNo(),currency,
                amount,bonusBalance.getBalance(),"收益发放");

        final BigDecimal userAmount = userBalance.getBalance().add(amount);
        final BigDecimal bonusAmount = bonusBalance.getBalance().subtract(amount);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.saveBalanceDetail(userDetail);
                balanceDetailDao.saveBalanceDetail(bonusDetail);
                if (balancePersonDao.updateBalance(userBalance,userAmount)<=0){
                    log.error("update user balance error,user id {}",customerNo);
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
                }
                if (balanceOrgDao.updateBalance(bonusBalance,bonusAmount)<=0){
                    log.error("update bonus account balance error");
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);                }
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
    public boolean payPrincipal(String customerNo, String currency, BigDecimal amount,
                                String tradeNo,String freezeCode, boolean freeze,String prod){
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,null);
        AccountOrg accountOrg = accountService.getOrgAccountByProd(prod,AccountTransType.FINANCING.getCode());

        final BalancePerson userBalance = balanceService.getAccBalance(userAccount.getAccountNo(),currency);
        log.info("get user balance :{}",userBalance);

        final BalanceOrg payableBalance = balanceOrgDao.getBalance(accountOrg.getAccountNo(),currency);
        log.info("get payable account balance :{}",payableBalance);

        final BalanceDetail userDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),userAccount.getAccountNo(),currency,
                amount,userBalance.getBalance(),"到期还本");
        final BalanceDetail payableDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),accountOrg.getAccountNo(),currency,
                amount,payableBalance.getBalance(),"到期还本");

        final BigDecimal userAmount = userBalance.getBalance().add(amount);
        final BigDecimal payableAmount = payableBalance.getBalance().subtract(amount);
        if (payableAmount.compareTo(BigDecimal.ZERO)<0){
            log.error("payable account balance is not enough.");
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }

        FreezeDetail freezeDetail = null;
        BigDecimal userFreeze = BigDecimal.ZERO;
        if (freeze){
            freezeDetail = AccountConvert.convertToFreezeDetail(tradeNo,freezeCode,
                    userAccount.getAccountNo(),currency,amount,new BigDecimal("0"),
                    "理赔金额冻结",AccountTransType.INSURANCE.getCode());
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
                    log.error("update user balance error,user id:{}",customerNo);
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
                }
                if (balanceOrgDao.updateBalance(payableBalance,payableAmount)<=0){
                    log.error("update payable account balance error");
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
                }
                if (freeze){
                    freezeDetailDao.saveFreezeDetail(finalFreezeDetail);
                    if (balancePersonDao.updateFreeze(userBalance, finalUserFreeze)<=0){
                        log.error("freeze user balance error,user id:{}",customerNo);
                        throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
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
    public boolean compensate(String customerNo, String currency, String compensateCcy,BigDecimal amount,
                              BigDecimal compensateAmt, String tradeNo, String freezeCode, String insuranceNo){
        //理赔收款
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,null);
        AccountOrg accountOrg = accountService.getOrgAccByInsurance(insuranceNo,AccountTransType.INSURANCE.getCode());
        final BalancePerson userBalanceOut = balanceService.getAccBalance(userAccount.getAccountNo(),currency);
        log.info("get user balance out :{}",userBalanceOut);
        final BalanceOrg cmpBalanceIn = balanceOrgDao.getBalance(accountOrg.getAccountNo(),currency);
        log.info("get compensate account balance in :{}",cmpBalanceIn);
        final BalanceDetail userDetailOut = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),userAccount.getAccountNo(),currency,
                amount,userBalanceOut.getBalance(),"理赔收款");
        final BalanceDetail cmpDetailIn = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),accountOrg.getAccountNo(),currency,
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
        final BalancePerson userBalanceIn = balanceService.getAccBalance(userAccount.getAccountNo(),compensateCcy);
        log.info("get user balance in :{}",userBalanceIn);
        final BalanceOrg cmpBalanceOut = balanceOrgDao.getBalance(accountOrg.getAccountNo(),compensateCcy);
        log.info("get compensate account balance out :{}",cmpBalanceOut);
        final BalanceDetail userDetailIn = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),userAccount.getAccountNo(),compensateCcy,
                compensateAmt,userBalanceIn.getBalance(),"理赔放款");
        final BalanceDetail cmpDetailOut = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),accountOrg.getAccountNo(),compensateCcy,
                compensateAmt,cmpBalanceIn.getBalance(),"理赔放款");
        final BigDecimal userAmountIn = userBalanceIn.getBalance().add(compensateAmt);
        final BigDecimal cmpAmountOut = cmpBalanceOut.getBalance().subtract(compensateAmt);
        if (cmpAmountOut.compareTo(BigDecimal.ZERO)<0){
            log.error("compensate out account balance is not enough.");
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }
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
                balanceOrgDao.updateBalance(cmpBalanceIn,cmpAmountIn);
                balanceOrgDao.updateBalance(cmpBalanceOut,cmpAmountOut);
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
    public boolean invest(String tradeNo,String prod){
        final List<FreezeDetail>  freezeDetails = freezeDetailDao.getFreezeByCode(tradeNo);
        final List<BalanceDetail> balanceDetails = new ArrayList<BalanceDetail>();
        List<FreezeDetail> unfreezeDetails = new ArrayList<FreezeDetail>();
        BalancePerson userBal = null;
        BigDecimal userAmount = BigDecimal.ZERO;
        final Map<BalanceOrg,BigDecimal> map = new HashMap<BalanceOrg,BigDecimal>();
        for (FreezeDetail detail : freezeDetails){
            AccountOrg acc = accountService.getOrgAccountByProd(prod,detail.getFreezeType());
            BalanceOrg bal = balanceOrgDao.getBalance(acc.getAccountNo(),detail.getCurrency());
            log.info("get biz balance :{}",bal);
            userBal = balanceService.getAccBalance(detail.getAccountNo(),detail.getCurrency());
            userAmount = userAmount.add(detail.getFreezeAmt());
            map.merge(bal, detail.getFreezeAmt(), BigDecimal::add);

            final BalanceDetail finDtl = AccountConvert.convertToBalanceDetail(
                    tradeNo,Direction.IN.getCode(),acc.getAccountNo(),detail.getCurrency(),
                    detail.getFreezeAmt(),bal.getBalance(),"投资收款");
            final FreezeDetail unFreeze = AccountConvert.convertToFreezeDetail(tradeNo,tradeNo,
                    detail.getAccountNo(),detail.getCurrency(),new BigDecimal("0"),detail.getFreezeAmt(),
                    "解冻扣款",detail.getFreezeType());
            balanceDetails.add(finDtl);
            unfreezeDetails.add(unFreeze);
        }
        log.info("get user balance :{}",userBal);
        final BalanceDetail userDtl = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),freezeDetails.get(0).getAccountNo(),
                freezeDetails.get(0).getCurrency(),userAmount,userBal.getBalance(),
                "投资扣款");
        balanceDetails.add(userDtl);

        final BalancePerson finalUserBal = userBal;
        final BigDecimal finalUserAmount = userAmount;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.insertBatch(balanceDetails);
                freezeDetailDao.insertBatch(unfreezeDetails);
                if(balancePersonDao.updateBalance(finalUserBal, finalUserBal.getBalance().subtract(finalUserAmount))<=0){
                    log.error("update user balance error");
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
                }
                if(balancePersonDao.updateFreeze(finalUserBal, finalUserBal.getFreeze().subtract(finalUserAmount))<=0){
                    log.error("unfreeze user balance error,account no:{}",finalUserBal.getAccountNo());
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
                }
                map.forEach((k,v)->{
                    if(balanceOrgDao.updateBalance(k,k.getBalance().add(v))<=0){
                        log.error("update internal account {} balance error",k.getAccountNo());
                        throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
                    }
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
    public boolean redemption(String customerNo,String currency, String tradeNo, BigDecimal amount,
                              BigDecimal fine, BigDecimal bonus, String prod){
        AccountPerson userAccount = accountService.getCustomerAccount(customerNo,null);
        AccountOrg accFin = accountService.getOrgAccountByProd(prod,AccountTransType.FINANCING.getCode());
        AccountOrg accFee = accountService.getOrgAccountByProd(prod,AccountTransType.FEE.getCode());

        final BalancePerson userBalance = balanceService.getAccBalance(userAccount.getAccountNo(),currency);
        log.info("get user balance :{}",userBalance);
        final BalanceOrg finBalance = balanceOrgDao.getBalance(accFin.getAccountNo(),currency);
        log.info("get bonus account balance :{}",finBalance);
        final BalanceOrg feeBalance = balanceOrgDao.getBalance(accFee.getAccountNo(),currency);
        log.info("get fine account balance :{}",feeBalance);

        BigDecimal actralAmount = amount.subtract(bonus).subtract(fine);
        BigDecimal principal = amount.subtract(bonus);
        List<BalanceDetail>  details = new ArrayList<>();
        BalanceDetail userDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),userAccount.getAccountNo(),currency,
                actralAmount,userBalance.getBalance(),"提前赎回");
        BalanceDetail finDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.OUT.getCode(),accFin.getAccountNo(),currency,
                principal,finBalance.getBalance(),"提前赎回");
        BalanceDetail feeDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,Direction.IN.getCode(),accFee.getAccountNo(),currency,
                fine,feeBalance.getBalance(),"提前赎回");
        details.add(finDetail);
        details.add(feeDetail);
        details.add(userDetail);

        final BigDecimal userAmount = userBalance.getBalance().add(actralAmount);
        final BigDecimal finAmount = finBalance.getBalance().subtract(principal);
        final BigDecimal feeAmount = feeBalance.getBalance().add(fine);

        if (finAmount.compareTo(BigDecimal.ZERO)<0){
            log.error("payable account balance is not enough.");
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.insertBatch(details);
                balancePersonDao.updateBalance(userBalance,userAmount);
                balanceOrgDao.updateBalance(finBalance,finAmount);
                balanceOrgDao.updateBalance(feeBalance,feeAmount);
            }
        });

        return true;
    }

    @Override
    public boolean giveUpCompensation(String freezeCode,String tradeNo,String customerNo,String currency){
        AccountPerson accountPerson = accountService.getCustomerAccount(customerNo,null);
        BalancePerson balancePerson = balanceService.getAccBalance(accountPerson.getAccountNo(),currency);

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
                log.error("freeze account balance is not enough.");
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

    @Override
    public boolean systemTransfer(String customerNo, String currency, String direction,
                                  String tradeNo, BigDecimal amount,String accountNo){
        AccountOrg orgAccount = accountService.getOrgAccount(null,accountNo);
        AccountInternal finAccount = accountInternalDao.getAccountByTransType(
                AccountTransType.FINANCING.getCode(),null);

        final BalanceOrg orgBalance = balanceOrgDao.getBalance(orgAccount.getAccountNo(),currency);
        final BalanceInternal finBalance = balanceInternalDao.getBalance(finAccount.getAccountNo(),currency);

        final BigDecimal orgAmount;
        final BigDecimal finAmount;

        //计算余额
        if (Direction.IN.getCode().equals(direction)){
            finAmount = finBalance.getBalance().add(amount);
            orgAmount = orgBalance.getBalance().add(amount);
        }else{
            finAmount = finBalance.getBalance().subtract(amount);
            orgAmount = orgBalance.getBalance().subtract(amount);
        }
        if (finAmount.compareTo(BigDecimal.ZERO)<0){
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }
        if (orgAmount.compareTo(BigDecimal.ZERO)<0){
            throw new BizException(BizExceptionEnum.INTERNAL_BALANCE_NOT_ENOUGH);
        }

        final BalanceDetail finDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,direction,finAccount.getAccountNo(),currency,
                amount,finBalance.getBalance(),"机构资金划转");
        final BalanceDetail orgDetail = AccountConvert.convertToBalanceDetail(
                tradeNo,direction,orgAccount.getAccountNo(),currency,
                amount,orgBalance.getBalance(),"机构资金划转");


        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                balanceDetailDao.saveBalanceDetail(orgDetail);
                balanceDetailDao.saveBalanceDetail(finDetail);
                if (balanceInternalDao.updateBalance(finBalance,finAmount)<=0){
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
                }
                if(balanceOrgDao.updateBalance(orgBalance,orgAmount)<=0){
                    throw new TryAgainException(BizExceptionEnum.UPDATE_BALANCE_FAILED);
                }
            }
        });

        return true;

    }
}
