package cn.idachain.finance.batch.common.enums;

public enum InterestMode {
    PRECYCLE("0","按月付息到期还本"),
    DISPOSABLE("1","一次性付息");

    private String code;
    private String desc;

    InterestMode(String code,String desc){
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
    public static InterestMode getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (InterestMode rc : InterestMode.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
