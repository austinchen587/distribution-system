package com.example.auth.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvitationCode {
    private Long id;
    private Long userId;        // 邀请人ID（codes的创建者）
    private String code;        // 唯一 code
    private String targetRole;  // 允许注册的目标角色（如 agent/sales/...）
    private String status;      // active/inactive
    private Integer usageCount; // 已使用次数
    private Integer maxUsage;   // 最大使用次数，NULL 表示无限
    private LocalDateTime expiresAt; // 过期时间，NULL 表示永不过期
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

