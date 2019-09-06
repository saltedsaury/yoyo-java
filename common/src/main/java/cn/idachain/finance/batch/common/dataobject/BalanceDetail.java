package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class BalanceDetail {

    private String bizNo;

    private String tradeNo;

    private String transType;

    private String accountNo;

    private String currency;

    private BigDecimal amount;

    private BigDecimal beforeBal;

    private String status;

    private String remark;
}
