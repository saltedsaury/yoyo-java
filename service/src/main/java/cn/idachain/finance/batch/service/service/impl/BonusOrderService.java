package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.BonusOrder;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.dao.IBonusOrderDao;
import cn.idachain.finance.batch.service.service.IBonusOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class BonusOrderService implements IBonusOrderService {

    @Autowired
    private IBonusOrderDao bonusOrderDao;

    @Override
    public BigDecimal getTotalRevenue(String customerNo, String currency,String status){
        List<BonusOrder> list = bonusOrderDao.sumTotalRevenue(customerNo,currency,status);
        if (BlankUtil.isBlank(list.get(0))){
            return new BigDecimal(0);
        }
        BigDecimal total = list.get(0).getAmount();
        return total;
    }

}
