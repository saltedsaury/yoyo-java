package cn.idachain.finance.batch.common.enums;

public enum  AccountTransType {

    FINANCING("FINANCING","理财备付金"),
    PAYABLE("PAYABLE","应付账款"),
    LOAN("LOAN","短款"),
    BONUS("BONUS","收益发放"),
    COMPENSATION_OUT("COMPENSATION_OUT","理赔放款"),
    COMPENSATION_IN("COMPENSATION_IN","理赔收款"),
    INSURANCE_FEE("INSURANCE_FEE","保费收取"),
    FINE("FINE","赎回罚金");

    private String code;
    private String desc;

    AccountTransType(String code,String desc){
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
