package cn.idachain.finance.batch.common.enums;

public enum ValueMode {

    FIXED("0","固定起息日"),
    AUTO("1","自动起息");

    private String code;
    private String desc;

    ValueMode(String code, String desc){
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
    public static ValueMode getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (ValueMode rc : ValueMode.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
