package cn.idachain.finance.batch.common.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class FinancingBalance {

    private String ccy;

    private String balance;

    private String price;
}
