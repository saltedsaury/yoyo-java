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
    LESS_THAN_MIN_AMOUNT(600009,"投资金额不能低于起投金额！"),
    TRANSFER_ERROR(600010,"划转失败"),
    PRODUCT_STATUS_NOT_ALLOWED_INVEST(600011,"当前产品状态不允许投资！"),
    INVEST_AMOUNT_NOT_ALLOWED(600012,"投资金额不符合尾标规则，请调整金额后重新投资！"),
    CAN_NOT_REDEEM_ON_BONUS_DATE(600013,"收益发放日不支持提前赎回！"),
    QUOTA_AMOUNT_NOT_ALLOWED(600014,"投资金额非定额！"),
    GRAD_AMOUNT_NOT_ALLOWED(600015,"投资金额不满足投资梯度！"),
    MORE_THAN_MIN_AMOUNT(600016,"投资金额不能高于单笔最大金额！"),
    SUBSCRIBE_TIMES_NOT_ENOUGH(600017,"剩余认购次数不足！"),
    SURPLUS_TIMES_LESS_THAN_ZERO(600017,"剩余认购超过限制！"),
    TOO_MANY_VISITS(600018,"访问过于频繁，请稍后重试！"),
    UNKNOWN_SURPLUS_AMOUNT(600019,"剩余投资金额未初始化"),



    //账务异常
    USER_BALANCE_NOT_ENOUGH(700001,"用户余额不足！"),
    INTERNAL_BALANCE_NOT_ENOUGH(700002,"内部户余额不足！"),
    OPEN_ACCOUNT_ERROR(700003,"开户失败！"),
    ASSET_FREEZE_ERROR(700004,"资产冻结失败,请检查余额"),
    ORG_ACCOUNT_NOT_EXIST(700005,"机构账户不存在"),
    UPDATE_BALANCE_FAILED(700006,"余额更新失败"),
    RECONCILE_FAILED(700007, "内部对账失败"),

    //系统异常
    DB_ERROR(900001,"数据库操作失败！"),
    SYSTEM_BATCH_DATE_ERROR(900002,"批处理日期异常！"),

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
