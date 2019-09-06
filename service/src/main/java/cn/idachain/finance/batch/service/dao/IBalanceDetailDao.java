package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.BalanceDetail;
import com.baomidou.mybatisplus.service.IService;

public interface IBalanceDetailDao extends IService<BalanceDetail> {
    int saveBalanceDetail(BalanceDetail balanceDetail);
}
