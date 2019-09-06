package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class BalancePerson {

    private String accountNo;

    private String currency;

    private BigDecimal balance;

    private BigDecimal freeze;

    private String status;

    private String remark;

}
