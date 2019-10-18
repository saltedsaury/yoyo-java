package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.BonusOrder;
import cn.idachain.finance.batch.common.mapper.BonusOrderMapper;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.dao.IBonusOrderDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class BonusOrderDao extends ServiceImpl<BonusOrderMapper,BonusOrder> implements IBonusOrderDao {
    @Autowired
    private BonusOrderMapper bonusOrderMapper;

    @Override
    public int countBonusByPlanAndStatus(String planNo, String status){
        EntityWrapper<BonusOrder> wrapper = new EntityWrapper<BonusOrder>();
        wrapper.eq("plan_no",planNo);
        wrapper.eq("status",status);
        //List<BonusOrder> orders = bonusOrderMapper.selectList(wrapper);
        int count = bonusOrderMapper.selectCount(wrapper);
        return count;
    }

    @Override
    public void saveBonusOrder(BonusOrder bonusOrder){
        bonusOrderMapper.insert(bonusOrder);
    }

    @Override
    public List<BonusOrder> selectBonusByStatus(Date currentDate,String status){
        EntityWrapper<BonusOrder> wrapper = new EntityWrapper<BonusOrder>();
        if (!BlankUtil.isBlank(currentDate)){
            wrapper.le("bonus_date",currentDate);
        }
        wrapper.eq("status",status);
        List<BonusOrder> orders = bonusOrderMapper.selectList(wrapper);
        return orders;
    }

    @Override
    public void updateBonusByStatus(BonusOrder bonusOrder, String status){
        EntityWrapper<BonusOrder> wrapper = new EntityWrapper<BonusOrder>();
        if (!BlankUtil.isBlank(bonusOrder.getTradeNo())){
            wrapper.eq("trade_no",bonusOrder.getTradeNo());
        }
        if (!BlankUtil.isBlank(bonusOrder.getInvestNo())){
            wrapper.eq("invest_no",bonusOrder.getInvestNo());
        }
        wrapper.eq("status",bonusOrder.getStatus());

        bonusOrder.setStatus(status);
        bonusOrderMapper.update(bonusOrder,wrapper);
    }

    @Override
    public List<BonusOrder> sumTotalRevenue(String customerNo,String currency,String status){
        EntityWrapper<BonusOrder> wrapper = new EntityWrapper<BonusOrder>();
        wrapper.setSqlSelect("sum(amount) as amount");
        wrapper.eq("customer_no",customerNo);
        wrapper.eq("ccy",currency);
        wrapper.eq("status", status);
        List<BonusOrder> plans = bonusOrderMapper.selectList(wrapper);
        return plans;
    }

    @Override
    public BonusOrder getBonusOrderByPeriods(String planNo,Long periods){
        BonusOrder wrapper = new BonusOrder();
        wrapper.setPlanNo(planNo);
        wrapper.setPeriods(periods);
        BonusOrder bonusOrder = bonusOrderMapper.selectOne(wrapper);
        return bonusOrder;
    }

    @Override
    public List<BonusOrder> getBonusOrderByCustomer(String customerNo, String status, Page page){
        EntityWrapper<BonusOrder> wrapper = new EntityWrapper<BonusOrder>();
        wrapper.eq("customer_no",customerNo);
        wrapper.eq("status",status);
        wrapper.orderBy("plan_no",false);
        wrapper.orderBy("modified_time",false);
        List<BonusOrder> bonusOrders = bonusOrderMapper.selectPage(page,wrapper);
        return bonusOrders;
    }

    @Override
    public List<BonusOrder> getBonusOrderByPlanNo(String planNo, Page page){
        EntityWrapper<BonusOrder> wrapper = new EntityWrapper<BonusOrder>();
        wrapper.eq("plan_no",planNo);
        wrapper.orderBy("periods",true);
        List<BonusOrder> bonusOrders = bonusOrderMapper.selectPage(page,wrapper);
        return bonusOrders;
    }

    @Override
    public void markReconciled(Collection<String> orderNos) {
        if (orderNos.isEmpty()) {
            return;
        }
        bonusOrderMapper.markReconciled(orderNos);
    }

}
