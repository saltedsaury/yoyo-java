package cn.idachain.finance.batch.common.config;

import cn.idachain.finance.batch.common.util.BlankUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>mybatis配置</p>
 *
 * @author yehe
 * @version 1.0
 * @since 2018/6/12 20:28
 */
@Slf4j
@Configuration
@MapperScan(basePackages={"cn.idachain.finance.batch.common.mapper"})
public class MybatisConfig {

    //    mybatisPlus全局配置
    @Bean(name = "globalConfig")
    static public GlobalConfiguration globalConfig(
            @Value("${mybatis-plus.global-config.id-type}") Integer idType, //主键类型  0:"数据库ID自增", 1:"用户输入ID",2:"全局唯一ID (数字类型唯一ID)", 3:"全局唯一ID UUID";
            @Value("${mybatis-plus.global-config.field-strategy}") Integer fieldStrategy, //字段策略 0:"忽略判断",1:"非 NULL 判断"),2:"非空判断"
            @Value("${mybatis-plus.global-config.db-column-underline}") Boolean dbColumnUnderline, //驼峰下划线转换
            @Value("${mybatis-plus.global-config.refresh-mapper}") Boolean isRefresh, //刷新mapper 调试神器
            //@Value("${mybatis-plus.global-config.capital-mode}") Boolean isCapitalMode, //数据库大写模式 --不要开启
            @Value("${mybatis-plus.global-config.logic-delete-value}") String logicDeleteValue, //逻辑删除配置
            @Value("${mybatis-plus.global-config.logic-not-delete-value}") String logicNotDeleteValue //逻辑删除配置
    ) {
        log.info("初始化GlobalConfiguration");
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        if (!BlankUtil.isBlank(idType)) {
            //主键类型
            globalConfig.setIdType(idType);
        }
        if (!BlankUtil.isBlank(fieldStrategy)) {
            //字段策略默认为1 非null判断
            globalConfig.setFieldStrategy(fieldStrategy);
        }
        if (!BlankUtil.isBlank(dbColumnUnderline)) {
            //驼峰下划线转换
            globalConfig.setDbColumnUnderline(dbColumnUnderline);
        }
        if (!BlankUtil.isBlank(isRefresh)) {
            //刷新mapper 调试神器
            globalConfig.setRefresh(isRefresh);
        }
        //if (!BlankUtil.isBlank(isCapitalMode)) {
        //    //数据库大写模式 默认关闭
        //    //globalConfig.setCapitalMode(isCapitalMode);
        //}
        if (!BlankUtil.isBlank(logicDeleteValue)) {
            //		globalConfig.setLogicDeleteValue(logicDeleteValue);  //逻辑删除配置
        }
        if (!BlankUtil.isBlank(logicNotDeleteValue)) {
            //		globalConfig.setLogicNotDeleteValue(logicNotDeleteValue);  //逻辑删除配置
        }
        return globalConfig;
    }

    @Bean(name = "sqlSessionFactory")
    static public SqlSessionFactory sqlSessionFactory(@Qualifier(value = "globalConfig") GlobalConfiguration globalConfig,
                                                      @Qualifier(value = "dataSource") DruidDataSource dataSource) throws Exception {
        log.info("初始化SqlSessionFactory");
        String mapperLocations = "classpath:/META-INF/sqlmap/*.xml";
        String typeAliasesPackage = "cn.idachain.finance.batch.common.dataobject.**";
        String configLocation = "classpath:/META-INF/mybatis/mybatis-sqlconfig.xml";
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置分页插件
        interceptors.add((Interceptor) paginationInterceptor);

        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        //设置数据源
        sqlSessionFactory.setDataSource(dataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        //自动扫描Mapping.xml文件
        sqlSessionFactory.setMapperLocations(resolver.getResources(mapperLocations));
        sqlSessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        sqlSessionFactory.setConfigLocation(resolver.getResource(configLocation));
        sqlSessionFactory.setGlobalConfig(globalConfig);
        sqlSessionFactory.setPlugins(interceptors.toArray(new Interceptor[0]));

        return sqlSessionFactory.getObject();
    }



}