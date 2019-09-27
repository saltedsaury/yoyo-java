package cn.idachain.finance.batch.service.service;

import java.math.BigDecimal;

public interface IBalanceDetialService {
    boolean transfer(String customerNo, String currency, String direction, String tradeNo, BigDecimal amount);

    //boolean investFreeze(String customerNo, String currency, String tradeNo, BigDecimal principal, BigDecimal insuranceFee);

    //boolean loan(String currency, BigDecimal amount, String tradeNo);

    boolean payBonus(String customerNo, String currency, BigDecimal amount, String tradeNo,String prod);

    boolean payPrincipal(String customerNo, String currency, BigDecimal amount, String tradeNo,String freezeCode,boolean freeze, String prod);

    boolean compensate(String customerNo, String currency, String compensateCcy,
                       BigDecimal amount, BigDecimal compensateAmt, String tradeNo,
                       String freezeCode, String insuranceNo);

    boolean invest(String tradeNo,String prod);

    boolean redemption(String customerNo, String currency, String tradeNo, BigDecimal amount,
                       BigDecimal fine, BigDecimal bonus, String prod);

    boolean giveUpCompensation(String freezeCode, String tradeNo, String customerNo, String currency);

    boolean systemTransfer(String customerNo, String currency, String direction,
                           String tradeNo, BigDecimal amount, String accountType);
}
