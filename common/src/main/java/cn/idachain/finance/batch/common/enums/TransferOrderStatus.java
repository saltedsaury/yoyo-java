package cn.idachain.finance.batch.common.enums;

public enum TransferOrderStatus {
    INIT("0","待处理"),
    PROCESSING("1","处理中"),
    FAILED("2","划转失败"),
    SUCCESS("3","划转成功");

    private String code;
    private String desc;

    TransferOrderStatus(String code,String desc){
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
    public static TransferOrderStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (TransferOrderStatus rc : TransferOrderStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
