package com.yoyo.base.service.dao.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.mapper.ProfitDetailMapper;
import com.yoyo.base.service.dao.IProfitDetailDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ProfitDetailDao implements IProfitDetailDao {

    @Autowired
    private ProfitDetailMapper profitDetailMapper;

   /* public List<com.yoyo.base.common.dataobject.ProfitDetail> getProductsByActivityId(String activityId) {
        EntityWrapper<com.yoyo.base.common.dataobject.ProfitDetail> wrapper = new EntityWrapper<com.yoyo.base.common.dataobject.ProfitDetail>();
        wrapper.eq("activity_id",activityId);
        activityMappingMapper.selectList(wrapper);
        return activityMappingMapper.selectList(wrapper);
    }*/


    @Override
    public Integer setProfitDetail(ProfitDetail profitDetail) {

        return profitDetailMapper.insert(profitDetail);
    }

    @Override
    public BigDecimal sumProfit(String channelId, Date start, Date end){
        EntityWrapper<ProfitDetail> wrapper = new EntityWrapper<ProfitDetail>();
        wrapper.eq("channel_id",channelId)
                .gt("order_created",start)
                .le("order_created",end)
                .setSqlSelect("COALESCE(sum(profit),0.00) as total");
        List<ProfitDetail> profitDetails = profitDetailMapper.selectList(wrapper);
        if (null != profitDetails && profitDetails.size()>0){
            return profitDetails.get(0).getTotal();
        }
        return BigDecimal.ZERO;

    }
}
