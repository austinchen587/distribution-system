package com.example.common.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Swagger常用注解组合
 * 提供预定义的注解组合，简化API文档编写
 */
public class SwaggerAnnotations {
    
    /**
     * 通用的成功响应注解
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "操作成功",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public @interface CommonApiResponses {
    }
    
    /**
     * 分页查询的参数注解
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Parameter(name = "page", description = "页码，从1开始", in = ParameterIn.QUERY, 
            schema = @Schema(type = "integer", defaultValue = "1"))
    @Parameter(name = "size", description = "每页大小", in = ParameterIn.QUERY,
            schema = @Schema(type = "integer", defaultValue = "10"))
    @Parameter(name = "sort", description = "排序字段", in = ParameterIn.QUERY,
            schema = @Schema(type = "string"))
    @Parameter(name = "order", description = "排序方向（asc/desc）", in = ParameterIn.QUERY,
            schema = @Schema(type = "string", defaultValue = "desc"))
    public @interface PageableParameters {
    }
}