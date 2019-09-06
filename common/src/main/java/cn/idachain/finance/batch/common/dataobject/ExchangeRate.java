package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class ExchangeRate extends BaseDO{

    private String bizNo;

    private BigDecimal rate;

    private String transactionPairs;

    private String remark;

    private String productNo;
}
