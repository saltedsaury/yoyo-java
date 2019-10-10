package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.TransferOrder;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

public interface ITransferOrderDao {
    void saveTransferOrder(TransferOrder transferOrder);

    void updateStatusByObj(TransferOrder transferOrder, String status);

    void updateProcessStatusByObj(TransferOrder transferOrder, String status);

    void updateStatus(TransferOrder order,
                      String status,
                      String processStatus);

    List<TransferOrder> getTransferOrderByStatusBeforeId(String status, List<String> processStatus, Long last);

    List<TransferOrder> getTransferOrderByStatus(String status, List<String> process, Page page);

    List<TransferOrder> getTransferOrderBetween(Long start, Long end);

    Long lastId();
}
