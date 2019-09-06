package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class CompensateTrade extends BaseDO{

    private String tradeNo;

    private String insuranceTrade;

    private BigDecimal effectiveAmount;

    private String ccy;

    private BigDecimal compensateAmount;

    private String compensateCcy;

    private String status;

    private String operatorNo;

    private String customerNo;

}
