package cn.idachain.finance.batch.common.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConfigurationProperties("redis.common")
@Setter
public class RedisConfig extends RedisConnectionProperties {

    /**
     * Jedis客户端连接配置
     * RedisHttpSession 默认连接池
     *
     * @return
     */
    @Bean(name = "redisConnectionFactory")
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(this.getHost());
        factory.setPort(this.getPort());
        factory.setPassword(this.getPassword());

        JedisPoolConfig config = factory.getPoolConfig();
        config.setMaxIdle(this.getMaxIdle());
        config.setMaxTotal(this.getMaxTotal());
        return factory;
    }


    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory());

        setStringKeySerializer(template);

        template.afterPropertiesSet();
        return template;
    }


    private void setStringKeySerializer(StringRedisTemplate template) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
    }

}