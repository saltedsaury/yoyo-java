package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.SystemBatch;

public interface ISystemBatchDao {
    SystemBatch selectSystemBatchByCode(String batchCode);

    SystemBatch selectSystemBatchByPreCode(String preBatchCode,String batchType);

    void updateSystemBatchStatus(SystemBatch systemBatch, String status);
}
