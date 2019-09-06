package cn.idachain.finance.batch.common.enums;

public enum InsuranceTradeStatus {
    INIT("0","未生效"),
    PREPARE("1","生效中"),
    WAIT_COMPENSATION("2","待理赔"),
    FINISH("3","已终止");

    private String code;
    private String desc;

    InsuranceTradeStatus(String code,String desc){
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
    public static InsuranceTradeStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (InsuranceTradeStatus rc : InsuranceTradeStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }

}
