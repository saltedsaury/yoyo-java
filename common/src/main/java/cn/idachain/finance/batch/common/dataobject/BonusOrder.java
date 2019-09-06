package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class BonusOrder extends BaseDO{

    private static final long serialVersionUID = 8639669841196041744L;
    private String tradeNo;

    private String customerNo;

    private String investNo;

    private String planNo;

    private Long periods;

    private String ccy;

    private BigDecimal amount;

    private String status;

    private String operatorNo;

    private String remark;

    private Date bonusDate;

    private String productNo;
}