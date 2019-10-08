package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.BonusOrder;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.Date;
import java.util.List;

public interface IBonusOrderDao extends IService<BonusOrder> {
    int countBonusByPlanAndStatus(String planNo, String status);

    void saveBonusOrder(BonusOrder bonusOrder);

    List<BonusOrder> selectBonusByStatus(Date currentDate, String status);

    void updateBonusByStatus(BonusOrder bonusOrder, String status);

    List<BonusOrder> sumTotalRevenue(String customerNo,String currency,String status);

    BonusOrder getBonusOrderByPeriods(String planNo, Long periods);

    List<BonusOrder> getBonusOrderByCustomer(String customerNo, String status, Page page);

    List<BonusOrder> getBonusOrderByPlanNo(String planNo, Page page);

}
