package cn.idachain.finance.batch.common.enums;

public enum  Channel {

    APP(0,"APP"),
    WEB(1,"WEB"),
    ALL(9,"ALL");

    private int code;
    private String desc;

    private Channel(int code,String desc){
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
