package com.yoyo.base.service.service;

import com.yoyo.base.common.dataobject.ProfitDetail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IProfitDetailService {

    boolean setProfitDetail(ProfitDetail profitDetail);

    BigDecimal sumProfit(String channelId, Date start, Date end);
}
