package cn.idachain.finance.batch.service.service;

import java.lang.reflect.InvocationTargetException;

public interface IBatchExecuteService {
    boolean execute(String batchCode, String batchType) throws Exception;

    boolean taskexecute(String taskName) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException;
}
