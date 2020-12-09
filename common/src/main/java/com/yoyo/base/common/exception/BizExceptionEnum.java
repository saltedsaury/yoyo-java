package com.yoyo.base.common.exception;

public enum BizExceptionEnum implements ServiceExceptionEnum {
    //参数异常
    DERICTION_ERROR(300001,"资金方向有误"),

    //系统异常
    DB_ERROR(900001,"数据库操作失败！"),

    ;


    BizExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
