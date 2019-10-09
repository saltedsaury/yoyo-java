package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.dataobject.BalanceDetail;
import cn.idachain.finance.batch.common.base.SuperMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface BalanceDetailMapper extends SuperMapper<BalanceDetail> {

    @Select("select * from balance_detail where trade_no in " +
            "(select trade_no from bonus_order where paid_time BETWEEN #{param1} and #{param2})")
    List<BalanceDetail> getDetailsByBonusOrderBetweenTime(Long start, Long end);

    @Select("select * from balance_detail where trade_no in " +
            "(select order_no from transfer_order where charge_time between #{param1} and #{param2})")
    List<BalanceDetail> getDetailsByTransferOrderBetweenTime(Long start, Long end);

    @Select("select * from balance_detail where trade_no in " +
            "(select trade_no from invest_info where invest_success_time between #{param1} and #{param2})")
    List<BalanceDetail> getDetailsByInvestInfoBetweenTime(Long start, Long end);

    @Select("select * from balance_detail where trade_no in " +
            "(select trade_no from compensate_trade where paid_time between #{param1} and #{param2})")
    List<BalanceDetail> getDetailsByCompensationBetweenTime(Long start, Long end);

    @Select("select * from balance_detail where trade_no in " +
            "(select trade_no from redemption_trade where paid_time between #{param1} and #{param2})")
    List<BalanceDetail> getDetailsByRedemptionBetweenTime(Long start, Long end);

    @Select("select * from balance_detail where trade_no in " +
            "(select plan_no from revenue_plan where paid_time between #{param1} and #{param2})")
    List<BalanceDetail> getDetailsByRevenuePlanBetweenTime(Long start, Long end);
}
