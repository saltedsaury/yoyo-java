package com.yoyo.base.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class Relationship extends BaseDO {

    private String channelId;

    private String parentId;

    private String mobileNum;

}
