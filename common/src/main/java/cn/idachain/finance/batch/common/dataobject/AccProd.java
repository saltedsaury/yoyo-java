package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccProd {

    private String accountNo;

    private String productNo;

    private String accountType;

    private String remark;

}
