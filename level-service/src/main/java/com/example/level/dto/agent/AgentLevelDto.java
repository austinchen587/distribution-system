package com.example.level.dto.agent;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 代理等级信息DTO
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "代理等级信息")
public class AgentLevelDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 等级ID
     */
    @Schema(description = "等级ID", example = "1")
    @NotNull
    private Integer id;
    
    /**
     * 等级名称
     */
    @Schema(description = "等级名称", example = "SV1")
    @NotBlank
    private String name;
    
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
     * 最小GMV要求
     */
    @Schema(description = "最小GMV要求", example = "50000.00")
    private BigDecimal minGmv;
    
    /**
     * 最大GMV要求
     */
    @Schema(description = "最大GMV要求", example = "200000.00")
    private BigDecimal maxGmv;
    
    /**
     * 等级排序
     */
    @Schema(description = "等级排序", example = "1")
    private Integer levelOrder;
    
    /**
     * 等级代码
     */
    @Schema(description = "等级代码", example = "GOLD")
    private String level;
    
    /**
     * 等级名称
     */
    @Schema(description = "等级名称", example = "金牌代理")
    private String levelName;
    
    /**
     * 所需评分
     */
    @Schema(description = "所需评分", example = "80")
    private Integer requiredScore;
    
    /**
     * 等级权限
     */
    @Schema(description = "等级权限", example = "高级权限")
    private String privileges;
    
    // Constructors
    public AgentLevelDto() {}
    
    public AgentLevelDto(Integer id, String name, BigDecimal commissionRate, BigDecimal baseSalary) {
        this.id = id;
        this.name = name;
        this.commissionRate = commissionRate;
        this.baseSalary = baseSalary;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public BigDecimal getMinGmv() {
        return minGmv;
    }
    
    public void setMinGmv(BigDecimal minGmv) {
        this.minGmv = minGmv;
    }
    
    public BigDecimal getMaxGmv() {
        return maxGmv;
    }
    
    public void setMaxGmv(BigDecimal maxGmv) {
        this.maxGmv = maxGmv;
    }
    
    public Integer getLevelOrder() {
        return levelOrder;
    }
    
    public void setLevelOrder(Integer levelOrder) {
        this.levelOrder = levelOrder;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public String getLevelName() {
        return levelName;
    }
    
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
    
    public Integer getRequiredScore() {
        return requiredScore;
    }
    
    public void setRequiredScore(Integer requiredScore) {
        this.requiredScore = requiredScore;
    }
    
    public String getPrivileges() {
        return privileges;
    }
    
    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }
    
    /**
     * 检查GMV是否符合此等级的要求
     * 
     * @param gmv 要检查的GMV值
     * @return 是否符合要求
     */
    public boolean isGmvInRange(BigDecimal gmv) {
        if (gmv == null) {
            return false;
        }
        
        boolean aboveMin = (minGmv == null) || gmv.compareTo(minGmv) >= 0;
        boolean belowMax = (maxGmv == null) || gmv.compareTo(maxGmv) < 0;
        
        return aboveMin && belowMax;
    }
    
    /**
     * 检查是否为入门级别
     * 
     * @return 是否为最低等级
     */
    public boolean isEntryLevel() {
        return levelOrder != null && levelOrder == 1;
    }
    
    @Override
    public String toString() {
        return "AgentLevelDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", commissionRate=" + commissionRate +
                ", baseSalary=" + baseSalary +
                ", minGmv=" + minGmv +
                ", maxGmv=" + maxGmv +
                ", levelOrder=" + levelOrder +
                '}';
    }
}