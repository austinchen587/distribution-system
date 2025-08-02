package com.example.common.service;

import com.example.common.dto.DataAccessContext;
import com.example.common.entity.DataOperationLog;
import com.example.common.enums.OperationStatus;
import com.example.common.mapper.DataOperationLogMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 数据操作日志记录器
 * 
 * <p>负责记录所有数据操作的审计日志，是权限控制系统的重要组成部分。
 * 该组件提供了完整的数据操作审计功能，支持操作成功、失败、拒绝等多种状态的记录。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>记录数据操作的完整审计信息</li>
 *   <li>支持操作成功、失败、拒绝等状态记录</li>
 *   <li>支持操作前后数据变化记录</li>
 *   <li>提供完整的操作上下文记录</li>
 * </ul>
 * 
 * <p>日志记录内容：
 * <ul>
 *   <li>基本信息：服务名、表名、操作类型、用户ID</li>
 *   <li>执行信息：执行时间、影响行数、操作状态</li>
 *   <li>上下文信息：IP地址、User-Agent、请求ID</li>
 *   <li>数据变化：操作前后的数据状态（可选）</li>
 * </ul>
 * 
 * <p>使用场景：
 * <ul>
 *   <li>数据访问拦截器记录操作日志</li>
 *   <li>安全审计和合规检查</li>
 *   <li>系统性能监控和分析</li>
 *   <li>数据变更历史追踪</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@Component
public class DataOperationLogger {

    private static final Logger logger = LoggerFactory.getLogger(DataOperationLogger.class);
    
    private static final int MAX_SQL_LENGTH = 1000;
    private static final int MAX_ERROR_MESSAGE_LENGTH = 500;
    private static final int MAX_DATA_LENGTH = 2000;
    
    @Autowired
    private DataOperationLogMapper logMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 记录操作成功的日志
     * 
     * @param context 数据访问上下文
     * @param result 操作结果
     */
    public void logSuccess(DataAccessContext context, Object result) {
        try {
            DataOperationLog log = buildBaseLog(context);
            log.setStatus(OperationStatus.SUCCESS.getCode());
            
            // 设置影响行数
            if (result instanceof Number) {
                log.setAffectedRows(((Number) result).intValue());
            }
            
            // 记录操作后数据（对于INSERT/UPDATE操作）
            if (context.isModifyOperation() && result != null) {
                String afterData = truncateString(serializeObject(result), MAX_DATA_LENGTH);
                log.setAfterData(afterData);
            }
            
            insertLog(log);

            logger.debug("记录操作成功日志: {}", context.getOperationSummary());

        } catch (Exception e) {
            logger.error("记录操作成功日志失败: {}", context.getOperationSummary(), e);
        }
    }
    
    /**
     * 记录操作失败的日志
     * 
     * @param context 数据访问上下文
     * @param exception 异常信息
     */
    public void logFailure(DataAccessContext context, Exception exception) {
        try {
            DataOperationLog log = buildBaseLog(context);
            log.setStatus(OperationStatus.FAILED.getCode());
            
            // 设置错误信息
            if (exception != null) {
                String errorMessage = truncateString(exception.getMessage(), MAX_ERROR_MESSAGE_LENGTH);
                log.setErrorMessage(errorMessage);
            }
            
            insertLog(log);

            logger.debug("记录操作失败日志: {}", context.getOperationSummary());

        } catch (Exception e) {
            logger.error("记录操作失败日志失败: {}", context.getOperationSummary(), e);
        }
    }
    
    /**
     * 记录操作被拒绝的日志
     * 
     * @param context 数据访问上下文
     * @param reason 拒绝原因
     */
    public void logDenied(DataAccessContext context, String reason) {
        try {
            DataOperationLog log = buildBaseLog(context);
            log.setStatus(OperationStatus.DENIED.getCode());
            
            // 设置拒绝原因
            if (StringUtils.hasText(reason)) {
                String errorMessage = truncateString(reason, MAX_ERROR_MESSAGE_LENGTH);
                log.setErrorMessage(errorMessage);
            }
            
            insertLog(log);

            logger.debug("记录操作拒绝日志: {}", context.getOperationSummary());

        } catch (Exception e) {
            logger.error("记录操作拒绝日志失败: {}", context.getOperationSummary(), e);
        }
    }
    
    /**
     * 记录带数据变化的操作日志
     * 
     * @param context 数据访问上下文
     * @param beforeData 操作前数据
     * @param afterData 操作后数据
     * @param result 操作结果
     */
    public void logWithDataChange(DataAccessContext context, Object beforeData, Object afterData, Object result) {
        try {
            DataOperationLog log = buildBaseLog(context);
            log.setStatus(OperationStatus.SUCCESS.getCode());
            
            // 设置影响行数
            if (result instanceof Number) {
                log.setAffectedRows(((Number) result).intValue());
            }
            
            // 记录操作前数据
            if (beforeData != null) {
                String beforeDataStr = truncateString(serializeObject(beforeData), MAX_DATA_LENGTH);
                log.setBeforeData(beforeDataStr);
            }

            // 记录操作后数据
            if (afterData != null) {
                String afterDataStr = truncateString(serializeObject(afterData), MAX_DATA_LENGTH);
                log.setAfterData(afterDataStr);
            }
            
            insertLog(log);

            logger.debug("记录数据变化日志: {}", context.getOperationSummary());

        } catch (Exception e) {
            logger.error("记录数据变化日志失败: {}", context.getOperationSummary(), e);
        }
    }
    
    /**
     * 记录SQL执行日志
     * 
     * @param context 数据访问上下文
     * @param sqlStatement SQL语句
     * @param result 执行结果
     */
    public void logSqlExecution(DataAccessContext context, String sqlStatement, Object result) {
        try {
            DataOperationLog log = buildBaseLog(context);
            log.setStatus(OperationStatus.SUCCESS.getCode());
            
            // 设置SQL语句
            if (StringUtils.hasText(sqlStatement)) {
                log.setSqlStatement(truncateString(sqlStatement, MAX_SQL_LENGTH));
            }
            
            // 设置影响行数
            if (result instanceof Number) {
                log.setAffectedRows(((Number) result).intValue());
            }
            
            insertLog(log);

            logger.debug("记录SQL执行日志: {}", context.getOperationSummary());

        } catch (Exception e) {
            logger.error("记录SQL执行日志失败: {}", context.getOperationSummary(), e);
        }
    }
    
    /**
     * 构建基础日志对象
     * 
     * @param context 数据访问上下文
     * @return 基础日志对象
     */
    private DataOperationLog buildBaseLog(DataAccessContext context) {
        DataOperationLog log = DataOperationLog.builder()
            .requestId(context.getRequestId())
            .serviceName(context.getServiceName())
            .tableName(context.getTableName())
            .operationType(context.getOperationType())
            .userId(context.getUserId())
            .executionTime(context.getExecutionTime())
            .ipAddress(context.getIpAddress())
            .userAgent(context.getUserAgent())
            .createdAt(LocalDateTime.now())
            .build();
        
        return log;
    }
    
    /**
     * 插入日志记录
     *
     * @param operationLog 日志对象
     */
    private void insertLog(DataOperationLog operationLog) {
        try {
            logMapper.insert(operationLog);
        } catch (Exception e) {
            logger.error("插入审计日志失败", e);
            // 不抛出异常，避免影响业务操作
        }
    }
    
    /**
     * 序列化对象为JSON字符串
     * 
     * @param object 要序列化的对象
     * @return JSON字符串
     */
    private String serializeObject(Object object) {
        if (object == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.warn("序列化对象失败: {}", object.getClass().getSimpleName(), e);
            return object.toString();
        }
    }
    

    
    /**
     * 截断字符串到指定长度
     * 
     * @param str 原始字符串
     * @param maxLength 最大长度
     * @return 截断后的字符串
     */
    private String truncateString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        
        return str.substring(0, maxLength - 3) + "...";
    }
}
