package com.example.user.config;

import com.example.data.mapper.UserMapper;
import com.example.data.mapper.CustomerLeadMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User Service 专用 Mapper Bean 配置
 * 手动创建需要的 Mapper Bean，避免自动扫描导致的冲突
 * 
 * @author User Service Team
 * @version 1.0
 * @since 2025-08-07
 */
@Configuration
public class UserSpecificMapperConfig {

    /**
     * 创建 UserMapper Bean
     */
    @Bean
    public MapperFactoryBean<UserMapper> userMapper(@Qualifier("userSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        MapperFactoryBean<UserMapper> factoryBean = new MapperFactoryBean<>(UserMapper.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean;
    }

    /**
     * 创建 CustomerLeadMapper Bean
     */
    @Bean
    public MapperFactoryBean<CustomerLeadMapper> customerLeadMapper(@Qualifier("userSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        MapperFactoryBean<CustomerLeadMapper> factoryBean = new MapperFactoryBean<>(CustomerLeadMapper.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean;
    }
}