package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.ExchangeRate;

public interface IExchangeRateDao {
    ExchangeRate getCurrentRateByPairs(String transPairs);
}
