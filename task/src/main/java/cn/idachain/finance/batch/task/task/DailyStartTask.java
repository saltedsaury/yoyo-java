package cn.idachain.finance.batch.task.task;

import cn.idachain.finance.batch.common.enums.BatchCode;
import cn.idachain.finance.batch.common.enums.BatchType;
import cn.idachain.finance.batch.common.dataobject.SystemBatch;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.dao.ISystemBatchDao;
import cn.idachain.finance.batch.service.dao.ISystemDateDao;
import cn.idachain.finance.batch.service.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@Service
public class DailyStartTask {
    @Autowired
    protected ISystemBatchDao systemBatchDao;
    @Autowired
    protected ISystemDateDao systemDateDao;

    @Scheduled(cron = "${task.financing.daily-start}")
    public void execute(){
        System.out.println("daily start begin.");
        SimpleAsyncTaskExecutor cmdDispatchExecutor = new SimpleAsyncTaskExecutor();
        cmdDispatchExecutor.setThreadNamePrefix("DailyStart_"+new Date(System.currentTimeMillis()).toString());
        cmdDispatchExecutor.execute(new BatchThread(systemBatchDao,systemDateDao));
    }

    private class BatchThread implements Runnable{
        private SystemBatch systemBatch;
        private ISystemDateDao systemDateDao;
        private ISystemBatchDao systemBatchDao;

        private BatchThread(ISystemBatchDao systemBatchDao,ISystemDateDao systemDateDao){
            this.systemDateDao = systemDateDao;
            this.systemBatchDao = systemBatchDao;
        }

        @Override
        public void run() {
            log.info("DailyStart thread run.");
            try {
                systemBatch = systemBatchDao.selectSystemBatchByPreCode(BatchCode.BATCH_START.getCode()
                        , BatchType.DAILY_START.getCode());
                log.info("起始任务：" + systemBatch);
                while (!BlankUtil.isBlank(systemBatch)) {
                    //TODO 如果一直执行失败，需要一个保障机制停止线程并报出异常
                    //执行当前任务直至成功
                    log.info("当前任务：" + systemBatch);
                    while (true) {
                        boolean success = false;
                        try {
                            Class<?> batch = Class.forName("cn.idachain.finance.batch.service.batch."
                                    + systemBatch.getBatchCode());
                            //Object batchClass = batch.newInstance();
                            Object batchClass = ApplicationContextHolder.getBeanByType(batch);
                            Method execute = batch.getMethod("execute");
                            success = (Boolean) execute.invoke(batchClass);
                            //任务执行成功，获取下一任务并跳出循环
                            if (success) {
                                log.info("DailyStart {} finish!", systemBatch.getBatchCode());
                                this.systemBatch = systemBatchDao.selectSystemBatchByPreCode(
                                        systemBatch.getBatchCode(),BatchType.DAILY_START.getCode());
                                break;
                            }
                        } catch (Exception e) {
                            log.error("excute DailyStart {} failed.error:{}", systemBatch.getBatchCode(),e);
                            throw new InterruptedException();
                        }
                    }
                }
            }catch(InterruptedException e){
                log.error("do DailyStart task failed.");
                e.printStackTrace();
            }
        }
    }
}
