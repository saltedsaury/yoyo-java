package cn.idachain.finance.batch.common.exception;

public enum CommonExceptionEnum implements ServiceExceptionEnum {
    ILLEGAL_ARGUMENT(100001,"参数异常"),

    DEFAULT_SYS_ERROR(999999,"系统异常"),
    ;


    CommonExceptionEnum(int code, String message) {
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
