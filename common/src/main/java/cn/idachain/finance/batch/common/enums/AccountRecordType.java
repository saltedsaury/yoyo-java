package cn.idachain.finance.batch.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author kun
 * @version 2019/10/8 15:07
 */
public enum AccountRecordType {

    PAY_BONUS("发放分红"),

    PAY_PRINCIPAL("到期还本"),

    COMPENSATE("保险理赔"),

    INVEST_CONFIRM("投资确认"),

    REDEMPTION("提前赎回");

    @Getter
    private String desc;

    AccountRecordType(String desc) {
        this.desc = desc;
    }

    public static AccountRecordType getByName(String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(AccountRecordType.values()).filter(e -> e.name().equals(name)).findAny().orElse(null);
    }
}
