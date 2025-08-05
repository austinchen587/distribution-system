package com.example.auth.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户详细信息DTO - 包含代理等级和业绩信息
 * 用于需要完整用户信息的场景
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "用户详细信息")
public class UserDetailsDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户基础信息
     */
    @Schema(description = "用户基础信息")
    private UserDto userInfo;
    
    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    
    /**
     * 微信ID
     */
    @Schema(description = "微信ID", example = "wechat_id_123")
    private String wechatId;
    
    /**
     * 代理等级名称
     */
    @Schema(description = "代理等级名称", example = "SV1")
    private String agentLevelName;
    
    /**
     * 佣金比例
     */
    @Schema(description = "佣金比例", example = "0.15")
    private BigDecimal commissionRate;
    
    /**
     * 基础底薪
     */
    @Schema(description = "基础底薪", example = "5000.00")
    private BigDecimal baseSalary;
    
    /**
     * 当前GMV（本月）
     */
    @Schema(description = "当前GMV", example = "50000.00")
    private BigDecimal currentGmv;
    
    /**
     * 累计GMV
     */
    @Schema(description = "累计GMV", example = "500000.00")
    private BigDecimal totalGmv;
    
    /**
     * 邀请码
     */
    @Schema(description = "邀请码", example = "INV123456")
    private String invitationCode;
    
    /**
     * 注册时间
     */
    @Schema(description = "注册时间", example = "2025-01-01 10:00:00")
    private String createdAt;
    
    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间", example = "2025-08-04 10:00:00")
    private String lastLoginAt;
    
    // Constructors
    public UserDetailsDto() {}
    
    public UserDetailsDto(UserDto userInfo) {
        this.userInfo = userInfo;
    }
    
    // Getters and Setters
    public UserDto getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo(UserDto userInfo) {
        this.userInfo = userInfo;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getWechatId() {
        return wechatId;
    }
    
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }
    
    public String getAgentLevelName() {
        return agentLevelName;
    }
    
    public void setAgentLevelName(String agentLevelName) {
        this.agentLevelName = agentLevelName;
    }
    
    public BigDecimal getCommissionRate() {
        return commissionRate;
    }
    
    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
    
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
    
    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }
    
    public BigDecimal getCurrentGmv() {
        return currentGmv;
    }
    
    public void setCurrentGmv(BigDecimal currentGmv) {
        this.currentGmv = currentGmv;
    }
    
    public BigDecimal getTotalGmv() {
        return totalGmv;
    }
    
    public void setTotalGmv(BigDecimal totalGmv) {
        this.totalGmv = totalGmv;
    }
    
    public String getInvitationCode() {
        return invitationCode;
    }
    
    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(String lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    @Override
    public String toString() {
        return "UserDetailsDto{" +
                "userInfo=" + userInfo +
                ", realName='" + realName + '\'' +
                ", wechatId='" + wechatId + '\'' +
                ", agentLevelName='" + agentLevelName + '\'' +
                ", commissionRate=" + commissionRate +
                ", baseSalary=" + baseSalary +
                ", currentGmv=" + currentGmv +
                ", totalGmv=" + totalGmv +
                ", invitationCode='" + invitationCode + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", lastLoginAt='" + lastLoginAt + '\'' +
                '}';
    }
}