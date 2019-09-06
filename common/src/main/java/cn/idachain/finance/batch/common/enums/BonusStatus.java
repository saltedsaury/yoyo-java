package cn.idachain.finance.batch.common.enums;

public enum BonusStatus {
    INIT("0","待发放"),
    AUDITING("1","待审批"),
    PREPARE("2","审批通过"),
    REJECT("3","审批拒绝"),
    FINISH("4","已发放"),
    CANCEL("5","因提前赎回取消");

    private String code;
    private String desc;

    BonusStatus(String code, String desc){
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
    public static BonusStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (BonusStatus rc : BonusStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }

}
