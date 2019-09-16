package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.SystemBatch;
import cn.idachain.finance.batch.common.mapper.SystemBatchMapper;
import cn.idachain.finance.batch.service.dao.ISystemBatchDao;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SystemBatchDao implements ISystemBatchDao {

    @Autowired
    private SystemBatchMapper systemBatchMapper;

    @Override
    public SystemBatch selectSystemBatchByCode(String batchCode){
        SystemBatch systemBatch = new SystemBatch();
        systemBatch.setBatchCode(batchCode);

        return systemBatchMapper.selectOne(systemBatch);
    }

    @Override
    public SystemBatch selectSystemBatchByPreCode(String preBatchCode,String batchType){
        SystemBatch systemBatch = new SystemBatch();
        systemBatch.setPreBatchCode(preBatchCode);
        systemBatch.setBatchType(batchType);

        return systemBatchMapper.selectOne(systemBatch);
    }

    @Override
    public void updateSystemBatchStatus(SystemBatch systemBatch,String status){
        EntityWrapper<SystemBatch> wrapper = new EntityWrapper<SystemBatch>();
        wrapper.eq("batch_code",systemBatch.getBatchCode());
        wrapper.eq("status",systemBatch.getStatus());

        systemBatch.setStatus(status);
        systemBatch.setModifiedTime(new Date(System.currentTimeMillis()));
        systemBatchMapper.update(systemBatch,wrapper);
    }
}
