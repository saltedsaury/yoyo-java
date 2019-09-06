package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.CompensateTrade;
import cn.idachain.finance.batch.common.mapper.CompensateTradeMapper;
import cn.idachain.finance.batch.service.dao.ICompensateTradeDao;
import cn.idachain.finance.batch.common.enums.InsuranceTradeStatus;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class CompensateTradeDao implements ICompensateTradeDao {
    @Autowired
    private CompensateTradeMapper compensateTradeMapper;

    /**
     * 记录入库
     * @param trade
     */
    @Override
    public void saveInsuranceTrade(CompensateTrade trade){
        compensateTradeMapper.insert(trade);
    }

    @Override
    public void updateCompensateTradeStatusByObj(CompensateTrade trade, String status){

        final EntityWrapper<CompensateTrade> wrapper = new EntityWrapper<CompensateTrade>();
        wrapper.eq("trade_no",trade.getTradeNo());
        wrapper.eq("status",trade.getStatus());

        trade.setStatus(status);
        trade.setModifiedTime(new Date(System.currentTimeMillis()));
        compensateTradeMapper.update(trade,wrapper);
    }

    @Override
    public List<CompensateTrade> selectCompensateTradeByStatus(String status){
        EntityWrapper<CompensateTrade> wrapper = new EntityWrapper<CompensateTrade>();
        wrapper.eq("status", status);
        List<CompensateTrade> trades = compensateTradeMapper.selectList(wrapper);
        return trades;
    }

    @Override
    public List<CompensateTrade> getListAlreadyCompensate(String insuranceTrade){
        EntityWrapper<CompensateTrade> wrapper = new EntityWrapper<CompensateTrade>();
        wrapper.eq("insurance_trade",insuranceTrade);
        wrapper.ne("status", InsuranceTradeStatus.FINISH.getCode());
        List<CompensateTrade> trades = compensateTradeMapper.selectList(wrapper);
        return trades;
    }

    @Override
    public CompensateTrade selectTradeByInsuranceTradeNo(String insuranceTrade){
        CompensateTrade wrapper = new CompensateTrade();
        wrapper.setInsuranceTrade(insuranceTrade);
        CompensateTrade trade = compensateTradeMapper.selectOne(wrapper);
        return trade;
    }

    @Override
    public CompensateTrade selectTradeByTradeNo(String tradeNo) {
        CompensateTrade wrapper = new CompensateTrade();
        wrapper.setTradeNo(tradeNo);
        CompensateTrade trade = compensateTradeMapper.selectOne(wrapper);
        return trade;
    }

    @Override
    public List<CompensateTrade> getCompensateTradeForPage(String userNo, Page page) {
        EntityWrapper<CompensateTrade> wrapper = new EntityWrapper<CompensateTrade>();
        wrapper.eq("customer_no", userNo);
        List<CompensateTrade> compensateTrades = compensateTradeMapper.selectPage(page,wrapper);

        return compensateTrades;
    }
}
