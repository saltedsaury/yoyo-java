package cn.idachain.finance.batch.common.enums;

public enum  BoolType {
    FALSE("0","FALSE"),
    TRUE("1","TRUE");

    private String code;
    private String desc;

    private BoolType(String code,String desc){
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
