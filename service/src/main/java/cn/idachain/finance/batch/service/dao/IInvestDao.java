package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.InvestInfo;
import com.baomidou.mybatisplus.plugins.Page;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface IInvestDao {

    void saveInvestInfo(InvestInfo investInfo);

    InvestInfo selectInvestInfoByTradeNoAndStatus(String tradeNo, String status, String customerNo);

    List<InvestInfo> selectInvestInfoByBizTypeAndStatus(String bizType, String status);

    void updateInvestInfoStatusByObj(InvestInfo investInfo, String status);

    List<InvestInfo> selectInvestRecordForBatch(String productNo, String bizType, String status);

    InvestInfo selectInvestInfoByBizTypeAndPlanNo(String bizType, String planNo, String status);

    List<InvestInfo> selectInvestHistory(String bizType, String customerNo, Page page,
                                         String status, List<String> statusList);

    BigDecimal sumTotalAmountByStatus(List<String> status, String uid,String ccy);

    void markReconciled(Collection<String> orderNos);

    BigDecimal getSurplusAmount(String status, String productNo);
}
