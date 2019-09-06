package cn.idachain.finance.batch.service.service;

import cn.idachain.finance.batch.common.dataobject.CompensateTrade;

import java.util.List;

public interface ICompensateTradeService {
    CompensateTrade compensateConfirm(CompensateTrade trade) throws Exception;

    List<CompensateTrade> getTradesWaitingConfirm();

    CompensateTrade getCompensateTradeByNo(String tradeNo);

}
