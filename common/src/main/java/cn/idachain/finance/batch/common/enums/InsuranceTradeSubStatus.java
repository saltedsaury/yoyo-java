package cn.idachain.finance.batch.common.enums;

public enum InsuranceTradeSubStatus {
    NO_APPLICATION("300","未申请理赔"),
    APPLIED("301","已申请理赔"),
    FINISHI_COMPENSATION("304","理赔完成"),
    BE_OVERDUE("302","逾期未申请"),
    GIVE_UP("303","放弃理赔");

    private String code;
    private String desc;

    InsuranceTradeSubStatus(String code, String desc){
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
    public static InsuranceTradeSubStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (InsuranceTradeSubStatus rc : InsuranceTradeSubStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }

}
