package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.RedemptionTrade;
import cn.idachain.finance.batch.common.mapper.RedemptionTradeMapper;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.dao.IRedemptionTradeDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class RedemptionTradeDao implements IRedemptionTradeDao {
    @Autowired
    private RedemptionTradeMapper redemptionTradeMapper;

    @Override
    public List<RedemptionTrade> selectRedemptionByStatus(String investNo,String status){
        EntityWrapper<RedemptionTrade> wrapper = new EntityWrapper<RedemptionTrade>();
        if (!BlankUtil.isBlank(investNo)) {
            wrapper.eq("invest_no", investNo);
        }
        if (!BlankUtil.isBlank(status)){
            wrapper.eq("status",status);
        }

        List<RedemptionTrade> plans = redemptionTradeMapper.selectList(wrapper);
        return plans;
    }

    @Override
    public void updateTradeStatusByObj(RedemptionTrade trade, String status){

        EntityWrapper<RedemptionTrade> infoWrapper = new EntityWrapper<RedemptionTrade>();
        infoWrapper.eq("trade_no",trade.getTradeNo());
        infoWrapper.eq("status",trade.getStatus());

        trade.setStatus(status);
        trade.setModifiedTime(new Date(System.currentTimeMillis()));
        redemptionTradeMapper.update(trade,infoWrapper);
    }

    @Override
    public void saveRedemptionTrade(RedemptionTrade trade){
        redemptionTradeMapper.insert(trade);
    }

    @Override
    public void markReconciled(Collection<String> orderNos) {
        if (orderNos.isEmpty()) {
            return;
        }
        redemptionTradeMapper.markReconciled(orderNos);
    }
}
