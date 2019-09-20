package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.service.service.IBatchExecuteService;
import cn.idachain.finance.batch.service.util.ApplicationContextHolder;
import cn.idachain.finance.batch.service.dao.ISystemBatchDao;
import cn.idachain.finance.batch.service.dao.ISystemDateDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Service
public class BatchExecuteService implements IBatchExecuteService {
    @Autowired
    protected ISystemBatchDao systemBatchDao;
    @Autowired
    protected ISystemDateDao systemDateDao;

    @Override
    public boolean execute(String batchCode, String batchType) throws Exception {
        try {
            log.info("Batch {} run.", batchCode);
            Class<?> batch = Class.forName("cn.idachain.finance.batch.service.batch."
                    + batchCode);
            //Object batchClass = batch.newInstance();
            Object batchClass = ApplicationContextHolder.getBeanByType(batch);
            Method execute = batch.getMethod("execute");
            return (Boolean) execute.invoke(batchClass);
        }catch (Exception e) {
            log.error("Batch {} excute error,{}",batchCode,e);
            throw e;
        }
    }

    @Override
    public boolean taskexecute(String taskName) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        try {
            log.info("task {} run.", taskName);
            Class<?> batch = Class.forName("cn.idachain.finance.batch.task.task."
                    + taskName);
            //Object batchClass = batch.newInstance();
            Object batchClass = ApplicationContextHolder.getBeanByType(batch);
            Method execute = batch.getMethod("execute");
            return (Boolean) execute.invoke(batchClass);
        }catch (Exception e) {
            log.error("task {} excute error,{}",taskName,e);
            throw e;
        }
    }
}
