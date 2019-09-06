package cn.idachain.finance.batch.common.enums;

public enum InsuranceFeeType {
    INVEST_PROPORTION("0","投资比例值"),
    BONUS_PROPORTION("1","分红比例值"),
    FIXED("2","固定值");

    private String code;
    private String desc;

    InsuranceFeeType(String code, String desc){
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
