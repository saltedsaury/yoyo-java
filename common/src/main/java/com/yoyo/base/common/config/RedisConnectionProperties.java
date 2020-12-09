package cn.idachain.finance.batch.common.config;

import lombok.Data;

@Data
public class RedisConnectionProperties {

    /**
     * 最大空闲连接数, 默认8个
     */
    private int maxIdle;

    /**
     * 最大连接数, 默认8个
     */
    private int maxTotal;

    /**
     * 连接地址
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    /**
     * 密码
     */
    private String password;
}

