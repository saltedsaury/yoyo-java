package com.yoyo.base.service.service.impl;

import com.yoyo.base.common.dataobject.DailyProfit;
import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.service.dao.IDailyProfitDao;
import com.yoyo.base.service.dao.IProfitDetailDao;
import com.yoyo.base.service.service.IDailyProfitService;
import com.yoyo.base.service.service.IProfitDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DailyProfitService implements IDailyProfitService {

    @Autowired
    private IDailyProfitDao dailyProfitDao;

    @Override
    public boolean setDailyProfit(DailyProfit dailyProfit) {
        if (dailyProfitDao.setDailyProfit(dailyProfit)>0){
            return true;
        }
        return false;
    }

    @Override
    public List<DailyProfit> getDailyProfit(String channelId, Date start, Date end){
        return dailyProfitDao.getDailyProfitList(channelId,start,end);
    }

}