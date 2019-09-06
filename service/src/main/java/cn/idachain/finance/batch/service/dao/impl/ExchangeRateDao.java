package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.ExchangeRate;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.common.mapper.ExchangeRateMapper;
import cn.idachain.finance.batch.service.dao.IExchangeRateDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ExchangeRateDao implements IExchangeRateDao {

    @Autowired
    private ExchangeRateMapper exchangeRateMapper;

    @Override
    public ExchangeRate getCurrentRateByPairs(String transPairs){
        EntityWrapper<ExchangeRate> wrapper = new EntityWrapper<>();
        wrapper.eq("transaction_pairs",transPairs);
        wrapper.orderBy("create_time",false);
        wrapper.last("limit 1");
        List<ExchangeRate> list = exchangeRateMapper.selectList(wrapper);

        if (list.size()!= 1){
            log.error("transaction pairs exchange rate haven't set");
            throw new BizException(BizExceptionEnum.EXCHANGE_RATE_NOT_EXIST);
        }
        return list.get(0);
    }
}
