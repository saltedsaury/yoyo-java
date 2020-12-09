package com.yoyo.base.common.dataobject;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Product extends BaseDO {

    private String itemName;

    private String itemId;

    private String picUrl;

    private String price;

    private String alias;

}
