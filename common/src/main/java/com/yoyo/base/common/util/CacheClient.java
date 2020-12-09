package com.yoyo.base.common.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description: 缓存客户端，存对象须实现Serializable序列化接口
 *
 */
@Service
public class CacheClient extends BaseCacheClient {

    @Resource(name = "redisTemplate")
    @Override
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Resource(name = "stringRedisTemplate")
    @Override
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

}
