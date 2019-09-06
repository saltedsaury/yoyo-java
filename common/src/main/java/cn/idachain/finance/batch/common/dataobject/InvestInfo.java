package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class InvestInfo extends BaseDO{

    private static final long serialVersionUID = 4173610669071212544L;
    private String tradeNo;

    private String bizType;

    private String customerNo;

    private String productNo;

    private String planNo;

    private String ccy;

    private BigDecimal amount;

    private BigDecimal fee;

    private String status;

    private String operatorNo;


}