package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kun
 * @version 2019/10/9 15:17
 */
@Data
public class BaseBalance {

    private String accountNo;

    private String currency;

    private BigDecimal balance;

    private BigDecimal freeze;

    private String status;

    private String remark;

}
