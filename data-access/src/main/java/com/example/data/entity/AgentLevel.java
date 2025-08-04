package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 代理等级定义实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
public class AgentLevel {
    
    /**
     * 等级主键ID
     */
    private Integer id;
    
    /**
     * 等级名称 (如: SV1, SV2)
     */
    @NotBlank(message = "等级名称不能为空")
    @Size(max = 50, message = "等级名称长度不能超过50字符")
    private String name;
    
    /**
     * 该等级的基础佣金比例
     */
    @NotNull(message = "佣金比例不能为空")
    @DecimalMin(value = "0.0000", message = "佣金比例不能为负数")
    @DecimalMax(value = "1.0000", message = "佣金比例不能超过100%")
    private BigDecimal commissionRate;
    
    /**
     * 该等级的固定底薪
     */
    @NotNull(message = "基础底薪不能为空")
    @DecimalMin(value = "0.00", message = "基础底薪不能为负数")
    private BigDecimal baseSalary;
    
    /**
     * 晋升到此等级的最小GMV要求
     */
    @DecimalMin(value = "0.00", message = "最小GMV要求不能为负数")
    private BigDecimal minGmv;
    
    /**
     * 此等级的GMV上限（不包含）
     */
    @DecimalMin(value = "0.00", message = "最大GMV要求不能为负数")
    private BigDecimal maxGmv;
    
    /**
     * 用于等级排序的数字，数字越小等级越低
     */
    @NotNull(message = "等级顺序不能为空")
    @Min(value = 1, message = "等级顺序必须大于0")
    private Integer levelOrder;
    
    // Constructors
    public AgentLevel() {}
    
    public AgentLevel(String name, BigDecimal commissionRate, BigDecimal baseSalary, Integer levelOrder) {
        this.name = name;
        this.commissionRate = commissionRate;
        this.baseSalary = baseSalary;
        this.levelOrder = levelOrder;
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
    
    // Business Methods
    
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
    
    /**
     * 比较等级高低
     * 
     * @param other 另一个等级
     * @return 比较结果 (负数: 当前等级低, 0: 相同, 正数: 当前等级高)
     */
    public int compareTo(AgentLevel other) {
        if (other == null || other.levelOrder == null) {
            return 1;
        }
        if (this.levelOrder == null) {
            return -1;
        }
        return this.levelOrder.compareTo(other.levelOrder);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentLevel that = (AgentLevel) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(levelOrder, that.levelOrder);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, levelOrder);
    }
    
    @Override
    public String toString() {
        return "AgentLevel{" +
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