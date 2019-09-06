package cn.idachain.finance.batch.common.model;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class Product {
    private String productNo;

    private String productName;

    private String productLogo;

    private Long sort;

    private String productLabel;

    private String version;

    private String productType;

    private String channel;

    private String status;

    private String ccy;

    private Date effectiveDate;

    private Date expiryDate;

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
}
