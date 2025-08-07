package com.example.common.event.domain;

import com.example.common.event.DomainEvent;
import com.example.common.event.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户创建事件
 * 
 * <p>当新用户成功注册时发布此事件。该事件触发后续的业务流程，包括：
 * <ul>
 *   <li>创建用户的邀请码</li>
 *   <li>建立邀请关系（如果通过邀请注册）</li>
 *   <li>初始化用户统计数据</li>
 *   <li>发送欢迎通知</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("UserCreatedEvent")
public class UserCreatedEvent extends DomainEvent {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @JsonProperty("userId")
    private Long userId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @JsonProperty("username")
    private String username;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @JsonProperty("phone")
    private String phone;

    /**
     * 用户角色
     */
    @NotBlank(message = "用户角色不能为空")
    @JsonProperty("role")
    private String role;

    /**
     * 邀请码（如果通过邀请注册）
     */
    @JsonProperty("invitationCode")
    private String invitationCode;

    /**
     * 邀请人ID（如果通过邀请注册）
     */
    @JsonProperty("inviterId")
    private Long inviterId;

    /**
     * 创建用户事件的静态构建方法
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param phone 手机号
     * @param role 用户角色
     * @param correlationId 关联ID
     * @return 用户创建事件
     */
    public static UserCreatedEvent create(Long userId, String username, String phone, String role, String correlationId) {
        return UserCreatedEvent.builder()
                .userId(userId)
                .username(username)
                .phone(phone)
                .role(role)
                .correlationId(correlationId)
                .eventType(EventType.USER_CREATED)
                .build();
    }

    /**
     * 创建带邀请信息的用户事件
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param phone 手机号
     * @param role 用户角色
     * @param invitationCode 邀请码
     * @param inviterId 邀请人ID
     * @param correlationId 关联ID
     * @return 用户创建事件
     */
    public static UserCreatedEvent createWithInvitation(Long userId, String username, String phone, String role,
                                                       String invitationCode, Long inviterId, String correlationId) {
        return UserCreatedEvent.builder()
                .userId(userId)
                .username(username)
                .phone(phone)
                .role(role)
                .invitationCode(invitationCode)
                .inviterId(inviterId)
                .correlationId(correlationId)
                .eventType(EventType.USER_CREATED)
                .build();
    }

    @Override
    public boolean isValid() {
        return super.isValid() 
                && userId != null
                && username != null && !username.trim().isEmpty()
                && phone != null && !phone.trim().isEmpty()
                && role != null && !role.trim().isEmpty();
    }
}