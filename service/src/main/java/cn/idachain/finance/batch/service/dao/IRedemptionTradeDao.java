package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.RedemptionTrade;

import java.util.List;

public interface IRedemptionTradeDao {
    List<RedemptionTrade> selectRedemptionByStatus(String investNo, String status);

    void updateTradeStatusByObj(RedemptionTrade trade, String status);

    void saveRedemptionTrade(RedemptionTrade trade);
}
