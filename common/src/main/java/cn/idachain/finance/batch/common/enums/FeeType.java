package cn.idachain.finance.batch.common.enums;

public enum FeeType {

    PROPORTION("0","比例值"),
    FIXED("1","固定值");

    private String code;
    private String desc;

    FeeType(String code,String desc){
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
