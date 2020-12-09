package com.yoyo.base.service.external;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class YzAccessToken<T> implements Serializable {

    private static final long serialVersionUID = 4917480918640310535L;
    @NotNull
    private Boolean success = true;
    private String code = "success";
    private T data;
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
