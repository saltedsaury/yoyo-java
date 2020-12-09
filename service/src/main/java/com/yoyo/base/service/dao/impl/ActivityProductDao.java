package com.yoyo.base.service.dao.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yoyo.base.common.dataobject.DailyProfit;
import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.mapper.DailyProfitMapper;
import com.yoyo.base.common.mapper.ProfitDetailMapper;
import com.yoyo.base.service.dao.IDailyProfitDao;
import com.yoyo.base.service.dao.IProfitDetailDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class DailyProfitDao implements IDailyProfitDao {

    @Autowired
    private DailyProfitMapper dailyProfitMapper;

    @Override
    public List<DailyProfit> getDailyProfitList(String channelId,Date start, Date end) {
        EntityWrapper<DailyProfit> wrapper = new EntityWrapper<DailyProfit>();
        wrapper.eq("channel_id",channelId);
        wrapper.gt("create_time",start);
        wrapper.le("create_time",end);
        return dailyProfitMapper.selectList(wrapper);
    }


    @Override
    public Integer setDailyProfit(DailyProfit dailyProfit) {

        return dailyProfitMapper.insert(dailyProfit);
    }
}
