package cn.idachain.finance.batch.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 建议不再使用
 */
@Component
@Slf4j
public class CacheUtil {

    @Autowired
    private CacheClient cacheClientAutowired;

    private static CacheClient cacheClient;

    public static Integer AUTH_CODE_RETRY = 3;

    @PostConstruct
    public void init() {
        this.cacheClient = cacheClientAutowired;
    }

    /**
     * 默认验证码过期时间，10分钟
     */
    private static final int TIME_OUT = 10 * 60;

    public static void put(String key, String value) {
        cacheClient.addValue(key, value, TIME_OUT);
    }

    public static <T> void put(String key, T value) {
        cacheClient.addValue(key, value, TIME_OUT);
    }

    public static String get(String key) {
        return (String) cacheClient.getValue(key);
    }

    public static <T> T get(String key, Class<T> value) {
        return (T) cacheClient.getValue(key);
    }


    public static void inValidate(String key) {
        JSONObject json = new JSONObject();
        try {
            json = get(key, JSONObject.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (!StringUtils.isBlank(json.getString("verifyCode"))) {
            json.put("verifyCode", "");
            CacheUtil.put(key, json);
        }
    }
}
