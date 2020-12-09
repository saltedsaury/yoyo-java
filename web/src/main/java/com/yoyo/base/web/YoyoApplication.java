package cn.yoyo.base.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * <p>运行入口</p>
 *
 * @author yehe
 * @version 1.0
 * @since 2018/6/9 21:40
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"cn.idachain.finance.batch"})
@EnableTransactionManagement
@ServletComponentScan
public class YoyoApplication extends WebMvcConfigurerAdapter implements InitializingBean {
    private static final int HTTP_CLIENT_MAX_TOTAL = 200;
    private static final int HTTP_CLIENT_MAX_PER_ROUTE = 100;
    private static final int REQUEST_SOCKET_TIMEOUT = 10000;
    private static final int REQUEST_CONNECT_TIMEOUT = 3000;
    private static final int REQUEST_CONNECTION_REQUEST_TIMEOUT = 1000;

    public static void main(String[] args) {

        log.info("Batch application start.");
        SpringApplication.run(YoyoApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpRequestFactory());
    }
    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }
    @Bean
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(HTTP_CLIENT_MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(HTTP_CLIENT_MAX_PER_ROUTE);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(REQUEST_SOCKET_TIMEOUT)
                .setConnectTimeout(REQUEST_CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(REQUEST_CONNECTION_REQUEST_TIMEOUT)
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }
}
