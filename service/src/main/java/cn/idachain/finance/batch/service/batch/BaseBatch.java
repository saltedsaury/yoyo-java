package cn.idachain.finance.batch.service.batch;

import cn.idachain.finance.batch.service.util.DateUtil;
import cn.idachain.finance.batch.common.constants.BizConstants;
import cn.idachain.finance.batch.common.enums.BatchCode;
import cn.idachain.finance.batch.common.enums.BatchStatus;
import cn.idachain.finance.batch.common.dataobject.SystemBatch;
import cn.idachain.finance.batch.common.dataobject.SystemDate;
import cn.idachain.finance.batch.service.dao.ISystemBatchDao;
import cn.idachain.finance.batch.service.dao.ISystemDateDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
public class BaseBatch {

    @Autowired
    private ISystemBatchDao systemBatchDao;
    @Autowired
    private ISystemDateDao systemDateDao;

    protected SystemBatch systemBatch;
    protected SystemDate systemDate;

    public void beforeExcute(String batchCode){
        systemBatch = systemBatchDao.selectSystemBatchByCode(batchCode);
        systemDate = systemDateDao.getSystemDateByType(BizConstants.SYSTEM_BATCH);
    }

    public boolean checkStatus(){
        if(BatchCode.BATCH_START.getCode().equals(systemBatch.getPreBatchCode())){
            Date current = new Date(System.currentTimeMillis());
            log.error("check system date : {},real date :{}",systemDate.getSystemDate(),current);
            return DateUtil.isSameDay(systemDate.getSystemDate(), current);
        }
        SystemBatch parent = systemBatchDao.selectSystemBatchByCode(systemBatch.getPreBatchCode());
        if (!DateUtil.isSameDay(systemDate.getSystemDate(),parent.getFinishDate())){
            log.error("check parent batch finish date fail,system date:{},parent finish date:{}");
            return false;
        }
        return BatchStatus.SUCCESS.getCode().equals(parent.getStatus());
    }

    public void afterExecute(){
        //任务完成状态更新
        log.info("update batch {} finish date :{}",systemBatch.getBatchCode(),systemBatch);
        systemBatch.setFinishDate(systemDate.getSystemDate());
        systemBatchDao.updateSystemBatchStatus(systemBatch,BatchStatus.SUCCESS.getCode());
    }

}
