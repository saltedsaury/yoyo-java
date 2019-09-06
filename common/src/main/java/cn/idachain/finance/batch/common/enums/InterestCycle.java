package cn.idachain.finance.batch.common.enums;

public enum InterestCycle {
    THREE(0,3,"三期"),
    SIX(1,6,"六期"),
    TWELVE(2,12,"十二期");

    private int code;
    private int times;
    private String desc;

    InterestCycle(int code,int times,String desc){
        this.code = code;
        this.times = times;
        this.desc = desc;
    }
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public int getTimes(){
        return times;
    }
}
