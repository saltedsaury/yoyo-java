package cn.idachain.finance.batch.common.enums;

public enum CompensationStatus {

    AUDITING("0","审批中"),
    COMPENSATION("1","赔付中"),
    REJECT("2","审批拒绝"),
    FINISH("3","已赔付");

    private String code;
    private String desc;

    CompensationStatus(String code, String desc){
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
    public static CompensationStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (CompensationStatus rc : CompensationStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
