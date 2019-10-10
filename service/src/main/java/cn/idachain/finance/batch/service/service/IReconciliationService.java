package cn.idachain.finance.batch.service.service;

/**
 * @author kun
 * @version 2019/10/9 17:49
 */
public interface IReconciliationService {

    long buildBalanceSnapshot();

    void checkBalanceInternal();

    void checkBalanceSnapshot();

    void checkOrderDetail();
}
