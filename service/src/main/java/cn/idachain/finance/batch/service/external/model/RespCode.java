package cn.idachain.finance.batch.service.external.model;

/**
 * @author yuanjiantao
 * @version 1.0
 * @since 2019/4/24 20:12
 */
public enum RespCode {
    // 成功：1开头
    SUCCESS("000000", "success"),

    // 通用错误：2开头
    SYS_ERROR("200001", "sys error"),
    ILLEGAL_ARGUMENT("200002", "illegal argument"),
    FILE_TYPE_ERROR("200003", "file type is wrong"),
    FILE_ALREADY_EXIST("200004", "file is existed"),
    LOGIN_TIMEOUT("200005", "login timeout"),
    PERMISSION_DENIED("200006", "permission denied"),
    //add by cuiyong 2017.11.10
    MISSING_PARAMETER("200007", "required parameter is missing"),
    FILE_NOT_EXIST("200008", "file not exist"),
    ACTIVITY_NOT_OPEN("200010", "activity not open"),
    FILE_FORMAT_NOT_SUPPORTED("200011", "file's format is not supported"),
    METHOD_NOTSUPPORT("200012", "request method is not supported"),
    FILE_RECORD_NOT_EXIST("200013", "file record not exist"),

    //System
    APP_SYSTEM_VERSION_NOT_EXIST("200014", "app version not exist"),
    BILL_HAS_BEEN_PROGRESSED("200019", "the bill has been processed"),

    GOOGLEPLAY_APPCODE_LOW("200020", "The version is too old to be used，please upgrade to the latest version."),
    NOT_SUPPORT_DEVICE("200021", "current device are not supported"),
    SIGNATURE_ERROR("200022", "签名错误"),


    // 各模块错误
    // 业务错误：3开头
    ILLEGAL_ACCOUNT("300001", "账户涉及违规操作，暂时无法使用通证兑换功能"),
    RATE_CHANGED("300002", "兑换比例发生变化，请刷新页面重新兑换"),
    FEE_CHANGED("300003", "手续费比例发生变化，请重新下单"),
    IDA_INSUFFICIENT_BALANCE("300004", "IDA可用余额不足，无法兑换"),
    MRT1_INSUFFICIENT_BALANCE("300005", "MRT1可用余额不足，无法兑换"),
    BLACKLIST_USER("300006", "账户涉及违规操作，暂时无法使用通证兑换功能"),
    CEX_INTERFACE_USER_STATUS_FORBIDDEN("300007", "您已被封禁，暂时无法使用兑换功能，如有疑问，请联系客服！"),
    CEX_USER_NOT_FOUND("300008", "登录失效，请重新登录"),
    CEX_INTERFACE_USER_NO_IS_EMPTY("300009", "获取cex用户ID为空"),
    NO_CHANCE_TO_EXCHANGE("300010", "本日兑换次数已用尽，请明日再发起兑换"),
    INSUFFICIENT_BALANCE("300011", "您的余额不足，请重新下单，如有疑问，请联系客服！"),
    UNKNOWN_ORDER_TYPE("300012", "未知订单类型"),
    PARAMETER_ERROR_CURRENCY("300013", "币种参数错误，如有疑问，请联系客服！"),
    PARAMETER_ERROR_AMOUNT("300014", "兑换数量参数错误，如有疑问，请联系客服！"),
    PARAMETER_ERROR("300015", "参数错误，如有疑问，请联系客服！"),
    SECURITY_PWD_ERROR("300016", "密码输入有误，请重试"),
    CLOSE("300017", "功能维护中，请稍后再试"),

    //
    BIZ_UNKNOWN_EXCEPTION("999999", "系统异常，如有疑问，请联系客服！"),
    BIZ_PARAMETER_ERROR("999102", "参数错误，如有疑问，请联系客服！"),
    DATE_PARSE_ERROR("999703", "日志转换异常，如有疑问，请联系客服！"),
    USER_NOT_FOUND("999028", "该用户不存在，如有疑问，请联系客服！"),
    MEMBER_STATUS_INVALID("999032", "用户状态异常，请联系客服，如有疑问，请联系客服！"),

    ;

    private String code;
    private String msg;

    RespCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static RespCode getByCode(String code) {
        for (RespCode item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
