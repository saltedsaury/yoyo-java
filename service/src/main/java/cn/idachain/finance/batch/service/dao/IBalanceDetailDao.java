package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.BalanceDetail;
import com.baomidou.mybatisplus.service.IService;

import java.util.Date;
import java.util.List;

public interface IBalanceDetailDao extends IService<BalanceDetail> {

    int saveBalanceDetail(BalanceDetail balanceDetail);


    List<BalanceDetail> getDetailsByBonusOrderToReconcile(Long lastId);

    List<BalanceDetail> getDetailsByTransferToReconcile(Long lastId);

    List<BalanceDetail> getDetailsByInvestInfoToReconcile(Long lastId);

    List<BalanceDetail> getDetailsByCompensationToReconcile(Long lastId);

    List<BalanceDetail> getDetailsByRedemptionToReconcile(Long lastId);

    List<BalanceDetail> getDetailsByRevenuePlanToReconcile(Long lastId);

    Long getLastId();
}
