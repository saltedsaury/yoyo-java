package cn.idachain.finance.batch.common.enums;

public enum Direction {

    IN("0","资金入"),
    OUT("1","资金出");

    private String code;
    private String desc;

    Direction(String code, String desc){
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
    public static Direction getByCode(String code) {
        if (null == code) {
            return null;
        }

        for (Direction rc : Direction.values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }

        return null;
    }
}
