package cn.idachain.finance.batch.common.enums;

public enum InterestType {
    BONUS("0","分红"),
    REWARD("1","奖励");

    private String code;
    private String desc;

    InterestType(String code,String desc){
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
