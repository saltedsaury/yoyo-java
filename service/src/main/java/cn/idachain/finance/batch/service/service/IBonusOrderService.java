package cn.idachain.finance.batch.service.service;

import java.math.BigDecimal;

public interface IBonusOrderService {
    BigDecimal getTotalRevenue(String customerNo, String currency,String status);

}
