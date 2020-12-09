package com.yoyo.base.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateIdUtil {

    public static long getId(ModuleEnum moduleEnum) {

        switch (moduleEnum) {
            case ACCOUNT:
                return moduleEnum.getUUID(0);
            case BIZ:
                return moduleEnum.getUUID(1);
            case PROD:
                return moduleEnum.getUUID(2);
            case BATCH:
                return moduleEnum.getUUID(3);
            default:
                return 0;
        }
    }

    public  enum ModuleEnum {
        ACCOUNT,
        BIZ,
        PROD,
        BATCH;

        private Snowflake singleton;

        ModuleEnum() {
            singleton = new Snowflake();
        }

        public long getUUID(int bizType) {
            singleton.setBizType(bizType);
            return singleton.next();
        }
    }

}
