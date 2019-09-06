package cn.idachain.finance.batch.service.service;

public interface IBatchExecuteService {
    boolean execute(String batchCode, String batchType) throws Exception;
}
