package cn.idachain.finance.batch.service.external.model;

import lombok.Getter;

/**
 * Created by liuhailin on 2019/2/15.
 */
@Getter
public enum CexUserStatus {
    NORMAL(0,"正常"),FORBIDDEN(1,"封禁");
    private Integer code;
    private String desc;

    CexUserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CexUserStatus parse(Integer code) {
        for (CexUserStatus cus : CexUserStatus.values()) {
            if (cus.getCode().equals(code)) {
                return cus;
            }
        }
        return null;
    }
}
