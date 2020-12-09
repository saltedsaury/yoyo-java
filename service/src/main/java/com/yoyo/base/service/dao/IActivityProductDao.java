package com.yoyo.base.service.dao;

import com.yoyo.base.common.dataobject.DailyProfit;
import com.yoyo.base.common.dataobject.ProfitDetail;

import java.util.Date;
import java.util.List;

public interface IDailyProfitDao {

    List<DailyProfit> getDailyProfitList(String channelId, Date start, Date end);

    Integer setDailyProfit(DailyProfit dailyProfit);
}
