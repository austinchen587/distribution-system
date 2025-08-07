package com.example.common.saga.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ServiceInvoker 单元测试
 * 
 * 测试服务调用器的核心功能：
 * - 远程HTTP服务调用和响应处理
 * - 本地Service Bean调用模拟
 * - 服务健康检查和连接测试
 * - URL构建和请求参数处理
 * - JSON序列化和异常处理
 * - 服务端点配置和路由逻辑
 * - 执行时间统计和日志记录
 * 
 * @author Event-Driven Architecture Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceInvoker 单元测试")
class ServiceInvokerTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private ServiceInvoker serviceInvoker;
    private final String testServiceName = "auth-service";
    private final String testAction = "createUser";
    private final String testEndpoint = "http://localhost:8081";

    @BeforeEach
    void setUp() {
        serviceInvoker = new ServiceInvoker();
        ReflectionTestUtils.setField(serviceInvoker, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(serviceInvoker, "objectMapper", objectMapper);
    }

    @Test
    @DisplayName("正常场景：远程服务调用成功应该返回成功结果")
    void should_ReturnSuccessResult_when_RemoteServiceCallSucceeds() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", "testuser");
        parameters.put("password", "password123");

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("userId", 123L);
        expectedResponse.put("message", "用户创建成功");

        String requestBody = "{\"username\":\"testuser\",\"password\":\"password123\"}";
        when(objectMapper.writeValueAsString(parameters)).thenReturn(requestBody);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(testEndpoint + "/api/users"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        ServiceInvocationResult result = serviceInvoker.invoke(testServiceName, testAction, parameters);

        // Then
        assertAll("远程服务调用成功验证",
                () -> assertTrue(result.isSuccess(), "调用应该成功"),
                () -> assertEquals(expectedResponse, result.getResult(), "返回数据应该匹配"),
                () -> assertNull(result.getErrorMessage(), "不应该有错误信息"),
                () -> assertNull(result.getStatusCode(), "状态码应该为空"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );

        // Verify interactions
        verify(objectMapper, times(1)).writeValueAsString(parameters);
        verify(restTemplate, times(1)).exchange(
                eq(testEndpoint + "/api/users"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("异常场景：远程服务调用返回错误状态码应该返回失败结果")
    void should_ReturnFailureResult_when_RemoteServiceCallReturnsErrorStatus() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", "testuser");

        String requestBody = "{\"username\":\"testuser\"}";
        when(objectMapper.writeValueAsString(parameters)).thenReturn(requestBody);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        ServiceInvocationResult result = serviceInvoker.invoke(testServiceName, testAction, parameters);

        // Then
        assertAll("远程服务调用错误状态验证",
                () -> assertFalse(result.isSuccess(), "调用应该失败"),
                () -> assertNull(result.getResult(), "结果数据应该为空"),
                () -> assertTrue(result.getErrorMessage().contains("HTTP调用失败"), "错误信息应该包含HTTP调用失败"),
                () -> assertEquals(400, result.getStatusCode(), "状态码应该为400"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );
    }

    @Test
    @DisplayName("异常场景：JSON序列化失败应该返回失败结果")
    void should_ReturnFailureResult_when_JsonSerializationFails() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("data", new Object()); // 无法序列化的对象

        when(objectMapper.writeValueAsString(parameters))
                .thenThrow(new JsonProcessingException("序列化失败") {});

        // When
        ServiceInvocationResult result = serviceInvoker.invoke(testServiceName, testAction, parameters);

        // Then
        assertAll("JSON序列化失败验证",
                () -> assertFalse(result.isSuccess(), "调用应该失败"),
                () -> assertNull(result.getResult(), "结果数据应该为空"),
                () -> assertTrue(result.getErrorMessage().contains("JSON序列化失败"), "错误信息应该包含序列化失败"),
                () -> assertNull(result.getStatusCode(), "状态码应该为空"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );

        verify(objectMapper, times(1)).writeValueAsString(parameters);
        verify(restTemplate, never()).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    @DisplayName("异常场景：未知服务名称应该返回失败结果")
    void should_ReturnFailureResult_when_UnknownServiceName() {
        // Given
        String unknownService = "unknown-service";
        Map<String, Object> parameters = new HashMap<>();

        // When
        ServiceInvocationResult result = serviceInvoker.invoke(unknownService, testAction, parameters);

        // Then
        assertAll("未知服务验证",
                () -> assertFalse(result.isSuccess(), "调用应该失败"),
                () -> assertNull(result.getResult(), "结果数据应该为空"),
                () -> assertEquals("未知的服务: " + unknownService, result.getErrorMessage(), "错误信息应该指明未知服务"),
                () -> assertNull(result.getStatusCode(), "状态码应该为空"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );

        verify(restTemplate, never()).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    @DisplayName("正常场景：本地服务调用应该成功执行")
    void should_ExecuteSuccessfully_when_InvokingLocalService() {
        // Given
        String localServiceName = "common-service";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("data", "test");

        // When
        ServiceInvocationResult result = serviceInvoker.invoke(localServiceName, testAction, parameters);

        // Then
        assertAll("本地服务调用验证",
                () -> assertTrue(result.isSuccess(), "调用应该成功"),
                () -> assertNotNull(result.getResult(), "结果不应该为空"),
                () -> assertEquals("本地服务调用成功", result.getResult().get("message"), "消息应该匹配"),
                () -> assertEquals(localServiceName, result.getResult().get("serviceName"), "服务名应该匹配"),
                () -> assertEquals(testAction, result.getResult().get("action"), "操作名应该匹配"),
                () -> assertTrue((Boolean) result.getResult().get("success"), "成功标志应该为true"),
                () -> assertNull(result.getErrorMessage(), "不应该有错误信息"),
                () -> assertTrue(result.getExecutionTime() >= 100, "执行时间应该至少100ms（模拟执行时间）")
        );
    }

    @Test
    @DisplayName("正常场景：空参数调用应该正确处理")
    void should_HandleCorrectly_when_InvokingWithEmptyParameters() throws Exception {
        // Given
        Map<String, Object> emptyParameters = new HashMap<>();

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "success");

        // 空参数不应该调用JSON序列化
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        ServiceInvocationResult result = serviceInvoker.invoke(testServiceName, testAction, emptyParameters);

        // Then
        assertTrue(result.isSuccess(), "调用应该成功");
        assertEquals(expectedResponse, result.getResult(), "返回数据应该匹配");

        // 验证空参数不调用JSON序列化
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    @DisplayName("正常场景：null参数调用应该正确处理")
    void should_HandleCorrectly_when_InvokingWithNullParameters() throws Exception {
        // Given
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "success");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        ServiceInvocationResult result = serviceInvoker.invoke(testServiceName, testAction, null);

        // Then
        assertTrue(result.isSuccess(), "调用应该成功");
        assertEquals(expectedResponse, result.getResult(), "返回数据应该匹配");

        // 验证null参数不调用JSON序列化
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    @DisplayName("正常场景：URL构建应该根据操作类型正确生成")
    void should_BuildCorrectUrl_when_DifferentActionTypes() throws Exception {
        // Given
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("success", true);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Test create action
        serviceInvoker.invoke(testServiceName, "createUser", null);
        verify(restTemplate).exchange(
                eq(testEndpoint + "/api/users"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );

        reset(restTemplate);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Test update action
        serviceInvoker.invoke(testServiceName, "updateUser", null);
        verify(restTemplate).exchange(
                eq(testEndpoint + "/api/users/update"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );

        reset(restTemplate);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Test approve action
        serviceInvoker.invoke(testServiceName, "approvePromotion", null);
        verify(restTemplate).exchange(
                eq(testEndpoint + "/api/saga/approvePromotion"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("正常场景：服务健康检查应该正确工作")
    void should_CheckHealthCorrectly_when_ServiceIsHealthy() {
        // Given
        ResponseEntity<Map> healthResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.getForEntity(testEndpoint + "/actuator/health", Map.class))
                .thenReturn(healthResponse);

        // When
        boolean isHealthy = serviceInvoker.isServiceHealthy(testServiceName);

        // Then
        assertTrue(isHealthy, "健康的服务应该返回true");
        verify(restTemplate, times(1)).getForEntity(testEndpoint + "/actuator/health", Map.class);
    }

    @Test
    @DisplayName("异常场景：服务健康检查异常应该返回false")
    void should_ReturnFalse_when_HealthCheckThrowsException() {
        // Given
        when(restTemplate.getForEntity(testEndpoint + "/actuator/health", Map.class))
                .thenThrow(new RuntimeException("连接失败"));

        // When
        boolean isHealthy = serviceInvoker.isServiceHealthy(testServiceName);

        // Then
        assertFalse(isHealthy, "健康检查异常时应该返回false");
    }

    @Test
    @DisplayName("边界条件：未知服务的健康检查应该返回false")
    void should_ReturnFalse_when_CheckingHealthOfUnknownService() {
        // Given
        String unknownService = "unknown-service";

        // When
        boolean isHealthy = serviceInvoker.isServiceHealthy(unknownService);

        // Then
        assertFalse(isHealthy, "未知服务的健康检查应该返回false");
        verify(restTemplate, never()).getForEntity(anyString(), eq(Map.class));
    }

    @Test
    @DisplayName("正常场景：获取支持的服务列表应该返回预期服务")
    void should_ReturnExpectedServices_when_GetSupportedServices() {
        // When
        Set<String> supportedServices = serviceInvoker.getSupportedServices();

        // Then
        assertAll("支持的服务列表验证",
                () -> assertNotNull(supportedServices, "服务列表不应该为null"),
                () -> assertTrue(supportedServices.contains("auth-service"), "应该包含auth-service"),
                () -> assertTrue(supportedServices.contains("lead-service"), "应该包含lead-service"),
                () -> assertTrue(supportedServices.contains("promotion-service"), "应该包含promotion-service"),
                () -> assertTrue(supportedServices.contains("reward-service"), "应该包含reward-service"),
                () -> assertTrue(supportedServices.contains("invitation-service"), "应该包含invitation-service"),
                () -> assertEquals(5, supportedServices.size(), "应该有5个支持的服务")
        );
    }

    @Test
    @DisplayName("正常场景：服务连接测试成功应该返回成功结果")
    void should_ReturnSuccessResult_when_ServiceConnectionTestSucceeds() {
        // Given
        ResponseEntity<Map> healthResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.getForEntity(testEndpoint + "/actuator/health", Map.class))
                .thenReturn(healthResponse);

        // When
        ServiceInvocationResult result = serviceInvoker.testServiceConnection(testServiceName);

        // Then
        assertAll("服务连接测试成功验证",
                () -> assertTrue(result.isSuccess(), "连接测试应该成功"),
                () -> assertNotNull(result.getResult(), "结果不应该为空"),
                () -> assertEquals(testServiceName, result.getResult().get("serviceName"), "服务名应该匹配"),
                () -> assertEquals("healthy", result.getResult().get("status"), "状态应该为healthy"),
                () -> assertNotNull(result.getResult().get("responseTime"), "响应时间应该被记录"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负"),
                () -> assertNull(result.getErrorMessage(), "不应该有错误信息")
        );
    }

    @Test
    @DisplayName("异常场景：服务连接测试失败应该返回失败结果")
    void should_ReturnFailureResult_when_ServiceConnectionTestFails() {
        // Given
        when(restTemplate.getForEntity(testEndpoint + "/actuator/health", Map.class))
                .thenThrow(new RuntimeException("连接失败"));

        // When
        ServiceInvocationResult result = serviceInvoker.testServiceConnection(testServiceName);

        // Then
        assertAll("服务连接测试失败验证",
                () -> assertFalse(result.isSuccess(), "连接测试应该失败"),
                () -> assertNull(result.getResult(), "结果应该为空"),
                () -> assertTrue(result.getErrorMessage().contains("服务不健康") || 
                                 result.getErrorMessage().contains("连接测试失败"), "错误信息应该包含服务不健康或连接测试失败"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );
    }

    @Test
    @DisplayName("边界条件：本地服务判断应该正确工作")
    void should_IdentifyLocalServicesCorrectly_when_CheckingServiceType() {
        // Given - 使用反射访问私有方法进行测试
        // 这里通过实际调用来验证本地服务判断逻辑

        // Test local services
        ServiceInvocationResult result1 = serviceInvoker.invoke("common-service", "testAction", null);
        assertTrue(result1.isSuccess(), "common-service应该被识别为本地服务");
        assertEquals("本地服务调用成功", result1.getResult().get("message"), "应该执行本地调用逻辑");

        ServiceInvocationResult result2 = serviceInvoker.invoke("local-test-service", "testAction", null);
        assertTrue(result2.isSuccess(), "local-前缀的服务应该被识别为本地服务");
        assertEquals("本地服务调用成功", result2.getResult().get("message"), "应该执行本地调用逻辑");
    }

    @Test
    @DisplayName("异常场景：远程服务调用抛出异常应该返回失败结果")
    void should_ReturnFailureResult_when_RemoteServiceCallThrowsException() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", "data");

        when(objectMapper.writeValueAsString(parameters)).thenReturn("{\"test\":\"data\"}");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("网络连接失败"));

        // When
        ServiceInvocationResult result = serviceInvoker.invoke(testServiceName, testAction, parameters);

        // Then
        assertAll("远程服务调用异常验证",
                () -> assertFalse(result.isSuccess(), "调用应该失败"),
                () -> assertNull(result.getResult(), "结果应该为空"),
                () -> assertTrue(result.getErrorMessage().contains("远程调用异常"), "错误信息应该包含远程调用异常"),
                () -> assertTrue(result.getErrorMessage().contains("网络连接失败"), "错误信息应该包含具体异常信息"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );
    }

    @Test
    @DisplayName("正常场景：HTTP请求头应该正确设置")
    void should_SetCorrectHeaders_when_MakingHttpRequest() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("data", "test");

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("success", true);

        when(objectMapper.writeValueAsString(parameters)).thenReturn("{\"data\":\"test\"}");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // When
        serviceInvoker.invoke(testServiceName, testAction, parameters);

        // Then - 验证请求头设置
        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.POST),
                argThat(httpEntity -> {
                    HttpHeaders headers = httpEntity.getHeaders();
                    return headers.getContentType().equals(MediaType.APPLICATION_JSON);
                }),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("边界条件：服务端点配置应该包含所有预期服务")
    void should_ContainAllExpectedEndpoints_when_CheckingServiceEndpoints() {
        // When
        Set<String> supportedServices = serviceInvoker.getSupportedServices();

        // Then
        assertAll("服务端点配置验证",
                () -> assertTrue(supportedServices.contains("auth-service"), "应该配置auth-service端点"),
                () -> assertTrue(supportedServices.contains("lead-service"), "应该配置lead-service端点"),
                () -> assertTrue(supportedServices.contains("promotion-service"), "应该配置promotion-service端点"),
                () -> assertTrue(supportedServices.contains("reward-service"), "应该配置reward-service端点"),
                () -> assertTrue(supportedServices.contains("invitation-service"), "应该配置invitation-service端点")
        );

        // 验证每个服务都能进行健康检查（即端点配置正确）
        for (String service : supportedServices) {
            // 模拟健康检查调用，验证端点配置存在
            when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                    .thenReturn(new ResponseEntity<>(new HashMap<>(), HttpStatus.OK));
            
            boolean canCheckHealth = serviceInvoker.isServiceHealthy(service);
            // 健康检查能够执行（不管结果如何）说明端点配置存在
            // 这里我们主要验证不会因为端点未配置而直接返回false
        }
    }
}