package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.FreezeDetail;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

public interface IFreezeDetailDao extends IService<FreezeDetail> {
    int saveFreezeDetail (FreezeDetail freezeDetail);

    List<FreezeDetail> getFreezeByCode(String tradeNo);
}
