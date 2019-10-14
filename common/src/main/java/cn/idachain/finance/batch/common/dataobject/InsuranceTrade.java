package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class InsuranceTrade extends BaseDO{

    private String tradeNo;

    private String insuranceNo;

    private String investNo;

    private String customerNo;

    private BigDecimal amount;

    private BigDecimal fee;

    private String status;

    private String subStatus;

    private Date compensateEnd;

}
