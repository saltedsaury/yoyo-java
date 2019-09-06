package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class AccountPerson {

    private String customerNo;

    private String accountNo;

    private String currency;

    private String accountType;

    private String remark;
}
