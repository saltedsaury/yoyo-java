package cn.idachain.finance.batch.common.exception;

public interface ServiceExceptionEnum {


    /**
     * 获取异常编码
     */
    Integer getCode();

    /**
     * 获取异常信息
     */
    String getMessage();
}
