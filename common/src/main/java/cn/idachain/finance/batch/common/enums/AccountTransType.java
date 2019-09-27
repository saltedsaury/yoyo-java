package cn.idachain.finance.batch.common.enums;

public enum AccountTransType {

    FINANCING("FINANCING","理财账户"),
    FEE("FEE","收费账户"),
    INSURANCE("INSURANCE","保险账户"),

    ;

    private String code;
    private String desc;

    AccountTransType(String code, String desc){
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
