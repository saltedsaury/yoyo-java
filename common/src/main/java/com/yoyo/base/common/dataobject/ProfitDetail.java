package com.yoyo.base.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class ProfitDetail extends BaseDO {

    private String tid;

    private String channelId;

    private BigDecimal profit;

    private Date orderCreated;

    private BigDecimal total;

}
