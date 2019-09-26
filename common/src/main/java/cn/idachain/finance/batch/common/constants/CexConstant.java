package cn.idachain.finance.batch.common.constants;

/**
 * Created by liuhailin on 2019/1/30.
 */
public class CexConstant {
    // 获取用户信息
    public static final String USER_INFO_URL = "/apis/channel/user/queryUserInfo";

    // 获取用户信息
    public static final String USER_INFO_URL_BY_SELF = "/apis/user/query/userInfo";

    // 获取用户状态信息
    public static final String USER_STATE_URL = "/apis/channel/queryUserInfo";

    // 获取二级代理验证码
    public static final String MSG_CODE_GET_URL = "/apis/channel/send/oneWayVerifyCode";

    // 验证二级代理验证码
    public static final String MSG_CODE_VERIFY_URL = "/apis/channel/check/oneWayVerifyCode";

    // 返佣转账
    public static final String COMMISSION_TRANSFER_URL = "/apis/channel/commission/giveback";

    public static final String CURRENCY_RATIO = "/apis/channel/query/exchangeRate";

    public static final String USER_INVENTORY = "/apis/channel/queryBatchUserAssetTotalInfo";

    public static final String CHARGE_ADDR = "/apis/channel/getRechgCurrAddrByCode";
    public static final String TRANSFER_OUT_WITHOUT_TOKEN = "/apis/channel/transferOutWithoutToken";


    // 用户资金密码校验
    public static final String CHANNEL_PASSWORD_CHECK_SECURITYPWD = "/apis/channel/user/check/securityPwd";
    // 图片（收付款的二维码）上传/访问
    public static final String USER_FILE_UPLOAD_FILE = "/apis/user/file/upload/file";
    // 读取文件
    public static final String USER_FILE_READ_FILE = "/apis/user/file/read/file";
    // 短信通知：签名
    public static final String MESSAGE_SEND_ONEWAYNOTIFY = "/apis/channel/send/oneWayNotify";
    // 资金划转：转入
    public static final String TRANSFER_IN = "/apis/channel/user/transferIn";
    // 资金划转：转出
    public static final String TRANSFER_OUT = "/apis/channel/user/transferOut";
    // 查询用户对应币种余额
    public static final String USER_ASSET_QUERY_CRRENCY_ASSET = "/apis/channel/user/queryCurrencyAsset";

    //查询最新成交记录
    public static final String QUERY_DEAL_LIST = "/apis/order/query/dealList";

    //冻结
    public static final String ASSET_FREEZE = "/apis/channel/user/assetFreeze";

    //解冻
    public static final String ASSET_UNFREEZE = "/apis/channel/assetUnFreeze";

    // 批次转入
    public static final String BATCH_TRANSFER_IN = "/apis/channel/batchTransferIn";

    //资产解冻并渠道划出
    public static final String UNFREEZE_TRANSFER_OUT = "/apis/channel/assetUnFreezeTransferOut";

    /**
     * 渠道短款
     */
    public static final String LOAN = "/apis/channel/loan";

    /**
     * 渠道资金池余额
     */
    public static final String FUNDS_LEFT = "/apis/channel/funds/left";

    /**
     * 渠道借款账户余额
     */
    public static final String LOAN_LEFT = "/apis/channel/loan/left";

    public static final String SELECT_TRANSFER_LIST = "/apis/c2c/funds/queryTransferList";

    public static final String HEADER_PARAM_CEX_PASSPORT = "CEXPASSPORT";
    //public static final String HEADER_PARAM_CEX_TOKEN = "CEXTOKEN";
    public static final String HEADER_PARAM_DEVICEID = "DEVICEID";
    public static final String HEADER_PARAM_DEVICEID_VALUE = "deviceid_job";
    public static final String HEADER_PARAM_DEVICESOURCE = "DEVICESOURCE";
    public static final String HEADER_PARAM_DEVICESOURCE_VALUE = "web";
    public static final String HEADER_PARAM_CEX_SIGNATURE = "CEXCHANNELSIGN";
    public static final String HEADER_PARAM_REC_SIGNATURE = "Rec-Sign";

    public static final String HEADER_PARAM_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_PARAM_CONTENT_TYPE_VALUE = "application/json";
    public static final String HEADER_PARAM_CEX_BIZ_CHANNEL = "CEXCHANNEL";
    public static final String HEADER_PARAM_CEX_BIZ_CHANNEL_VALUE = "28";
    public static final String HEADER_PARAM_CEX_BIZ_CHANNEL_OUT_ORDER_NO = "CEXCHANNELOUTORDERNO";


    public static final String PARAM_LANG = "lang";
    public static final String PARAM_LANG_VALUE = "zh-CN";
    public static final String PARAM_DATA = "data";
    public static final String PARAM_DATA_REQUESTPARAM = "requestParam";
    public static final String PARAM_DATA_SECURITYPWD = "securityPwd";
    public static final String PARAM_DATA_CURRENCY = "currency";
    public static final String PARAM_DATA_SYMBOL = "symbol";

    public static final String VALUE_DEVICEID = "DEVICEID";
    public static final String VALUE_DEVICESOURCE = "web";



    public static final String USER_INFO_USER_NO = "userNo";
}
