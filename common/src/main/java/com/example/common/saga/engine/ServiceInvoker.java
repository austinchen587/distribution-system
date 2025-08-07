package com.example.common.saga.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务调用器
 * 
 * <p>负责调用微服务中的具体业务方法，支持HTTP REST调用、消息队列调用等多种方式。
 * 为Saga步骤执行和补偿操作提供统一的服务调用接口。
 * 
 * <p>支持的调用方式：
 * <ul>
 *   <li>HTTP REST API调用</li>
 *   <li>本地Service Bean调用</li>
 *   <li>消息队列异步调用（未来扩展）</li>
 *   <li>RPC调用（未来扩展）</li>
 * </ul>
 * 
 * <p>调用流程：
 * <ol>
 *   <li>解析服务名称和操作名称</li>
 *   <li>选择合适的调用方式</li>
 *   <li>构建调用参数和请求</li>
 *   <li>执行调用并处理响应</li>
 *   <li>返回标准化结果</li>
 * </ol>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class ServiceInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ServiceInvoker.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 服务端点配置映射
     * 实际应该从配置中心或注册中心获取
     */
    private static final Map<String, String> SERVICE_ENDPOINTS = new HashMap<>();
    
    static {
        // 配置各个微服务的端点
        SERVICE_ENDPOINTS.put("auth-service", "http://localhost:8081");
        SERVICE_ENDPOINTS.put("lead-service", "http://localhost:8082");
        SERVICE_ENDPOINTS.put("promotion-service", "http://localhost:8083");
        SERVICE_ENDPOINTS.put("reward-service", "http://localhost:8084");
        SERVICE_ENDPOINTS.put("invitation-service", "http://localhost:8085");
    }

    /**
     * 调用服务方法
     * 
     * @param serviceName 服务名称
     * @param action 操作名称
     * @param parameters 调用参数
     * @return 调用结果
     */
    public ServiceInvocationResult invoke(String serviceName, String action, Map<String, Object> parameters) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("调用服务方法: serviceName={}, action={}, parameters={}", 
                serviceName, action, parameters);

            // 根据服务名称选择调用方式
            if (isLocalService(serviceName)) {
                return invokeLocalService(serviceName, action, parameters, startTime);
            } else {
                return invokeRemoteService(serviceName, action, parameters, startTime);
            }

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("服务调用异常: serviceName={}, action={}", serviceName, action, e);
            return ServiceInvocationResult.failure("服务调用异常: " + e.getMessage(), null, executionTime);
        }
    }

    /**
     * 调用远程服务（HTTP REST）
     * 
     * @param serviceName 服务名称
     * @param action 操作名称
     * @param parameters 调用参数
     * @param startTime 开始时间
     * @return 调用结果
     */
    private ServiceInvocationResult invokeRemoteService(String serviceName, String action, 
                                                       Map<String, Object> parameters, long startTime) {
        try {
            String endpoint = SERVICE_ENDPOINTS.get(serviceName);
            if (endpoint == null) {
                long executionTime = System.currentTimeMillis() - startTime;
                return ServiceInvocationResult.failure("未知的服务: " + serviceName, null, executionTime);
            }

            // 构建请求URL
            String url = buildRequestUrl(endpoint, action);
            
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 构建请求体
            String requestBody = null;
            if (parameters != null && !parameters.isEmpty()) {
                requestBody = objectMapper.writeValueAsString(parameters);
            }
            
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            
            logger.debug("发送HTTP请求: url={}, body={}", url, requestBody);

            // 发送HTTP请求
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, Map.class);
            
            long executionTime = System.currentTimeMillis() - startTime;

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("远程服务调用成功: serviceName={}, action={}, executionTime={}ms", 
                    serviceName, action, executionTime);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = response.getBody();
                return ServiceInvocationResult.success(responseBody, executionTime);
            } else {
                logger.error("远程服务调用失败: serviceName={}, action={}, status={}", 
                    serviceName, action, response.getStatusCode());
                
                return ServiceInvocationResult.failure(
                    "HTTP调用失败: " + response.getStatusCode(), 
                    response.getStatusCode().value(), 
                    executionTime
                );
            }

        } catch (JsonProcessingException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("JSON序列化失败: serviceName={}, action={}", serviceName, action, e);
            return ServiceInvocationResult.failure("JSON序列化失败: " + e.getMessage(), null, executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("远程服务调用异常: serviceName={}, action={}", serviceName, action, e);
            return ServiceInvocationResult.failure("远程调用异常: " + e.getMessage(), null, executionTime);
        }
    }

    /**
     * 调用本地服务（Spring Bean）
     * 
     * @param serviceName 服务名称
     * @param action 操作名称
     * @param parameters 调用参数
     * @param startTime 开始时间
     * @return 调用结果
     */
    private ServiceInvocationResult invokeLocalService(String serviceName, String action, 
                                                      Map<String, Object> parameters, long startTime) {
        try {
            logger.debug("调用本地服务: serviceName={}, action={}", serviceName, action);

            // TODO: 实现本地Spring Bean调用
            // 可以使用ApplicationContext来获取Bean并通过反射调用方法
            // 或者使用SpEL表达式执行
            
            // 模拟本地调用执行
            Thread.sleep(100); // 模拟执行时间
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "本地服务调用成功");
            result.put("serviceName", serviceName);
            result.put("action", action);
            
            logger.info("本地服务调用成功: serviceName={}, action={}, executionTime={}ms", 
                serviceName, action, executionTime);
                
            return ServiceInvocationResult.success(result, executionTime);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("本地服务调用异常: serviceName={}, action={}", serviceName, action, e);
            return ServiceInvocationResult.failure("本地调用异常: " + e.getMessage(), null, executionTime);
        }
    }

    /**
     * 构建请求URL
     * 
     * @param endpoint 服务端点
     * @param action 操作名称
     * @return 完整URL
     */
    private String buildRequestUrl(String endpoint, String action) {
        // 根据操作名称构建RESTful URL
        // 例如: createUser -> POST /api/users
        //      updateUser -> PUT /api/users/{id}
        //      approvePromotion -> POST /api/promotions/{id}/approve
        
        if (action.startsWith("create")) {
            String resource = extractResourceFromAction(action, "create");
            return endpoint + "/api/" + resource;
        } else if (action.startsWith("update")) {
            String resource = extractResourceFromAction(action, "update");
            return endpoint + "/api/" + resource + "/update";
        } else if (action.startsWith("delete")) {
            String resource = extractResourceFromAction(action, "delete");
            return endpoint + "/api/" + resource + "/delete";
        } else if (action.contains("approve") || action.contains("reject")) {
            return endpoint + "/api/saga/" + action;
        } else {
            // 默认使用Saga专用端点
            return endpoint + "/api/saga/" + action;
        }
    }

    /**
     * 从操作名称中提取资源名称
     * 
     * @param action 操作名称
     * @param prefix 前缀
     * @return 资源名称
     */
    private String extractResourceFromAction(String action, String prefix) {
        String resource = action.substring(prefix.length());
        // 转换为复数形式和小写
        return resource.toLowerCase() + "s";
    }

    /**
     * 判断是否为本地服务
     * 
     * @param serviceName 服务名称
     * @return 是否为本地服务
     */
    private boolean isLocalService(String serviceName) {
        // 可以根据配置或命名规则判断
        // 例如：当前服务名称匹配时为本地服务
        return serviceName.equals("common-service") || serviceName.startsWith("local-");
    }

    /**
     * 检查服务健康状态
     * 
     * @param serviceName 服务名称
     * @return 是否健康
     */
    public boolean isServiceHealthy(String serviceName) {
        try {
            String endpoint = SERVICE_ENDPOINTS.get(serviceName);
            if (endpoint == null) {
                return false;
            }

            String healthUrl = endpoint + "/actuator/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn("服务健康检查失败: serviceName={}", serviceName, e);
            return false;
        }
    }

    /**
     * 获取支持的服务列表
     * 
     * @return 服务名称列表
     */
    public java.util.Set<String> getSupportedServices() {
        return SERVICE_ENDPOINTS.keySet();
    }

    /**
     * 测试服务连接
     * 
     * @param serviceName 服务名称
     * @return 测试结果
     */
    public ServiceInvocationResult testServiceConnection(String serviceName) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (isServiceHealthy(serviceName)) {
                long executionTime = System.currentTimeMillis() - startTime;
                Map<String, Object> result = new HashMap<>();
                result.put("serviceName", serviceName);
                result.put("status", "healthy");
                result.put("responseTime", executionTime);
                
                return ServiceInvocationResult.success(result, executionTime);
            } else {
                long executionTime = System.currentTimeMillis() - startTime;
                return ServiceInvocationResult.failure("服务不健康: " + serviceName, 500, executionTime);
            }
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return ServiceInvocationResult.failure("连接测试失败: " + e.getMessage(), null, executionTime);
        }
    }
}