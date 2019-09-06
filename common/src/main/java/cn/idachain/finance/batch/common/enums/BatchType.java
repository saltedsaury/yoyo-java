package cn.idachain.finance.batch.common.enums;

public enum BatchType {
    DAILY_START("0","日启"),
    DAILY_END("1","日终");

    private String code;
    private String desc;

    BatchType(String code, String desc){
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
