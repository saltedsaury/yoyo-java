package cn.idachain.finance.batch.common.enums;

public enum InvestStatus {

    INIT("0","初始化"),
    APPLY_SUCCESS("1","申请成功"),
    INVEST_SUCCESS("2","投资成功"),
    GIVE_OUT("3","发放中"),
    REDEEMING("4","赎回中"),
    OVER_DUE("5","已到期"),
    ALREADY_REDEEMED("6","已赎回");

    private String code;
    private String desc;

    InvestStatus(String code,String desc){
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
    public static InvestStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (InvestStatus rc : InvestStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }

}
