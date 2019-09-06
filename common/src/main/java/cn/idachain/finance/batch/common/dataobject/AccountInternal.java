package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class AccountInternal {

    //具体业务
    private String transType;

    private String accountNo;

    private String currency;

    //资产负债费用损益
    private String accountType;

    private String remark;
}
