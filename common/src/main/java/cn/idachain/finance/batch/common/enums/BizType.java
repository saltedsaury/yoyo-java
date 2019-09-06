package cn.idachain.finance.batch.common.enums;

public enum BizType {

    INVEST("1","理财投资");

    private String code;
    private String desc;

    BizType(String code,String desc){
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
