package com.yoyo.base.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class DailyProfit extends BaseDO {

    private String channelId;

    private BigDecimal profit;

    private BigDecimal dividend;

}
