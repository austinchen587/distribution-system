package com.example.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限注解
 * 用于标记需要特定权限才能访问的方法或类
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * 权限标识符
     * 例如："user:create", "product:delete", "order:view"
     */
    String[] value();
    
    /**
     * 是否需要所有权限都满足（AND逻辑）
     * 默认为false，表示只需要满足其中一个权限即可（OR逻辑）
     */
    boolean requireAll() default false;
}