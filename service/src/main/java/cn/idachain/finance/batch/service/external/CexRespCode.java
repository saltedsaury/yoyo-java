package cn.idachain.finance.batch.service.external;

import lombok.Getter;

/**
 * Created by liuhailin on 2019/2/13.
 */
@Getter
public enum CexRespCode {
    SUCCESS("000000", "成功"),
    // 需要重试
    INVOKE_EXCEPTION("999001", "调用接口异常"),
    SUCCESS_TRANSFER_FAIL("999003", "接口调用成功，划转失败")
    ;

    private String code;
    private String desc;
    CexRespCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CexRespCode parse(String code, CexRespCode defaultValue) {
        for (CexRespCode c : CexRespCode.values()) {
            if (c.getCode().equals(code)) {
                return c;
            }
        }
        return defaultValue;
    }

    public static boolean isSuccess(String code) {
        return SUCCESS.getCode().equals(code);
    }
}
