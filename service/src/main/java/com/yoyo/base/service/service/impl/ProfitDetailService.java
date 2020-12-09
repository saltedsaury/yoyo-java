package com.yoyo.base.service.service.impl;

import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.service.dao.IProfitDetailDao;
import com.yoyo.base.service.service.IProfitDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProfitDetailService implements IProfitDetailService {

    @Autowired
    private IProfitDetailDao profitDetailDao;

    @Override
    public boolean setProfitDetail(ProfitDetail profitDetail) {
        if (profitDetailDao.setProfitDetail(profitDetail)>0){
            return true;
        }
        return false;
    }

    @Override
    public BigDecimal sumProfit(String channelId, Date start, Date end){
        return profitDetailDao.sumProfit(channelId,start,end);
    }
}