package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.InsuranceTrade;
import cn.idachain.finance.batch.common.mapper.InsuranceTradeMapper;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.dao.IInsuranceTradeDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class InsuranceTradeDao implements IInsuranceTradeDao {

    @Autowired
    private InsuranceTradeMapper insuranceTradeMapper;


    @Override
    public void updateInsuranceTradeStatusByObj(InsuranceTrade trade, String status){

        final EntityWrapper<InsuranceTrade> infoWrapper = new EntityWrapper<InsuranceTrade>();
        infoWrapper.eq("trade_no",trade.getTradeNo());
        infoWrapper.eq("status",trade.getStatus());

        trade.setStatus(status);
        trade.setModifiedTime(new Date(System.currentTimeMillis()));
        insuranceTradeMapper.update(trade,infoWrapper);
    }

    /**
     * 根据流水号和状态查询投资记录
     * @param tradeNo
     * @param status
     * @return
     */
    @Override
    public InsuranceTrade selectInsuranceTradeByTradeNoAndStatus(String tradeNo, String status,String curtomerNo){
        InsuranceTrade wrapper = new InsuranceTrade();
        wrapper.setTradeNo(tradeNo);
        if (!BlankUtil.isBlank(status)){
            wrapper.setStatus(status);
        }
        wrapper.setCustomerNo(curtomerNo);
        InsuranceTrade trade = insuranceTradeMapper.selectOne(wrapper);

        return trade;
    }

    @Override
    public InsuranceTrade getTradeByInvestNo(String investNo, String status){
        InsuranceTrade wrapper = new InsuranceTrade();
        wrapper.setInvestNo(investNo);
        wrapper.setStatus(status);
        InsuranceTrade trade = insuranceTradeMapper.selectOne(wrapper);

        return trade;
    }


    @Override
    public void updateInsuranceSubStatusByObj(InsuranceTrade trade, String status){

        final EntityWrapper<InsuranceTrade> infoWrapper = new EntityWrapper<InsuranceTrade>();
        infoWrapper.eq("trade_no",trade.getTradeNo());
        infoWrapper.eq("sub_status",trade.getSubStatus());

        trade.setSubStatus(status);
        trade.setModifiedTime(new Date(System.currentTimeMillis()));
        insuranceTradeMapper.update(trade,infoWrapper);
    }

    @Override
    public List<InsuranceTrade> getInsuranceTradeOverDue(String insuranceNo, String status, String subStatus, Date currentDate) {
        EntityWrapper<InsuranceTrade> wrapper = new EntityWrapper<InsuranceTrade>();
        wrapper.eq("insurance_no",insuranceNo);
        wrapper.eq("status",status);
        wrapper.eq("sub_status",subStatus);
        wrapper.le("compensate_end",currentDate);
        return insuranceTradeMapper.selectList(wrapper);
    }
}
