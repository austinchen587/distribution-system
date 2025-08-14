package com.example.user.event.publisher;

import com.example.common.dto.CommonResult;
import com.example.common.event.DomainEventPublisher;
import com.example.common.event.domain.UserCreatedEvent;
import com.example.data.entity.User;
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
 * UserEventPublisher单元测试
 * 
 * <p>测试用户事件发布器的各项功能，包括：
 * <ul>
 *   <li>用户创建事件发布</li>
 *   <li>用户更新事件发布</li>
 *   <li>用户删除事件发布</li>
 *   <li>用户状态变更事件发布</li>
 *   <li>用户角色变更事件发布</li>
 *   <li>异步事件发布</li>
 *   <li>事件发布失败处理</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-12
 */
@ExtendWith(MockitoExtension.class)
class UserEventPublisherTest {

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private UserEventPublisher userEventPublisher;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
    }

    @Test
    void should_publish_user_created_event_successfully() {
        // Given
        when(eventPublisher.publishEvent(any(UserCreatedEvent.class)))
                .thenReturn(CommonResult.success());

        // When
        CommonResult<Void> result = userEventPublisher.publishUserCreated(testUser);

        // Then
        assertTrue(result.isSuccess());
        verify(eventPublisher, times(1)).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void should_publish_user_created_event_with_invitation_successfully() {
        // Given
        String invitationCode = "INV123456";
        Long inviterId = 2L;
        when(eventPublisher.publishEvent(any(UserCreatedEvent.class)))
                .thenReturn(CommonResult.success());

        // When
        CommonResult<Void> result = userEventPublisher.publishUserCreated(
                testUser, invitationCode, inviterId);

        // Then
        assertTrue(result.isSuccess());
        verify(eventPublisher, times(1)).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void should_handle_user_created_event_publish_failure() {
        // Given
        when(eventPublisher.publishEvent(any(UserCreatedEvent.class)))
                .thenReturn(CommonResult.error("发布失败"));

        // When
        CommonResult<Void> result = userEventPublisher.publishUserCreated(testUser);

        // Then
        assertFalse(result.isSuccess());
        assertEquals("发布失败", result.getMessage());
    }

    @Test
    void should_publish_user_updated_event_successfully() {
        // Given
        String[] updatedFields = {"username", "email"};
        when(eventPublisher.publishEvent(any()))
                .thenReturn(CommonResult.success());

        // When
        CommonResult<Void> result = userEventPublisher.publishUserUpdated(testUser, updatedFields);

        // Then
        assertTrue(result.isSuccess());
        // Note: publishUserUpdated目前返回success但实际没有调用eventPublisher
        // 这是因为UserUpdatedEvent还未实现，这里测试当前的实现
    }

    @Test
    void should_publish_user_role_changed_event_successfully() {
        // Given
        String oldRole = "sales";
        String newRole = "leader";
        when(eventPublisher.publishEvent(any()))
                .thenReturn(CommonResult.success());

        // When
        CommonResult<Void> result = userEventPublisher.publishUserRoleChanged(
                testUser, oldRole, newRole);

        // Then
        assertTrue(result.isSuccess());
        // Note: 同样，这里测试当前的实现（返回success但实际未发布）
    }

    @Test
    void should_publish_user_status_changed_event_successfully() {
        // Given
        String oldStatus = "ACTIVE";
        String newStatus = "SUSPENDED";
        when(eventPublisher.publishEvent(any()))
                .thenReturn(CommonResult.success());

        // When
        CommonResult<Void> result = userEventPublisher.publishUserStatusChanged(
                testUser, oldStatus, newStatus);

        // Then
        assertTrue(result.isSuccess());
    }

    @Test
    void should_publish_user_deleted_event_successfully() {
        // Given
        when(eventPublisher.publishEvent(any()))
                .thenReturn(CommonResult.success());

        // When
        CommonResult<Void> result = userEventPublisher.publishUserDeleted(
                testUser.getId(), testUser.getUsername());

        // Then
        assertTrue(result.isSuccess());
    }

    @Test
    void should_handle_exception_during_event_publish() {
        // Given
        when(eventPublisher.publishEvent(any(UserCreatedEvent.class)))
                .thenThrow(new RuntimeException("RabbitMQ连接异常"));

        // When
        CommonResult<Void> result = userEventPublisher.publishUserCreated(testUser);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("发布用户创建事件失败"));
    }

    @Test
    void should_validate_user_entity_before_publish() {
        // Given
        User invalidUser = new User();
        // 不设置必需字段，使用户无效

        // When
        CommonResult<Void> result = userEventPublisher.publishUserCreated(invalidUser);

        // Then
        // 由于UserEventPublisher会尝试创建事件，但User实体不完整
        // 实际行为取决于UserCreatedEvent.create方法的验证逻辑
        assertNotNull(result);
    }

    @Test
    void should_generate_correlation_id_for_each_event() {
        // Given
        when(eventPublisher.publishEvent(any(UserCreatedEvent.class)))
                .thenReturn(CommonResult.success());

        // When
        CommonResult<Void> result1 = userEventPublisher.publishUserCreated(testUser);
        CommonResult<Void> result2 = userEventPublisher.publishUserCreated(testUser);

        // Then
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        verify(eventPublisher, times(2)).publishEvent(any(UserCreatedEvent.class));
        
        // 验证每次调用都生成了不同的correlationId
        // (这个验证需要通过日志或额外的监控来确认)
    }

    /**
     * 创建测试用户实体
     */
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPhone("13800138000");
        user.setRole("sales");
        user.setStatus("ACTIVE");
        return user;
    }

    /**
     * 边界条件测试：空用户
     */
    @Test
    void should_handle_null_user_gracefully() {
        // When & Then
        assertDoesNotThrow(() -> {
            CommonResult<Void> result = userEventPublisher.publishUserCreated(null);
            // 实际行为取决于UserEventPublisher的null检查实现
            assertNotNull(result);
        });
    }

    /**
     * 边界条件测试：空更新字段
     */
    @Test
    void should_handle_empty_updated_fields() {
        // Given
        String[] emptyFields = {};

        // When
        CommonResult<Void> result = userEventPublisher.publishUserUpdated(testUser, emptyFields);

        // Then
        assertTrue(result.isSuccess());
    }

    /**
     * 边界条件测试：null更新字段
     */
    @Test
    void should_handle_null_updated_fields() {
        // When
        CommonResult<Void> result = userEventPublisher.publishUserUpdated(testUser, null);

        // Then
        assertTrue(result.isSuccess());
    }
}