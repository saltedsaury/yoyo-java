package cn.idachain.finance.batch.common.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.SQLUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>数据源配置</p>
 *
 * @author yehe
 * @version 1.0
 * @since 2018/6/12 20:28
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${connection.classname}")
    private String className;
    @Value("${connection.url}")
    private String connectionUrl;
    @Value("${connection.username}")
    private String username;
    @Value("${connection.password}")
    private String password;
    @Value("${druid.initialSize}")
    private Integer initialSize;
    @Value("${druid.minIdle}")
    private Integer minIdle;
    @Value("${druid.maxActive}")
    private Integer maxActive;
    @Value("${druid.maxWait}")
    private Integer maxWait;
    @Value("${druid.timeBetweenEvictionRunsMillis}")
    private Integer timeBetweenEvictionRunsMillis;
    @Value("${druid.minEvictableIdleTimeMillis}")
    private Integer minEvictableIdleTimeMillis;
    @Value("${druid.validationQuery}")
    private String validationQuery;
    @Value("${druid.testWhileIdle}")
    private Boolean testWhileIdle;
    @Value("${druid.testOnBorrow}")
    private Boolean testOnBorrow;
    @Value("${druid.testOnReturn}")
    private Boolean testOnReturn;
    @Value("${druid.poolPreparedStatements}")
    private Boolean poolPreparedStatements;
    @Value("${druid.maxPoolPreparedStatementPerConnectionSize}")
    private Integer maxPoolPreparedStatementPerConnectionSize;
    @Value("${druid.filters}")
    private String filters;

    @Value("${druid.loginUsername}")
    private String loginUsername;
    @Value("${druid.loginPassword}")
    private String loginPassword;

    //	配置数据源
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource initDataSource() {
        log.info("初始化DruidDataSource");
        DruidDataSource dds = new DruidDataSource();
        dds.setDriverClassName(className);
        dds.setUrl(connectionUrl);
        dds.setUsername(username);
        dds.setPassword(password);
        dds.setInitialSize(initialSize);
        dds.setMinIdle(minIdle);
        dds.setMaxActive(maxActive);
        dds.setMaxWait(maxWait);
        dds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dds.setValidationQuery(validationQuery);
        dds.setTestWhileIdle(testWhileIdle);
        dds.setTestOnBorrow(testOnBorrow);
        dds.setTestOnReturn(testOnReturn);
        dds.setPoolPreparedStatements(poolPreparedStatements);
        dds.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        //log4jdbc sql输出设置
        Slf4jLogFilter logFilter = new Slf4jLogFilter();
        logFilter.setConnectionLogEnabled(false);
        logFilter.setStatementSqlFormatOption(new SQLUtils.FormatOption(true, true));
        logFilter.setStatementLogEnabled(false);
        logFilter.setResultSetLogEnabled(true);
        logFilter.setStatementExecutableSqlLogEnable(true);
        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(logFilter);
        dds.setProxyFilters(filterList);

        try {
            dds.setFilters(filters);
        } catch (Exception e) {
           log.error("dataSource setFilter error",e);
        }
        return dds;
    }

    //配置事务
    @Bean(name = "transactionManager")
    static public DataSourceTransactionManager transactionManager(@Qualifier(value = "dataSource") DruidDataSource dataSource) {
        log.info("初始化DataSourceTransactionManager");
        return new DataSourceTransactionManager(dataSource);
    }

    //配置事务模板
    @Bean(name = "transactionTemplate")
    static public TransactionTemplate transactionTemplate(@Qualifier(value = "transactionManager") DataSourceTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

}
