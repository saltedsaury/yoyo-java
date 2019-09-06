package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Data
public class ProductInfo extends BaseDO{
    private static final long serialVersionUID = 5027243421631128325L;
    private String productNo;

    private String productName;

    private String productLogo;

    private Long sort;

    private String productLabel;

    private String version;

    private String productType;

    private String channel;

    private String status;

    private String ccy;

    private Date effectiveDate;

    private Date expiryDate;

}