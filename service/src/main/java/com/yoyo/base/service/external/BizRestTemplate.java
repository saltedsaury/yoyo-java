package com.yoyo.base.service.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class BizRestTemplate {

    @Value("${yz.server}")
    private String server;

    private static final String SLASH = "/";

    @Autowired
    private RestTemplate restTemplate;

    public <T> T post(String url, Class<T> returnClass, Map<String, Object> data) {
        HttpEntity<Map> entity = buildHttpEntity(data);
        log.info("postForEntity: url {}", getFullUrl(url));
        T t = restTemplate.postForEntity(getFullUrl(url), entity, returnClass).getBody();
        log.info("postForEntity: return {}", t);
        return t;
    }


    private String getFullUrl(String url) {
        if (!server.endsWith(SLASH) && !url.startsWith(SLASH)) {
            return server + SLASH + url;
        }
        return server + url;
    }

    private HttpEntity<Map> buildHttpEntity(Map<String, Object> data) {
        return new HttpEntity<Map>(data);
    }
}
