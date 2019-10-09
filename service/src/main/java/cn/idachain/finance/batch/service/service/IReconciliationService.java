package cn.idachain.finance.batch.service.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author kun
 * @version 2019/10/9 17:49
 */
public interface IReconciliationService {

    void checkBalanceInternal();

    @Transactional(readOnly = true)
    void checkBalanceSnapshot();

    @Transactional(readOnly = false)
    void checkOrderDetail();
}
