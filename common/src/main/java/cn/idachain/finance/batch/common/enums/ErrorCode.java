package cn.idachain.finance.batch.common.enums;

public enum ErrorCode {

    TRADE_ERROR("2001","发送交易异常");

    private String code;
    private String desc;

    ErrorCode(String code,String desc){
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
