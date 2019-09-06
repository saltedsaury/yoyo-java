package cn.idachain.finance.batch.common.enums;

public enum ReturnType {
    PRINCIPAL("0","本金"),
    BONUS("1","分红");

    private String code;
    private String desc;

    ReturnType(String code,String desc){
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取枚举
     */
    public static ReturnType getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (ReturnType rc : ReturnType.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
