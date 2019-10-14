package cn.idachain.finance.batch.common.enums;

public enum InsuranceStatus {

    ACTIVE("1","生效中"),
    EXPIRED("9","已过期");

    private String code;
    private String desc;

    InsuranceStatus(String code, String desc){
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
    public static InsuranceStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (InsuranceStatus rc : InsuranceStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
