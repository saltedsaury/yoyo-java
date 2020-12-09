package com.yoyo.base.web.resp;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * 接口统一返回结果
 * 使用isSuccess判断调用是否成功 ,如果为true,则可以调用getResult,如果为false,则调用errorCode来获取出错信息
 * <p>
 * 1、isSuccess         判断调用是否成功
 * 2、getResult         获取调用结果集
 * 3、setResult         设置调用结果集
 * 4、getErrorCode      获取错误码
 * 5、setErrorCode      设置错误码
 * 6、getErrorMsg       获取错误描述
 * 7、setErrorMsg       设置错误描述
 * </p>
 */
public class CommonResult<T> implements Serializable {
    private static final long serialVersionUID = -4923904868804182038L;

    /**
     * 调用是否成功
     */
    private boolean success;

    /**
     * 调用结果集
     */
    private T result;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误描述
     */
    private String errorMsg;

    /**
     * 默认构造方法
     */
    public CommonResult() {
    }

    /**
     * 直接构造成功的返回
     *
     * @param result
     */
    public CommonResult(T result) {

        this.success = true;
        this.result = result;
        this.errorCode = "000000";
        this.errorMsg = "success";
    }

    /**
     * 直接构造失败的返回
     *
     * @param errorCode 错误码
     * @param errorMsg  错误描述
     */
    public CommonResult(String errorCode, String errorMsg) {

        this.success = false;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 判断调用是否成功
     *
     * @return
     */
    public boolean isSuccess() {

        return success;
    }

    /**
     * 获取调用结果集
     *
     * @return
     */
    public T getResult() {

        return result;
    }

    /**
     * 设置调用结果集
     *
     * @param result 结果集
     */
    public void setResult(T result) {

        success = true;
        this.result = result;
    }

    /**
     * 获取错误码
     *
     * @return
     */
    public String getErrorCode() {

        return errorCode;
    }

    /**
     * 设置错误码
     *
     * @param errorCode 错误码
     */
    public void setErrorCode(String errorCode) {

        this.success = false;
        this.errorCode = errorCode;
    }

    /**
     * 获取错误描述
     *
     * @return
     */
    public String getErrorMsg() {

        return errorMsg;
    }

    /**
     * 设置错误描述
     *
     * @param errorMsg 错误描述
     */
    public void setErrorMsg(String errorMsg) {

        this.errorMsg = errorMsg;
    }

    /**
     * 重写toString方法
     *
     * @return
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static CommonResult of(int code, String msg) {
        return new CommonResult(String.valueOf(code), msg);
    }
    public static Object badGateway() {
        return "SYS_ERROR|traceId:" + MDC.get("X-B3-TraceId");
    }

}
