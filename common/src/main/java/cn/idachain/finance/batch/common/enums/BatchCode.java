package cn.idachain.finance.batch.common.enums;

public enum BatchCode {
    BATCH_START("0000","默认起始节点"),
    B0001("B0001","分红待审核"),
    B0002("B0002","产品到期"),
    B1001("B1001","产品成立"),
    B1002("B1002","申购确认"),
    B1003("B1003","分红发放"),
    B1004("B1004","提前赎回"),
    B1005("B1005","自动赎回"),
    B1006("B1006","保险逾期");

    private String code;
    private String desc;

    BatchCode(String code,String desc){
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
