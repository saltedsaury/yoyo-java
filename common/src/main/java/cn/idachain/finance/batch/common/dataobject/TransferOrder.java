package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class TransferOrder extends BaseDO {

    private String orderNo;

    private String ccy;

    private BigDecimal amount;

    private String customerNo;

    private String deriction;

    private BigDecimal fee;

    private int channel;

    private String status;

    private String processStatus;

    private String transferType;

    private String accountNo;

    private Long transferTime;

    private Long chargeTime;

    private Boolean reconciled;

}
