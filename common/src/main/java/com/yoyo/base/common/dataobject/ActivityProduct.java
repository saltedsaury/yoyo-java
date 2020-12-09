package com.yoyo.base.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class ActivityProdcut extends BaseDO {

    private String activityId;

    private String itemId;

}
