package cn.idachain.finance.batch.common.enums;

public enum TimeUnit {
    DAY("0","日",1),
    WEEK("1","周",7),
    MONTH("2","月",30),
    YEAR("3","年",360);

    private String code;
    private String desc;
    private int day;

    TimeUnit(String code,String desc,int day){
        this.code = code;
        this.desc = desc;
        this.day = day;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public int getDay() { return day; }

    /**
     * 根据code获取枚举
     */
    public static TimeUnit getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (TimeUnit rc : TimeUnit.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
