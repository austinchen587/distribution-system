package com.example.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 数据访问配置类
 * 
 * <p>配置数据访问权限控制相关的组件，包括AOP拦截器的启用。
 * 该配置类是权限控制系统的配置入口。
 * 
 * <p>主要配置：
 * <ul>
 *   <li>启用AspectJ自动代理</li>
 *   <li>配置数据访问拦截器</li>
 *   <li>配置权限检查器</li>
 *   <li>配置审计日志记录器</li>
 * </ul>
 * 
 * <p>使用方式：
 * <ul>
 *   <li>在Spring Boot应用中自动扫描该配置类</li>
 *   <li>或者通过@Import注解手动导入</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@Configuration("commonDataAccessConfig")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CommonDataAccessConfig {
    
    // 这里可以添加其他数据访问相关的Bean配置
    
}
