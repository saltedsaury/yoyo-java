package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccInsurance {

    private String accountNo;

    private String insuranceNo;

    private String accountType;

    private String remark;
    
}
