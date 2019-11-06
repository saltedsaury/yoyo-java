package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.InvestInfo;
import cn.idachain.finance.batch.common.mapper.InvestInfoMapper;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.dao.IInvestDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class InvestDao implements IInvestDao {

    @Autowired
    private InvestInfoMapper investInfoMapper;

    /**
     * 申购投资记录入库
     * @param investInfo
     */
    @Override
    public void saveInvestInfo(InvestInfo investInfo){
        investInfoMapper.insert(investInfo);
    }

    /**
     * 根据流水号和状态查询投资记录
     * @param tradeNo
     * @param status
     * @return
     */
    @Override
    public InvestInfo selectInvestInfoByTradeNoAndStatus(String tradeNo, String status,String customerNo){
        InvestInfo infoWrapper = new InvestInfo();
        log.info("select invest info param :{},{},{}",tradeNo,status,customerNo);
        infoWrapper.setTradeNo(tradeNo);
        infoWrapper.setCustomerNo(customerNo);
        if (!BlankUtil.isBlank(status)) {
            infoWrapper.setStatus(status);
        }
        InvestInfo investInfo = investInfoMapper.selectOne(infoWrapper);

        return investInfo;
    }

    @Override
    public List<InvestInfo> selectInvestInfoByBizTypeAndStatus(String bizType, String status){
        EntityWrapper<InvestInfo> wrapper = new EntityWrapper<InvestInfo>();
        wrapper.eq("biz_type",bizType);
        if (!BlankUtil.isBlank(status)) {
            wrapper.eq("status", status);
        }
        List<InvestInfo> investInfos = investInfoMapper.selectList(wrapper);
        return investInfos;
    }

    @Override
    public void updateInvestInfoStatusByObj(InvestInfo investInfo, String status){
        EntityWrapper<InvestInfo> wrapper = new EntityWrapper<InvestInfo>();
        wrapper.eq("trade_no",investInfo.getTradeNo());
        wrapper.eq("status",investInfo.getStatus());

        investInfo.setStatus(status);
        investInfoMapper.update(investInfo,wrapper);
    }

    /**
     * 根据投资类型和状态查询投资记录
     * @param bizType
     * @param status
     * @return
     */
    @Override
    public List<InvestInfo> selectInvestRecordForBatch(String productNo, String bizType, String status){
        EntityWrapper<InvestInfo> infoWrapper = new EntityWrapper<InvestInfo>();
        infoWrapper.eq("product_no",productNo);
        infoWrapper.eq("biz_type",bizType);
        infoWrapper.eq("status",status);
        List<InvestInfo> investInfos = investInfoMapper.selectList(infoWrapper);

        return investInfos;
    }

    @Override
    public InvestInfo selectInvestInfoByBizTypeAndPlanNo(String bizType, String planNo, String status){
        InvestInfo wrapper = new InvestInfo();
        wrapper.setBizType(bizType);
        wrapper.setPlanNo(planNo);
        wrapper.setStatus(status);
        InvestInfo investInfo = investInfoMapper.selectOne(wrapper);
        return investInfo;
    }

    @Override
    public List<InvestInfo> selectInvestHistory(String bizType, String customerNo, Page page,
                                                String status, List<String> statusList){
        EntityWrapper<InvestInfo> wrapper = new EntityWrapper<InvestInfo>();
        wrapper.eq("biz_type",bizType);
        wrapper.eq("customer_no", customerNo);
        if (statusList!=null) {
            wrapper.in("status",statusList);
        }else{
            wrapper.ge("status", status);
        }
        List<InvestInfo> investInfos = investInfoMapper.selectPage(page,wrapper);

        return investInfos;
    }

    @Override
    public BigDecimal sumTotalAmountByStatus(List<String> status, String uid, String ccy){
        return investInfoMapper.sumTotalAmountByStatus(status,uid,ccy);
    }

    @Override
    public void markReconciled(Collection<String> orderNos) {
        if (orderNos.isEmpty()) {
            return;
        }
        investInfoMapper.markReconciled(orderNos);
    }

    @Override
    public BigDecimal getSurplusAmount(String status, String productNo){
        return investInfoMapper.getSurplusAmount(status,productNo);
    }
}
