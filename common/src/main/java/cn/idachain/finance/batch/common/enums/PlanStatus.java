package cn.idachain.finance.batch.common.enums;

public enum PlanStatus {
    INIT("0","待发放"),
    REDEMPT("1","提前赎回本金"),
    FINISH("2","已到期"),

    //以下用于统一界面显示，不存在于数据库中
    PAY("4","已发放");

    private String code;
    private String desc;

    PlanStatus(String code, String desc){
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
    public static PlanStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (PlanStatus rc : PlanStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }

}
