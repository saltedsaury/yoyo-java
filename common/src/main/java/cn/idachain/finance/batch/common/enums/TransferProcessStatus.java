package cn.idachain.finance.batch.common.enums;

public enum TransferProcessStatus {

    INIT("0","待处理"),
    CHARGEBACK_FAILED("1","扣款失败"),
    CHARGEBACK_SUCCESS("2","扣款成功"),
    TRANSFERED_FAILED("3","划转失败"),
    SUCCESS("4","划转成功");

    private String code;
    private String desc;

    TransferProcessStatus(String code,String desc){
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
    public static TransferProcessStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (TransferProcessStatus rc : TransferProcessStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }

}
