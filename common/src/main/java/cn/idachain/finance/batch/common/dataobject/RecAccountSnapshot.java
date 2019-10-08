package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author kun
 * @version 2019/10/8 14:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RecAccountSnapshot extends BaseDO {

    private static final long serialVersionUID = -3538542279472259300L;

    /**
     * accountNo + ":" + ccy
     */
    private String account;

    private String accountType;

    private BigDecimal balance;

    private Long snapshotTime;
}
