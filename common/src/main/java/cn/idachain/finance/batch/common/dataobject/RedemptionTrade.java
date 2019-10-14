package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class RedemptionTrade extends BaseDO{

    private String tradeNo;

    private String investNo;

    private BigDecimal amount;

    private BigDecimal fine;

    private BigDecimal bonus;

    private BigDecimal fee;

    private String ccy;

    private String status;

    private String customerNo;

    private String operatorNo;

    private Long paidTime;

    private Boolean reconciled;

}
