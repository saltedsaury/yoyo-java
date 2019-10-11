package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author kun
 * @version 2019/9/29 17:22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RecBalanceSnapshot extends BaseDO {

    private static final long serialVersionUID = 4648252383277732213L;

    private String currency;

    private BigDecimal inAmount;

    private BigDecimal outAmount;

    private Long snapshotTime;

}
