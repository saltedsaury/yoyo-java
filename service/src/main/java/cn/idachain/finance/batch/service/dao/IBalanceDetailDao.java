package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.BalanceDetail;
import com.baomidou.mybatisplus.service.IService;

import java.util.Date;
import java.util.List;

public interface IBalanceDetailDao extends IService<BalanceDetail> {

    int saveBalanceDetail(BalanceDetail balanceDetail);

    List<BalanceDetail> getDetailsByBonusOrderBetweenTime(Long start, Long end);

    List<BalanceDetail> getDetailsByTransferOrderBetweenTime(Long start, Long end);

    List<BalanceDetail> getDetailsByInvestInfoBetweenTime(Long start, Long end);

    List<BalanceDetail> getDetailsByCompensationBetweenTime(Long start, Long end);

    List<BalanceDetail> getDetailsByRedemptionBetweenTime(Long start, Long end);

    List<BalanceDetail> getDetailsByRevenuePlanBetweenTime(Long start, Long end);
}
