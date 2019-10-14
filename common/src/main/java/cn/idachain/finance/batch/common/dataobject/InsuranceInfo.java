package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class InsuranceInfo extends BaseDO{

    private String insuranceNo;

    private String insuranceName;

    private String compensationType;

    private BigDecimal compensation;

    private String transactionPairs;

    private String insuranceType;

    private BigDecimal insuranceFee;

    private String channel;

    private String status;

    private Date effectiveDate;

    private Date expiredDate;

    private Integer timeLimit;

    private String limitUnit;

    private String feeType;

    private BigDecimal fee;

    private String operator;


}
