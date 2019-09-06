package cn.idachain.finance.batch.service.external.model;

import cn.idachain.finance.batch.service.external.CexResponse;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.Span;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by liuhailin on 2019/2/11.
 */
@Data
public class RespData<T> implements Serializable{
    private static final Logger log = LoggerFactory.getLogger(RespData.class);
    private static final long serialVersionUID = 4917480918640310535L;

    private static final String SUCCESS_RESP_CODE = "000000";
    private static final String DEFAULT_MSG = "success";

    @NotNull
    private String code = SUCCESS_RESP_CODE;
    private String msg = DEFAULT_MSG;
    private T data;
    private String traceId;

    public RespData() {
        this.traceId = MDC.get(Span.TRACE_ID_NAME);
    }

    public RespData(String code) {
        this.code = code;
        this.traceId = MDC.get(Span.TRACE_ID_NAME);
    }

    public RespData(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.traceId = MDC.get(Span.TRACE_ID_NAME);
    }

    public RespData(RespCode code, String msg) {
        this.code = code.getCode();
        this.msg = msg;
        this.traceId = MDC.get(Span.TRACE_ID_NAME);
    }

    public RespData(CexResponse cexResponse) {
        this.code = cexResponse.getCode();
        this.msg = cexResponse.getMsg();
        this.data = (T) cexResponse.getData();
        this.traceId = MDC.get(Span.TRACE_ID_NAME);
    }

    public RespData(RespCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
        this.traceId = MDC.get(Span.TRACE_ID_NAME);
    }

    public static <T> RespData<T> success(T data) {
        RespData<T> respData = new RespData();
        respData.setCode(SUCCESS_RESP_CODE);
        respData.setData(data);
        return respData;
    }

    public static <T> RespData<T> error(String code, String message, T data) {
        RespData<T> respData = new RespData();
        respData.setCode(code);
        respData.setMsg(message);
        respData.setData(data);
        return respData;
    }

    public static RespData error(String code, String message) {
        RespData respData = new RespData();
        respData.setCode(code);
        respData.setMsg(message);
        return respData;
    }

    public static <T> RespData<T> error(RespCode code) {
        RespData<T> respData = new RespData();
        respData.setCode(code);
        respData.setMsg(code.getMsg());
        return respData;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCode(RespCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public String getMsg() {
        if (this.msg == null || this.msg.length() == 0) {
            this.msg = "unknow error";
        }

        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        log.info("code is {}", this.code);
        return SUCCESS_RESP_CODE.equals(this.code);
    }
}
