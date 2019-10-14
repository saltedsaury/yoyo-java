package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.CompensateTrade;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.Collection;
import java.util.List;

public interface ICompensateTradeDao {
    void saveInsuranceTrade(CompensateTrade trade);

    void updateCompensateTradeStatusByObj(CompensateTrade trade, String status);

    List<CompensateTrade> selectCompensateTradeByStatus(String status);

    List<CompensateTrade> getListAlreadyCompensate(String insuranceTrade);

    CompensateTrade selectTradeByInsuranceTradeNo(String insuranceTrade);

    CompensateTrade selectTradeByTradeNo(String tradeNo);

    List<CompensateTrade> getCompensateTradeForPage(String userNo, Page page);

    void markReconciled(Collection<String> orderNos);
}
