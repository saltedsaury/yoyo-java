package cn.idachain.finance.batch.common.enums;

public enum ProductStatus {
    INIT("0","初始化"),
    FOR_SALE("1","投资"),
    PAUSE("2","暂停购买"),
    OFF_SHELVE("3","已售罄"),
    LOCK_IN("4","发放中"),
    OPEN("5","已到期"),
    INVALID("6","产品失效");

    private String code;
    private String desc;

    ProductStatus(String code,String desc){
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
    public static ProductStatus getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (ProductStatus rc : ProductStatus.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
