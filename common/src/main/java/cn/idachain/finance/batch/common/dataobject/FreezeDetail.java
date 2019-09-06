package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class FreezeDetail {

    private String bizNo;

    private String tradeNo;

    private String freezeCode;

    private String accountNo;

    private String currency;

    private BigDecimal freezeAmt;

    private BigDecimal unfreezeAmt;

    private String freezeType;

    private String status;

    private String remark;
}
