package com.example.common.event;

import com.example.common.event.domain.UserCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DomainEvent 单元测试
 * 
 * 测试领域事件基类的核心功能：
 * - 事件ID生成和验证
 * - 关联ID生成和管理
 * - 事件初始化和验证
 * - 事件数据完整性检查
 * 
 * @author Event-Driven Architecture Team
 */
@DisplayName("DomainEvent 单元测试")
class DomainEventTest {

    private UserCreatedEvent testEvent;
    private final String testCorrelationId = "test-correlation-123";
    private final String testSource = "auth-service";

    @BeforeEach
    void setUp() {
        // 使用具体的事件类进行测试
        testEvent = UserCreatedEvent.builder()
                .userId(1L)
                .username("testuser")
                .phone("13800138000")
                .role("AGENT")
                .correlationId(testCorrelationId)
                .eventType(EventType.USER_CREATED)
                .timestamp(LocalDateTime.now())
                .source(testSource)
                .build();
    }

    @Test
    @DisplayName("正常场景：事件ID生成应该唯一且有效")
    void should_GenerateUniqueEventId_when_CreateEvent() {
        // Given & When
        String eventId1 = DomainEvent.generateEventId();
        String eventId2 = DomainEvent.generateEventId();

        // Then
        assertAll("事件ID生成验证",
                () -> assertNotNull(eventId1, "事件ID不能为空"),
                () -> assertNotNull(eventId2, "事件ID不能为空"),
                () -> assertNotEquals(eventId1, eventId2, "事件ID应该唯一"),
                () -> assertTrue(eventId1.length() > 0, "事件ID应该有内容"),
                () -> assertDoesNotThrow(() -> java.util.UUID.fromString(eventId1), "事件ID应该是有效的UUID格式")
        );
    }

    @Test
    @DisplayName("正常场景：关联ID生成应该唯一且有效")
    void should_GenerateUniqueCorrelationId_when_CreateCorrelationId() {
        // Given & When
        String correlationId1 = DomainEvent.generateCorrelationId();
        String correlationId2 = DomainEvent.generateCorrelationId();

        // Then
        assertAll("关联ID生成验证",
                () -> assertNotNull(correlationId1, "关联ID不能为空"),
                () -> assertNotNull(correlationId2, "关联ID不能为空"),
                () -> assertNotEquals(correlationId1, correlationId2, "关联ID应该唯一"),
                () -> assertTrue(correlationId1.length() > 0, "关联ID应该有内容"),
                () -> assertDoesNotThrow(() -> java.util.UUID.fromString(correlationId1), "关联ID应该是有效的UUID格式")
        );
    }

    @Test
    @DisplayName("正常场景：事件初始化应该正确设置基础字段")
    void should_InitializeEventCorrectly_when_InitializeEvent() {
        // Given
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(1L)
                .username("testuser")
                .phone("13800138000")
                .role("AGENT")
                .correlationId(testCorrelationId)
                .eventType(EventType.USER_CREATED)
                .build();

        // When
        event.initializeEvent(testSource);

        // Then
        assertAll("事件初始化验证",
                () -> assertNotNull(event.getEventId(), "事件ID应该被设置"),
                () -> assertNotNull(event.getTimestamp(), "时间戳应该被设置"),
                () -> assertEquals(testSource, event.getSource(), "事件来源应该被设置"),
                () -> assertEquals(testCorrelationId, event.getCorrelationId(), "关联ID应该保持不变"),
                () -> assertEquals("1.0", event.getVersion(), "版本号应该为默认值")
        );
    }

    @Test
    @DisplayName("边界条件：空来源初始化应该正常工作")
    void should_HandleEmptySource_when_InitializeEvent() {
        // Given
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(1L)
                .username("testuser")
                .phone("13800138000")
                .role("AGENT")
                .correlationId(testCorrelationId)
                .eventType(EventType.USER_CREATED)
                .build();

        // When & Then
        assertDoesNotThrow(() -> event.initializeEvent(null), "空来源不应该引发异常");
        assertDoesNotThrow(() -> event.initializeEvent(""), "空字符串来源不应该引发异常");
    }

    @Test
    @DisplayName("正常场景：有效事件应该通过验证")
    void should_PassValidation_when_EventIsValid() {
        // Given
        testEvent.initializeEvent(testSource);

        // When
        boolean isValid = testEvent.isValid();

        // Then
        assertTrue(isValid, "完整的事件应该通过验证");
    }

    @Test
    @DisplayName("异常场景：缺少事件ID的事件应该验证失败")
    void should_FailValidation_when_EventIdIsMissing() {
        // Given
        testEvent.setEventId(null);

        // When
        boolean isValid = testEvent.isValid();

        // Then
        assertFalse(isValid, "缺少事件ID的事件应该验证失败");
    }

    @Test
    @DisplayName("异常场景：缺少关联ID的事件应该验证失败")
    void should_FailValidation_when_CorrelationIdIsMissing() {
        // Given
        testEvent.setCorrelationId(null);
        testEvent.initializeEvent(testSource);

        // When
        boolean isValid = testEvent.isValid();

        // Then
        assertFalse(isValid, "缺少关联ID的事件应该验证失败");
    }

    @Test
    @DisplayName("异常场景：缺少事件类型的事件应该验证失败")
    void should_FailValidation_when_EventTypeIsMissing() {
        // Given
        testEvent.setEventType(null);
        testEvent.initializeEvent(testSource);

        // When
        boolean isValid = testEvent.isValid();

        // Then
        assertFalse(isValid, "缺少事件类型的事件应该验证失败");
    }

    @Test
    @DisplayName("异常场景：缺少时间戳的事件应该验证失败")
    void should_FailValidation_when_TimestampIsMissing() {
        // Given
        testEvent.setTimestamp(null);
        testEvent.initializeEvent(testSource);

        // When
        boolean isValid = testEvent.isValid();

        // Then
        assertFalse(isValid, "缺少时间戳的事件应该验证失败");
    }

    @Test
    @DisplayName("异常场景：缺少来源的事件应该验证失败")
    void should_FailValidation_when_SourceIsMissing() {
        // Given
        testEvent.initializeEvent(testSource);
        testEvent.setSource(null);

        // When
        boolean isValid = testEvent.isValid();

        // Then
        assertFalse(isValid, "缺少来源的事件应该验证失败");
    }

    @Test
    @DisplayName("边界条件：空字符串字段应该验证失败")
    void should_FailValidation_when_StringFieldsAreEmpty() {
        // Given
        testEvent.initializeEvent(testSource);

        // When & Then
        testEvent.setEventId("");
        assertFalse(testEvent.isValid(), "空事件ID应该验证失败");

        testEvent.setEventId("valid-id");
        testEvent.setCorrelationId("");
        assertFalse(testEvent.isValid(), "空关联ID应该验证失败");

        testEvent.setCorrelationId(testCorrelationId);
        testEvent.setSource("");
        assertFalse(testEvent.isValid(), "空来源应该验证失败");
    }

    @Test
    @DisplayName("边界条件：只有空格的字符串字段应该验证失败")
    void should_FailValidation_when_StringFieldsAreWhitespaceOnly() {
        // Given
        testEvent.initializeEvent(testSource);

        // When & Then
        testEvent.setEventId("   ");
        assertFalse(testEvent.isValid(), "仅有空格的事件ID应该验证失败");

        testEvent.setEventId("valid-id");
        testEvent.setCorrelationId("   ");
        assertFalse(testEvent.isValid(), "仅有空格的关联ID应该验证失败");

        testEvent.setCorrelationId(testCorrelationId);
        testEvent.setSource("   ");
        assertFalse(testEvent.isValid(), "仅有空格的来源应该验证失败");
    }

    @Test
    @DisplayName("正常场景：toString方法应该包含关键信息")
    void should_ContainKeyInformation_when_ToString() {
        // Given
        testEvent.initializeEvent(testSource);

        // When
        String eventString = testEvent.toString();

        // Then
        assertAll("toString 验证",
                () -> assertNotNull(eventString, "toString不能返回null"),
                () -> assertTrue(eventString.contains("UserCreatedEvent"), "应该包含事件类名"),
                () -> assertTrue(eventString.contains(testEvent.getEventId()), "应该包含事件ID"),
                () -> assertTrue(eventString.contains(testCorrelationId), "应该包含关联ID"),
                () -> assertTrue(eventString.contains("USER_CREATED"), "应该包含事件类型"),
                () -> assertTrue(eventString.contains(testSource), "应该包含事件来源")
        );
    }

    @Test
    @DisplayName("正常场景：重复初始化不应该改变已设置的事件ID")
    void should_NotChangeEventId_when_InitializedTwice() {
        // Given
        testEvent.initializeEvent(testSource);
        String originalEventId = testEvent.getEventId();

        // When
        testEvent.initializeEvent("another-service");

        // Then
        assertEquals(originalEventId, testEvent.getEventId(), "重复初始化不应该改变事件ID");
    }

    @Test
    @DisplayName("正常场景：重复初始化不应该改变已设置的时间戳")
    void should_NotChangeTimestamp_when_InitializedTwice() {
        // Given
        testEvent.initializeEvent(testSource);
        LocalDateTime originalTimestamp = testEvent.getTimestamp();

        // When
        // 稍微等待确保时间不同
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        testEvent.initializeEvent("another-service");

        // Then
        assertEquals(originalTimestamp, testEvent.getTimestamp(), "重复初始化不应该改变时间戳");
    }
}