package cn.idachain.finance.batch.service.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateIdUtil {


    public static long getId(ModuleEnum moduleEnum) {

        switch (moduleEnum) {
            case INVEST:
                return moduleEnum.getUUID(1);
            case TRANSFER:
                return moduleEnum.getUUID(2);
            case EARLYREDEMPATION:
                return moduleEnum.getUUID(3);
            case BONUS:
                return moduleEnum.getUUID(4);
            case REVENUEPLAN:
                return moduleEnum.getUUID(5);
            case INSURANCE:
                return moduleEnum.getUUID(6);
            case COMPENSATE:
                return moduleEnum.getUUID(7);
            case ACCOUNTDETAIL:
                return moduleEnum.getUUID(8);
            default:
                return 0;
        }
    }

    public  enum ModuleEnum {
        INVEST,
        TRANSFER,
        EARLYREDEMPATION,
        BONUS,
        REVENUEPLAN,
        INSURANCE,
        COMPENSATE,
        ACCOUNTDETAIL;

        private Snowflake singleton;

        ModuleEnum() {
            singleton = new Snowflake();
        }

        public long getUUID(int node) {
            singleton.setNode(node);
            return singleton.next();
        }
    }

}