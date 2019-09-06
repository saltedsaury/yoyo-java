package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.TransferOrder;

import java.util.List;

public interface ITransferOrderDao {
    void saveTransferOrder(TransferOrder transferOrder);

    void updateStatusByObj(TransferOrder transferOrder, String status);

    void updateProcessStatusByObj(TransferOrder transferOrder, String status);

    List<TransferOrder> getTransferOrderByStatus(String status);
}
