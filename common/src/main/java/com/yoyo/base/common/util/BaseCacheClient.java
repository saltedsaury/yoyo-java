package cn.idachain.finance.batch.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 *
 * @Date: 2018/7/9
 * @Time: 15:08
 */
@Slf4j
public class BaseCacheClient {

    protected RedisTemplate redisTemplate;

    protected StringRedisTemplate stringRedisTemplate;

    public RedisTemplate getRedisTemplate(){
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public StringRedisTemplate getStringRedisTemplate(){
        return stringRedisTemplate;
    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


    /**
     * 删除key
     *
     * @param key
     */
    public void delete(String key) {
        if (!StringUtils.isEmpty(key)) {
            redisTemplate.delete(key);
        }
    }


    /**
     * 判断指定key的hashKey是否存在
     *
     * @param key
     * @param hashKey
     * @return
     */
    public boolean hasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }


    /**
     * 设置超时时间
     *
     * @param key
     * @param timeout
     * @param unit
     */
    public void expire(String key, final long timeout, final TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }


    /**
     * 获取过期时间
     *
     * @param key
     * @return
     */
    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }


    /**
     * 删除多个key
     *
     * @param keys
     */
    public void delete(Set<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param expire 过期时间 单位秒
     */
    private void setExpire(String key, long expire) {
        if (expire != -1) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * 发布消息
     * @param channel 指定通道
     * @param content 消息内容
     */
    public void publish(String channel, String content) {
        if (StringUtils.isEmpty(channel) && StringUtils.isEmpty(content)) {
            stringRedisTemplate.convertAndSend(channel, content);
        }
    }

    /**
     * @param key
     * @param value  对象须实现Serializable序列化接口
     * @param expire 过期时间 单位秒
     */
    public void addValue(String key, Object value, long expire) {
        redisTemplate.opsForValue().set(key, value);

        setExpire(key, expire);
    }


    /**
     * 可存入普通对象，list<Object>
     *
     * @param key
     * @param value    对象须实现Serializable序列化接口
     * @param expire
     * @param timeUnit
     */
    public void addValue(String key, Object value, long expire, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    /**
     * 普通对象设置key-value值, 无过期时间
     *
     * @param key
     * @param value 对象须实现Serializable序列化接口
     */
    public void addValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public boolean addValueNX(String key, Object value,long expire){
        if (redisTemplate.opsForValue().setIfAbsent(key, value)){
            setExpire(key, expire);
            return true;
        }

        return false;
    }
    /**
     * @param key
     * @return
     */
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key
     * @return
     */
    public String getStringValue(String key) {
        return String.valueOf(redisTemplate.opsForValue().get(key));
    }

    /**
     * stringRedisTemplate 获取值
     * @param key
     * @return
     */
    public Object getStringRedisTemplateValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 可自定义缓存时间粒度
     * @param key
     * @param hashKey
     * @param data
     * @param expire
     * @param timeUnit
     */
    public void addHashValue(String key, String hashKey, Object data, long expire, TimeUnit timeUnit) {
        redisTemplate.opsForHash().put(key, hashKey, data);
        if (expire != -1) {
            redisTemplate.expire(key, expire, timeUnit);
        }
    }

    /**
     * 向redis 中添加Hash内容
     *
     * @param key     保存key
     * @param hashKey hashKey
     * @param data    保存对象 data
     * @param expire  过期时间秒    -1：表示不过期
     */
    public void addHashValue(String key, String hashKey, Object data, long expire) {
        redisTemplate.opsForHash().put(key, hashKey, data);

        setExpire(key, expire);
    }

    /**
     * Hash 添加数据
     *
     * @param key    key
     * @param map    data
     * @param expire 过期时间秒    -1：表示不过期
     */
    public void addAllHashValue(String key, Map<String, Object> map, long expire) {
        redisTemplate.opsForHash().putAll(key, map);

        setExpire(key, expire);
    }

    /**
     * 删除hash key
     *
     * @param key     key
     * @param hashKey hashKey
     */
    public long deleteHashValue(String key, String hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }


    /**
     * 获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object getHashValue(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }


    /**
     * 批量获取数据
     *
     * @param key
     * @return
     */
    public List<Object> getListHashAllValue(String key) {
        return redisTemplate.opsForHash().values(key);
    }


    /**
     * 批量获取数据
     *
     * @param key
     * @return
     */
    public Map getHashAllValue(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * 批量获取指定hashKey的数据
     *
     * @param key
     * @param hashKeys
     * @return
     */
    public List<Object> getHashMultiValue(String key, List<String> hashKeys) {
        return redisTemplate.opsForHash().multiGet(key, hashKeys);
    }


    /**
     * 获取hash数量
     *
     * @param key
     * @return
     */
    public Long getHashCount(String key) {
        return redisTemplate.opsForHash().size(key);
    }


    /**
     * 设置zset值
     *
     * @param key
     * @param member
     * @param score
     * @return
     */
    public boolean addZSetValue(String key, Object member, long score) {
        return redisTemplate.opsForZSet().add(key, member, score);
    }

    /**
     * 设置zset值
     *
     * @param key
     * @param member
     * @param score
     * @return
     */
    public boolean addZSetValue(String key, Object member, double score) {
        return redisTemplate.opsForZSet().add(key, member, score);
    }


    /**
     * 批量设置zset值
     *
     * @param key
     * @param tuples
     * @return
     */
    public long addBatchZSetValue(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        return redisTemplate.opsForZSet().add(key, tuples);
    }


    /**
     * 自增zset值
     *
     * @param key
     * @param member
     * @param delta
     */
    public void incZSetValue(String key, String member, long delta) {
        redisTemplate.opsForZSet().incrementScore(key, member, delta);
    }


    /**
     * 获取zset数量
     *
     * @param key
     * @param member
     * @return
     */
    public long getZSetScore(String key, String member) {
        Double score = redisTemplate.opsForZSet().score(key, member);
        if (score == null) {
            return 0;
        } else {
            return score.longValue();
        }
    }


    /**
     * 获取有序集 key 中成员 user 的排名 。其中有序集成员按 score 值递减 (从小到大) 排序。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetRank(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }


    /**
     * 从左添加到list列表
     *
     * @param key
     * @param list
     */
    public Long leftPushList(String key, Object list) {
        return redisTemplate.opsForList().leftPush(key, list);
    }

    /**
     * 从右添加到list列表
     *
     * @param key
     * @param list
     * @return
     */
    public Long rightPushList(String key, Object list) {
        return redisTemplate.opsForList().rightPush(key, list);
    }


    /**
     * 从左边开始遍历list
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Object rangeList(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }


    /**
     * 查询size
     *
     * @param key
     * @return
     */
    public Long sizeList(String key) {
        return redisTemplate.opsForList().size(key);
    }


    /**
     * 从左弹出一个元素
     *
     * @param key
     * @return
     */
    public Object leftPopList(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从右弹出一个元素
     *
     * @param key
     * @return
     */
    public Object rightPopList(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }


    /**
     * 更新某一个元素，从左边开始序号
     *
     * @param key
     * @param index
     * @param value
     */
    public void leftUpdateList(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }


    /**
     * 添加Set集合
     *
     * @param key
     * @param list
     */
    public Long addSetValue(String key, Object list) {
        return redisTemplate.opsForSet().add(key, list);
    }


    /**
     * 获取指定Key对应的set
     *
     * @param key
     * @return
     */
    public Object getSetValue(String key) {
        return redisTemplate.opsForSet().members(key);
    }


    /**
     * 获取并移除指定key的值
     *
     * @param key
     * @return
     */
    public Object popSetValue(String key) {
        return redisTemplate.opsForSet().pop(key);
    }


    public Long getSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }


    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * Hash 添加数据-泛型value
     *
     * @param key    key
     * @param map    data
     * @param expire 过期时间秒    -1：表示不过期
     *
     * @date 2018/7/5
     */
    public <V> void addAllHashValueWithV(String key, Map<String, V> map, long expire) {
        redisTemplate.opsForHash().putAll(key, map);

        setExpire(key, expire);
    }

    /**
     * 把数据缓存到 Redis BitMaps集合中
     * @param key    键
     * @param offset 偏移量
     * @param value  值
     * @return 设置成功返回true
     */
    public Boolean setBit(Object key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 根据键和偏移量判断是否在 Redis BitMaps集合中存在
     * @param key    键
     * @param offset 偏移量
     * @return 存在返回true
     */
    public Boolean getBit(Object key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 加锁
     * @param key   商品id
     * @param value 当前时间+超时时间
     * @return
     */
    public boolean lock(String key, String value) {
        if (redisTemplate.opsForValue().setIfAbsent(key, value)) {
            return true;
        }

        //避免死锁，且只让一个线程拿到锁
        String currentValue = getStringValue(key);
        //如果锁过期了
        if (currentValue!="null" && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            //获取上一个锁的时间
            String oldValues = String.valueOf(redisTemplate.opsForValue().getAndSet(key, value));

            if (oldValues!="null" && oldValues.equals(currentValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解锁
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = getStringValue(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
            log.error("『redis分布式锁』解锁异常，{}", e);
        }
    }
}
