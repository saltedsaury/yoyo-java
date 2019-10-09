package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
public class ProductAgreement extends BaseDO{
    private static final long serialVersionUID = 2465295378929034251L;
    private String productNo;

    private Long interestCycle;

    private String cycleType;

    private BigDecimal profitScale;

    private BigDecimal profitPerCycle;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal grad;

    private String interestMode;

    private String preRedeemFlag;

    private String fineType;

    private BigDecimal fine;

    private BigDecimal surplusAmount;

    private BigDecimal raisedAmount;

    private Date valueDate;

    private Date dueDate;

    private String rules;

    private String introduction;

    private String insuranceFlag;

    private String operator;

    private String valueMode;

    private String subscribedCcy;

    private BigDecimal subscribedAmount;

    private Integer subscribedTimes;

    private BigDecimal primaryRate;

    private Date primaryDate;

    private Integer lastInterest;

    private Integer quota;

}