package cn.idachain.finance.batch.service.external;

import cn.idachain.finance.batch.common.constants.CexConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.testng.collections.Maps;

import java.util.Map;

/**
 * Created by liuhailin on 2019/1/30.
 */
@Slf4j
@Component
public class CexRestTemplate {

    @Value("${vote.cex.server}")
    private String server;

    private static final String SLASH = "/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    AsyncRestTemplate asyncRestTemplate;

    @Autowired
    private HttpClient httpClient;

    public <T> T post(String url, Class<T> returnClass, CexRequest req) {
        HttpEntity<Map> entity = buildHttpEntity(req);
        log.info("postForEntity: url {}", getFullUrl(url));
        T t = restTemplate.postForEntity(getFullUrl(url), entity, returnClass).getBody();
        log.info("postForEntity: return {}", t);
        return t;
    }

    public <T> ResponseEntity<T> exchange(String url, Class<T> returnClass, CexRequest req) {
        HttpEntity<Map> entity = buildHttpEntity(req);
        log.info("exchange url {}", getFullUrl(url));
        return restTemplate.exchange(getFullUrl(url), HttpMethod.GET, entity, returnClass);
    }

    public <T> ListenableFuture<ResponseEntity<T>> asyncPost(String url, Class<T> returnClass, CexRequest req) {
        HttpEntity<Map> entity = buildHttpEntity(req);
        return asyncRestTemplate.postForEntity(getFullUrl(url), entity, returnClass);
    }

    private String getFullUrl(String url) {
        if (!server.endsWith(SLASH) && !url.startsWith(SLASH)) {
            return server + SLASH + url;
        }
        return server + url;
    }

    private Map<String, Object> buildRequestParam(CexRequest req) {
        Map<String, Object> param = Maps.newHashMap();
        param.put(CexConstant.PARAM_LANG, CexConstant.PARAM_LANG_VALUE);

        param.put(CexConstant.PARAM_DATA, req.getData());
        return param;
    }

    private HttpEntity<Map> buildHttpEntity(CexRequest req) {
        HttpHeaders headers = new HttpHeaders();
        /*if (StringUtils.isNotEmpty(req.getCexToken())) {
            headers.add(CexConstant.HEADER_PARAM_CEX_TOKEN, req.getCexToken());
        }*/
        if (StringUtils.isNotEmpty(req.getCexPassport())) {
            headers.add(CexConstant.HEADER_PARAM_CEX_PASSPORT, req.getCexPassport());
        }
        if (StringUtils.isNotEmpty(req.getDeviceId())) {
            headers.add(CexConstant.HEADER_PARAM_DEVICEID, req.getDeviceId());
        }
        if (StringUtils.isNotEmpty(req.getDeviceSource())) {
            headers.add(CexConstant.HEADER_PARAM_DEVICESOURCE, req.getDeviceSource());
        }
        if (StringUtils.isNotEmpty(req.getSign())) {
            headers.add(CexConstant.HEADER_PARAM_CEX_SIGNATURE, req.getSign());
        }
        if (StringUtils.isNotEmpty(req.getOutOrderNo())) {
            headers.add(CexConstant.HEADER_PARAM_CEX_BIZ_CHANNEL_OUT_ORDER_NO, req.getOutOrderNo());
        }
        headers.add(CexConstant.HEADER_PARAM_CEX_BIZ_CHANNEL, CexConstant.HEADER_PARAM_CEX_BIZ_CHANNEL_VALUE);
        headers.add(CexConstant.HEADER_PARAM_CONTENT_TYPE, CexConstant.HEADER_PARAM_CONTENT_TYPE_VALUE);

        return new HttpEntity<Map>(buildRequestParam(req), headers);
    }
}
