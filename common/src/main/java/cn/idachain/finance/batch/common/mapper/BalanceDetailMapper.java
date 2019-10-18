package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.dataobject.BalanceDetail;
import cn.idachain.finance.batch.common.base.SuperMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface BalanceDetailMapper extends SuperMapper<BalanceDetail> {

    @Select("select * from balance_detail where id <= #{param1} and trade_no in " +
            "(select trade_no from bonus_order where paid_time is not null and reconciled = 0)")
    List<BalanceDetail> getDetailsByBonusOrderToReconciled(Long lastId);

    @Select("select * from balance_detail where id <= #{param1} and trade_no in " +
            "(select order_no from transfer_order where charge_time is not null and reconciled = 0)")
    List<BalanceDetail> getDetailsByTransferOrderToReconcile(Long lastId);

    @Select("select * from balance_detail where id <= #{param1} and trade_no in " +
            "(select trade_no from invest_info where invest_success_time is not null and reconciled = 0)")
    List<BalanceDetail> getDetailsByInvestInfoToReconcile(Long lastId);

    @Select("select * from balance_detail where id <= #{param1} and trade_no in " +
            "(select trade_no from compensate_trade where paid_time is not null and reconciled = 0)")
    List<BalanceDetail> getDetailsByCompensationToReconcile(Long lastId);

    @Select("select * from balance_detail where id <= #{param1} and trade_no in " +
            "(select trade_no from redemption_trade where paid_time is not null and reconciled = 0)")
    List<BalanceDetail> getDetailsByRedemptionToReconcile(Long lastId);

    @Select("select * from balance_detail where id <= #{param1} and trade_no in " +
            "(select plan_no from revenue_plan where paid_time is not null and reconciled = 0)")
    List<BalanceDetail> getDetailsByRevenuePlanToReconcile(Long lastId);

    @Select("select id from balance_detail order by id desc limit 1")
    Long getLastId();

}
