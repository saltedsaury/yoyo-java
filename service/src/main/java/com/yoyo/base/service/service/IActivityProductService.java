package com.yoyo.base.service.service;

import com.yoyo.base.common.dataobject.DailyProfit;
import com.yoyo.base.common.dataobject.ProfitDetail;

import java.util.Date;
import java.util.List;

public interface IDailyProfitService {

    boolean setDailyProfit(DailyProfit dailyProfit);

    List<DailyProfit> getDailyProfit(String channelId, Date start, Date end);
}
