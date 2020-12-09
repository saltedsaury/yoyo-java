package com.yoyo.base.common.model.VO;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ProductDetail {

    private String productId;

    private String productName;

    private String price;

    private String picUrl;

    private String shareUrl;

    private String voucherUrl;

}
