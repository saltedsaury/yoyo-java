package cn.idachain.finance.batch.common.enums;

public enum ProductType {

    FINANCING(1,"理财产品"),
    SUBSCRIBE(2,"认购产品");

    private int code;

    private String desc;

    ProductType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
