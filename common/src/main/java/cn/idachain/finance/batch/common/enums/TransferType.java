package cn.idachain.finance.batch.common.enums;

public enum TransferType {

    CUSTOMER("0","用户划转"),
    SYSTEM("1","系统划转");

    private String code;
    private String desc;

    TransferType(String code,String desc){
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
    public static TransferType getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (TransferType rc : TransferType.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
