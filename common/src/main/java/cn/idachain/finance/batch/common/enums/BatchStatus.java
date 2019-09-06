package cn.idachain.finance.batch.common.enums;

public enum BatchStatus {

    INIT("0","初始化"),
    PREPARE("1","审核完成"),
    SUCCESS("2","完成"),
    INVALID("3","失败");

    private String code;
    private String desc;

    BatchStatus(String code,String desc){
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
