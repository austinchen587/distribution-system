package com.example.common.annotation;

import com.example.common.enums.UserRole;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解
 * 用于标记需要特定角色才能访问的方法或类
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /**
     * 允许访问的角色列表
     * 默认需要超级管理员权限
     */
    UserRole[] value() default {UserRole.SUPER_ADMIN};
    
    /**
     * 是否需要所有角色都满足（AND逻辑）
     * 默认为false，表示只需要满足其中一个角色即可（OR逻辑）
     */
    boolean requireAll() default false;
}