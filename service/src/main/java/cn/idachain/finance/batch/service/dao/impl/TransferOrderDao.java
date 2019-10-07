package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.TransferOrder;
import cn.idachain.finance.batch.common.mapper.TransferOrderMapper;
import cn.idachain.finance.batch.service.dao.ITransferOrderDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TransferOrderDao implements ITransferOrderDao {

    @Autowired
    private TransferOrderMapper transferOrderMapper;

    @Override
    public void saveTransferOrder(TransferOrder transferOrder){
        transferOrderMapper.insert(transferOrder);
    }

    @Override
    public void updateStatusByObj(TransferOrder transferOrder, String status){
        EntityWrapper<TransferOrder> wrapper = new EntityWrapper<TransferOrder>();
        wrapper.eq("order_no",transferOrder.getOrderNo());
        wrapper.eq("status",transferOrder.getStatus());

        transferOrder.setStatus(status);
        transferOrder.setModifiedTime(new Date(System.currentTimeMillis()));
        transferOrderMapper.update(transferOrder,wrapper);
    }

    @Override
    public void updateProcessStatusByObj(TransferOrder transferOrder,String status){
        EntityWrapper<TransferOrder> wrapper = new EntityWrapper<TransferOrder>();
        wrapper.eq("order_no",transferOrder.getOrderNo());
        wrapper.eq("process_status",transferOrder.getProcessStatus());

        transferOrder.setProcessStatus(status);
        transferOrder.setModifiedTime(new Date(System.currentTimeMillis()));
        transferOrderMapper.update(transferOrder,wrapper);
    }

    @Override
    public void updateStatusAndTransferTime(TransferOrder order,
                                            String status,
                                            String processStatus,
                                            Long transferTime) {
        EntityWrapper<TransferOrder> wrapper = new EntityWrapper<TransferOrder>();
        wrapper.eq("order_no",order.getOrderNo());
        wrapper.eq("status", order.getStatus());
        wrapper.eq("process_status",order.getProcessStatus());

        order.setStatus(status);
        order.setProcessStatus(processStatus);
        order.setTransferTime(transferTime);
        order.setModifiedTime(new Date(System.currentTimeMillis()));
        transferOrderMapper.update(order, wrapper);
    }

    @Override
    public List<TransferOrder> getTransferOrderByStatus(String status, List<String> process, Page page){
        return transferOrderMapper.selectListForConfirm(status,process,page);
    }
}
