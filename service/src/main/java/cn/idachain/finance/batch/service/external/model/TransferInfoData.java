package cn.idachain.finance.batch.service.external.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferInfoData {

    /**
     * 用户号
     */
    private String userNo;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 订单号
     */
    private String outOrderNo;

}
