package cn.idachain.finance.batch.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author kun
 * @version 2019/10/8 14:57
 */
public enum AccountType {

    INTERNAL("内部账户"),

    PERSON("用户账户"),

    ORG("机构账户");

    @Getter
    private String desc;

    AccountType(String desc) {
        this.desc = desc;
    }

    public static AccountType getByName(String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(AccountType.values()).filter(e -> e.name().equals(name)).findAny().orElse(null);
    }
}
