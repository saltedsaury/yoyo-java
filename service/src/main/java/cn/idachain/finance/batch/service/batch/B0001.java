package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.common.dataobject.BonusOrder;
import cn.idachain.finance.batch.common.enums.BatchCode;
import cn.idachain.finance.batch.common.enums.BonusStatus;
import cn.idachain.finance.batch.service.dao.IBonusOrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 日启 3
 * 到达分红日当日，修改分红单状态，等待日终处理
 */
@Slf4j
@Service
public class B0001 extends BaseBatch{

    @Autowired
    private IBonusOrderDao bonusOrderDao;
    public boolean execute() throws Exception {
        beforeExcute(BatchCode.B0001.getCode());
        if (!checkStatus()){
            return false;
        }
        List<BonusOrder> bonusOrders = bonusOrderDao.selectBonusByStatus(
                new Date(System.currentTimeMillis()), BonusStatus.INIT.getCode());
        log.info("do batch B0001 for bonus order list :{}",bonusOrders);
        try {
            for (BonusOrder order:bonusOrders){
                order.setStatus(BonusStatus.PREPARE.getCode());
            }
            if (bonusOrders !=null && bonusOrders.size()>0) {
                bonusOrderDao.updateBatchById(bonusOrders, bonusOrders.size());
            }
        }catch (Exception e){
            log.error("update bonusOrder batch db error:{}",e);
            throw e;
        }

        afterExecute();
        return true;
    }
}
