package cn.idachain.finance.batch.task.task;

import cn.idachain.finance.batch.service.service.ITransferOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferConfirm {

    @Autowired
    private ITransferOrderService transferOrderService;

    @Scheduled(fixedRateString = "${task.financing.transfer-confirm}")
    public void execute() {
        transferOrderService.transferConfirm();
    }
}
