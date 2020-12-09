package com.yoyo.base.service.dao;

import com.yoyo.base.common.dataobject.ProfitDetail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IProfitDetailDao {

    Integer setProfitDetail(ProfitDetail profitDetail);

    BigDecimal sumProfit(String channelId, Date start, Date end);
}
