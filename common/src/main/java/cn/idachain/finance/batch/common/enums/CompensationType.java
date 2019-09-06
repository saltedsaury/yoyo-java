package cn.idachain.finance.batch.common.enums;

public enum CompensationType {
    PROPORTION_CHANGE("0","比例兑换"),
    FIXED("1","固定值");

    private String code;
    private String desc;

    CompensationType(String code,String desc){
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
