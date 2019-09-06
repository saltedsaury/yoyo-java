package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.FreezeDetail;
import cn.idachain.finance.batch.common.mapper.FreezeDetailMapper;
import cn.idachain.finance.batch.service.dao.IFreezeDetailDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FreezeDetailDao extends ServiceImpl<FreezeDetailMapper,FreezeDetail> implements IFreezeDetailDao {

    @Autowired
    private FreezeDetailMapper freezeDetailMapper;

    @Override
    public int saveFreezeDetail (FreezeDetail freezeDetail){
        return freezeDetailMapper.insert(freezeDetail);
    }

    @Override
    public List<FreezeDetail> getFreezeByCode(String tradeNo) {
        EntityWrapper<FreezeDetail> condition = new EntityWrapper<FreezeDetail>();
        condition.eq("freeze_code",tradeNo);

        return freezeDetailMapper.selectList(condition);
    }

}
