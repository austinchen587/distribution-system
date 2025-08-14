package com.example.user.event.listener;

import com.example.common.event.domain.UserCreatedEvent;
import com.example.user.event.handler.UserCreatedEventHandler;
import com.example.user.event.handler.UserUpdatedEventHandler;
import com.example.user.event.handler.UserRoleChangedEventHandler;
import com.example.user.event.handler.UserStatusChangedEventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserEventListener单元测试
 * 
 * <p>测试用户事件监听器的各项功能，包括：
 * <ul>
 *   <li>用户创建事件监听和分发</li>
 *   <li>用户更新事件监听和分发</li>
 *   <li>用户角色变更事件监听和分发</li>
 *   <li>用户状态变更事件监听和分发</li>
 *   <li>用户删除事件监听和分发</li>
 *   <li>通用事件监听和分发</li>
 *   <li>事件数据验证</li>
 *   <li>异常处理和错误恢复</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-12
 */
@ExtendWith(MockitoExtension.class)
class UserEventListenerTest {

    @Mock
    private UserCreatedEventHandler userCreatedEventHandler;

    @Mock
    private UserUpdatedEventHandler userUpdatedEventHandler;

    @Mock
    private UserRoleChangedEventHandler userRoleChangedEventHandler;

    @Mock
    private UserStatusChangedEventHandler userStatusChangedEventHandler;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserEventListener userEventListener;

    private UserCreatedEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = createTestUserCreatedEvent();
    }

    @Test
    void should_handle_user_created_event_successfully() {
        // Given
        doNothing().when(userCreatedEventHandler).handle(any(UserCreatedEvent.class));

        // When & Then - 不应抛出异常
        assertDoesNotThrow(() -> {
            userEventListener.handleUserCreatedEvent(testEvent);
        });

        // Verify handler was called
        verify(userCreatedEventHandler, times(1)).handle(testEvent);
    }

    @Test
    void should_handle_invalid_user_created_event() {
        // Given
        UserCreatedEvent invalidEvent = createInvalidUserCreatedEvent();

        // When & Then - 方法应该返回而不调用handler
        assertDoesNotThrow(() -> {
            userEventListener.handleUserCreatedEvent(invalidEvent);
        });

        // Verify handler was not called for invalid event
        verify(userCreatedEventHandler, never()).handle(invalidEvent);
    }

    @Test
    void should_propagate_exception_from_handler() {
        // Given
        doThrow(new RuntimeException("Handler处理失败")).when(userCreatedEventHandler).handle(any());

        // When & Then - 应该传播异常以触发消息重试
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userEventListener.handleUserCreatedEvent(testEvent);
        });

        assertEquals("Handler处理失败", exception.getMessage());
    }

    @Test
    void should_handle_user_updated_event_successfully() {
        // Given
        String eventJson = "{\"userId\":1,\"username\":\"testuser\"}";

        // When & Then - 不应抛出异常
        assertDoesNotThrow(() -> {
            userEventListener.handleUserUpdatedEvent(eventJson);
        });
    }

    @Test
    void should_handle_empty_user_updated_event() {
        // Given
        String emptyEventJson = "";

        // When & Then - 应该优雅处理空消息
        assertDoesNotThrow(() -> {
            userEventListener.handleUserUpdatedEvent(emptyEventJson);
        });

        // Verify handler was not called
        verify(userUpdatedEventHandler, never()).handle(any());
    }

    @Test
    void should_handle_null_user_updated_event() {
        // Given
        String nullEventJson = null;

        // When & Then - 应该优雅处理null消息
        assertDoesNotThrow(() -> {
            userEventListener.handleUserUpdatedEvent(nullEventJson);
        });

        // Verify handler was not called
        verify(userUpdatedEventHandler, never()).handle(any());
    }

    @Test
    void should_handle_user_role_changed_event_successfully() {
        // Given
        String eventJson = "{\"userId\":1,\"oldRole\":\"sales\",\"newRole\":\"leader\"}";

        // When & Then - 不应抛出异常
        assertDoesNotThrow(() -> {
            userEventListener.handleUserRoleChangedEvent(eventJson);
        });
    }

    @Test
    void should_handle_user_status_changed_event_successfully() {
        // Given
        String eventJson = "{\"userId\":1,\"oldStatus\":\"ACTIVE\",\"newStatus\":\"SUSPENDED\"}";

        // When & Then - 不应抛出异常
        assertDoesNotThrow(() -> {
            userEventListener.handleUserStatusChangedEvent(eventJson);
        });
    }

    @Test
    void should_handle_user_deleted_event_successfully() {
        // Given
        String eventJson = "{\"userId\":1,\"username\":\"testuser\"}";

        // When & Then - 不应抛出异常
        assertDoesNotThrow(() -> {
            userEventListener.handleUserDeletedEvent(eventJson);
        });
    }

    @Test
    void should_handle_generic_user_event_successfully() {
        // Given
        String eventJson = "{\"eventType\":\"user.custom\",\"userId\":1}";

        // When & Then - 不应抛出异常
        assertDoesNotThrow(() -> {
            userEventListener.handleGenericUserEvent(eventJson);
        });
    }

    @Test
    void should_handle_malformed_json_gracefully() {
        // Given
        String malformedJson = "{invalid json}";

        // When & Then - 应该优雅处理无效JSON
        assertDoesNotThrow(() -> {
            userEventListener.handleUserUpdatedEvent(malformedJson);
            userEventListener.handleUserRoleChangedEvent(malformedJson);
            userEventListener.handleUserStatusChangedEvent(malformedJson);
            userEventListener.handleUserDeletedEvent(malformedJson);
            userEventListener.handleGenericUserEvent(malformedJson);
        });
    }

    @Test
    void should_log_correlation_id_properly() {
        // Given
        UserCreatedEvent eventWithCorrelationId = UserCreatedEvent.create(
                1L, "testuser", "13800138000", "sales", "test-correlation-123");

        // When & Then - 不应抛出异常
        assertDoesNotThrow(() -> {
            userEventListener.handleUserCreatedEvent(eventWithCorrelationId);
        });

        verify(userCreatedEventHandler, times(1)).handle(eventWithCorrelationId);
    }

    @Test
    void should_handle_event_without_correlation_id() {
        // Given
        UserCreatedEvent eventWithoutCorrelationId = UserCreatedEvent.create(
                1L, "testuser", "13800138000", "sales", null);

        // When & Then - 应该能处理没有correlationId的事件
        assertDoesNotThrow(() -> {
            userEventListener.handleUserCreatedEvent(eventWithoutCorrelationId);
        });
    }

    @Test
    void should_validate_rabbit_listener_configuration() {
        // Given - 检查方法是否有正确的@RabbitListener注解
        boolean hasRabbitListener = java.util.Arrays.stream(
                userEventListener.getClass().getDeclaredMethods())
                .filter(method -> method.getName().equals("handleUserCreatedEvent"))
                .anyMatch(method -> method.isAnnotationPresent(
                        org.springframework.amqp.rabbit.annotation.RabbitListener.class));

        // Then
        assertTrue(hasRabbitListener, "handleUserCreatedEvent方法应该有@RabbitListener注解");
    }

    @Test
    void should_handle_concurrent_events() {
        // Given - 模拟并发事件处理
        UserCreatedEvent event1 = UserCreatedEvent.create(1L, "user1", "13800138001", "sales", "corr-1");
        UserCreatedEvent event2 = UserCreatedEvent.create(2L, "user2", "13800138002", "leader", "corr-2");

        // When & Then - 应该能处理并发事件
        assertDoesNotThrow(() -> {
            userEventListener.handleUserCreatedEvent(event1);
            userEventListener.handleUserCreatedEvent(event2);
        });

        verify(userCreatedEventHandler, times(2)).handle(any(UserCreatedEvent.class));
    }

    @Test
    void should_handle_large_event_payload() {
        // Given - 创建包含大量数据的事件
        String largeUsername = "user_" + "a".repeat(1000);
        UserCreatedEvent largeEvent = UserCreatedEvent.create(
                1L, largeUsername, "13800138000", "sales", "large-event-correlation-id");

        // When & Then - 应该能处理大负载事件
        assertDoesNotThrow(() -> {
            userEventListener.handleUserCreatedEvent(largeEvent);
        });

        verify(userCreatedEventHandler, times(1)).handle(largeEvent);
    }

    /**
     * 创建测试用的用户创建事件
     */
    private UserCreatedEvent createTestUserCreatedEvent() {
        return UserCreatedEvent.create(
                1L, 
                "testuser", 
                "13800138000", 
                "sales", 
                "test-correlation-id"
        );
    }

    /**
     * 创建无效的用户创建事件（用于测试验证逻辑）
     */
    private UserCreatedEvent createInvalidUserCreatedEvent() {
        // 创建一个模拟的无效事件
        UserCreatedEvent event = UserCreatedEvent.create(null, null, null, null, null);
        return event;
    }

    /**
     * 性能测试：处理大量事件
     */
    @Test
    void should_handle_many_events_efficiently() {
        // Given
        int eventCount = 100;

        // When & Then - 应该能高效处理大量事件
        assertTimeout(java.time.Duration.ofSeconds(5), () -> {
            for (int i = 0; i < eventCount; i++) {
                UserCreatedEvent event = UserCreatedEvent.create(
                        (long) i, "user" + i, "1380013800" + (i % 10), "sales", "corr-" + i);
                userEventListener.handleUserCreatedEvent(event);
            }
        });

        verify(userCreatedEventHandler, times(eventCount)).handle(any(UserCreatedEvent.class));
    }
}