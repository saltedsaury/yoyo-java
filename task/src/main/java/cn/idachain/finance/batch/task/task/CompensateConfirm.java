package cn.idachain.finance.batch.task.task;

import cn.idachain.finance.batch.common.dataobject.CompensateTrade;
import cn.idachain.finance.batch.service.service.ICompensateTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CompensateConfirm {

    @Autowired
    private ICompensateTradeService compensateTradeService;

    @Scheduled(fixedRateString = "${task.financing.compensate-confirm}")
    public void execute() {
        List<CompensateTrade> list = compensateTradeService.getTradesWaitingConfirm();
        for (CompensateTrade trade :list){
            try {
                compensateTradeService.compensateConfirm(trade);
            }catch (Exception e){
                log.error("compensation {} confirm failed with error:{}",trade.getTradeNo(),e);
            }
        }
    }
}
