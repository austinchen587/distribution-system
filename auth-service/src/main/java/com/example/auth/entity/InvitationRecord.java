package com.example.auth.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvitationRecord {
    private Long id;
    private Long inviterId;
    private Long inviteeId;
    private String inviteCode;
    private String status; // success/pending/failed
    private LocalDateTime registeredAt;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}

