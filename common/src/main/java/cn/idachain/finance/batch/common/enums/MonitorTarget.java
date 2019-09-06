package cn.idachain.finance.batch.common.enums;

/**
 * 监控指标枚举
 * abc
 */
public enum MonitorTarget {
    /**
     * 监控会每分钟读取一次日志数据
     * <p>
     * 各个数据说明
     * "target_name"  ----->  监控指标的名字
     * <p>
     * "method" ----->  监控方法，可以取的值有last,avg,sum,min,keyword,text
     * last: 执行一次日志读取时，获取指标的最新一条日志值
     * avg：执行一次日志读取，获取指标(可能有好几条日志产生)的平均值
     * sum：执行一次日志读取，获取指标(可能有好几条日志产生)的加权总和
     * min：执行一次日志读取，获取指标(可能有好几条日志产生)其中最小的一个值
     * keyword：执行一次日志读取，返回关键字在日志出现的次数
     * text：执行一次日志读取，直接返回文本值。
     * <p>
     * "compare" 和  "threshold" 共同构成报警阈值
     * "compare"支持>=, > ,< ,<=,!=,text
     * 例如 >= 和 1  ----->  日志值大于等于1就报警
     * 不需要报警时 "compare" 传空字符串，"threshold"传null
     */


    //计算IDA价格错误
    IDA_PRICE_ERROR("IDA_PRICE_ERROR", "keyword", ">=", 5L),
    //调用up失败
    INVOKE_UP_FAILED("INVOKE_UP_FAILED", "keyword", ">=", 5L),
    //用户冻结失败
    USER_FREEZE_FAILED("USER_FREEZE_FAILED", "keyword", ">=", 1L),
    //资金池余额不足
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "keyword", ">=", 1L),
    //获取的价格为null
    RATE_NULL("RATE_NULL", "keyword", ">=", 1L),
    ;


    private String targetName;
    private String method;
    private String compare;
    private Long threshold;

    MonitorTarget(String targetName, String method, String compare, Long threshold) {
        this.targetName = targetName;
        this.method = method;
        this.compare = compare;
        this.threshold = threshold;
    }

    public String getTargetName() {
        return this.targetName;
    }

    public String getMethod() {
        return this.method;
    }

    public String getCompare() {
        return this.compare;
    }

    public Long getThreshold() {
        return this.threshold;
    }
}
