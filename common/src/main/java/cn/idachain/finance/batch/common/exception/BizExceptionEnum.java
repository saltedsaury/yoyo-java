package cn.idachain.finance.batch.common.exception;

public enum BizExceptionEnum implements ServiceExceptionEnum {
    //参数异常
    DERICTION_ERROR(300001,"资金方向有误"),

    //业务异常
    SURPLUS_AMOUNT_NOT_ENOUGH(600001,"剩余投资金额不足！"),
    SECURITY_PWD_ERROR(600002,"资金密码校验失败！"),
    RECORD_NOT_EXIST(600003,"查询记录不存在！"),
    PRODUCT_NOT_EXIST(600004,"理财产品不存在！"),
    INSURANCE_NOT_EXIST(600005,"保险产品不存在！"),
    RECORD_ALREADY_EXIST(600006,"申请已提交，请勿重复提交！"),
    CAN_NOT_REDEEM(600007,"当前产品不支持提前赎回！"),
    EXCHANGE_RATE_NOT_EXIST(600008,"交易对当前利率不存在！"),



    //账务异常
    USER_BALANCE_NOT_ENOUGH(700001,"用户余额不足！"),
    INTERNAL_BALANCE_NOT_ENOUGH(700002,"内部户余额不足！"),
    OPEN_ACCOUNT_ERROR(700003,"开户失败！"),
    ASSET_FREEZE_ERROR(700004,"资产冻结失败,请检查余额"),


    //系统异常
    DB_ERROR(900001,"数据库操作失败！"),

    ;


    BizExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
