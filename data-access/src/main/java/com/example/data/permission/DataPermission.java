package com.example.data.permission;

import java.lang.annotation.*;

/**
 * 数据访问权限控制注解
 * 用于标记需要进行权限检查的数据访问方法
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {
    
    /**
     * 目标数据表名
     */
    String table();
    
    /**
     * 操作类型
     */
    OperationType operation();
    
    /**
     * 是否记录操作日志
     */
    boolean logOperation() default true;
    
    /**
     * 权限描述
     */
    String description() default "";
    
    /**
     * 是否跳过权限检查（用于系统内部调用）
     */
    boolean skipCheck() default false;
}