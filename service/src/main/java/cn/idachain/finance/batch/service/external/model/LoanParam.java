package cn.idachain.finance.batch.service.external.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by liuhailin on 2019/2/12.
 */
@Data
@Builder
public class LoanParam {
    private String outOrderNo;
    private String currency;
    private BigDecimal amount;
}
