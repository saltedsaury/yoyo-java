package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class RevenuePlan extends BaseDO{
    private static final long serialVersionUID = 7855408673792571550L;

    private String planNo;

    private String investNo;

    private String productNo;

    private String customerNo;

    private BigDecimal principal;

    private BigDecimal actualPrincipal;

    private BigDecimal interest;

    private BigDecimal actualInterest;

    private String interestType;

    private String status;

    private BigDecimal payingInterest;

    private Date lastBonusDate;

    private Date effectiveDate;

    private String extension;

    private String remark;

}