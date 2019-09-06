package cn.idachain.finance.batch.common.enums;

public enum RedemptionStatus {

    AUDITING("0","待审批"),
    REDEEMING("1","赎回中"),
    FINISH("2","已赎回"),
    REJECT("3","审批拒绝");

    private String code;
    private String desc;

    RedemptionStatus(String code,String desc){
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
    public static RedemptionStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (RedemptionStatus rc : RedemptionStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
