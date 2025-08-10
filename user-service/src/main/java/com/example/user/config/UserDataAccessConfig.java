package com.example.user.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * User Service 专用数据访问层配置类
 * 
 * @author User Service Team
 * @version 1.0
 * @since 2025-08-07
 */
@Configuration("userDataAccessConfig")
@EnableTransactionManagement
public class UserDataAccessConfig {
    
    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/distribution_system}")
    private String url;
    
    @Value("${spring.datasource.username:root}")
    private String username;
    
    @Value("${spring.datasource.password:password}")
    private String password;
    
    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;
    
    /**
     * 主数据源配置
     * 使用HikariCP连接池，性能优异
     */
    @Bean("userDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        
        // 连接池配置
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        
        // MySQL优化配置
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        // 连接池名称
        config.setPoolName("HikariCP-UserService");
        
        return new HikariDataSource(config);
    }
    
    /**
     * MyBatis SqlSessionFactory配置
     */
    @Bean("userSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        
        // 设置MyBatis配置文件位置 - 使用user-service专用配置
        factoryBean.setConfigLocation(new ClassPathResource("mybatis-config-user.xml"));
        
        // 设置Mapper XML文件位置 - 加载所有XML文件但排除Promotion相关
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        java.util.List<org.springframework.core.io.Resource> resources = new java.util.ArrayList<>();
        // 加载所有mapper XML文件
        org.springframework.core.io.Resource[] allMappers = resolver.getResources("classpath:mapper/*.xml");
        for (org.springframework.core.io.Resource resource : allMappers) {
            String filename = resource.getFilename();
            // 排除Promotion相关的XML文件以避免tags字段类型处理问题
            if (filename != null && !filename.startsWith("Promotion")) {
                resources.add(resource);
            }
        }
        factoryBean.setMapperLocations(resources.toArray(new org.springframework.core.io.Resource[0]));
        
        // 设置类型别名包
        factoryBean.setTypeAliasesPackage("com.example.data.entity");
        
        return factoryBean.getObject();
    }
    
    /**
     * 事务管理器配置
     */
    @Bean("userTransactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    /**
     * Redis连接工厂配置
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }
    
    /**
     * Redis模板配置
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用String序列化器作为key和hash key的序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // 使用JSON序列化器作为value和hash value的序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
}