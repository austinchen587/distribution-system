package com.example.common.saga.engine;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 服务调用结果
 * 
 * <p>封装远程服务调用的执行结果，包括成功状态、返回数据和错误信息。
 * 用于Saga步骤执行和补偿操作的结果传递。
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class ServiceInvocationResult {

    /**
     * 调用是否成功
     */
    private boolean success;

    /**
     * 返回数据
     */
    private Map<String, Object> result;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * HTTP状态码（如果是HTTP调用）
     */
    private Integer statusCode;

    /**
     * 执行时长（毫秒）
     */
    private Long executionTime;

    /**
     * 创建成功结果
     * 
     * @param result 返回数据
     * @return 服务调用结果
     */
    public static ServiceInvocationResult success(Map<String, Object> result) {
        return ServiceInvocationResult.builder()
                .success(true)
                .result(result)
                .build();
    }

    /**
     * 创建成功结果（带执行时间）
     * 
     * @param result 返回数据
     * @param executionTime 执行时长
     * @return 服务调用结果
     */
    public static ServiceInvocationResult success(Map<String, Object> result, Long executionTime) {
        return ServiceInvocationResult.builder()
                .success(true)
                .result(result)
                .executionTime(executionTime)
                .build();
    }

    /**
     * 创建失败结果
     * 
     * @param errorMessage 错误信息
     * @return 服务调用结果
     */
    public static ServiceInvocationResult failure(String errorMessage) {
        return ServiceInvocationResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * 创建失败结果（带状态码）
     * 
     * @param errorMessage 错误信息
     * @param statusCode HTTP状态码
     * @return 服务调用结果
     */
    public static ServiceInvocationResult failure(String errorMessage, Integer statusCode) {
        return ServiceInvocationResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .statusCode(statusCode)
                .build();
    }

    /**
     * 创建失败结果（完整信息）
     * 
     * @param errorMessage 错误信息
     * @param statusCode HTTP状态码
     * @param executionTime 执行时长
     * @return 服务调用结果
     */
    public static ServiceInvocationResult failure(String errorMessage, Integer statusCode, Long executionTime) {
        return ServiceInvocationResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .statusCode(statusCode)
                .executionTime(executionTime)
                .build();
    }
}