package cn.idachain.finance.batch.service.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kun
 * @version 2019/10/9 17:49
 */
public interface IReconciliationService {

    void buildBalanceSnapshot();

    void checkOrderDetail();

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    void checkTotalBalance();
}
