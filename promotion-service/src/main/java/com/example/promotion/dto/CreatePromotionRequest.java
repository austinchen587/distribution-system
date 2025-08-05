package com.example.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建推广任务请求DTO
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "创建推广任务请求")
public class CreatePromotionRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 提交任务的代理ID
     */
    @Schema(description = "提交任务的代理ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "代理ID不能为空")
    private Long agentId;
    
    /**
     * 推广任务标题
     */
    @Schema(description = "推广任务标题", example = "微信朋友圈理财产品推广", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "推广标题不能为空")
    @Size(max = 255, message = "推广标题长度不能超过255字符")
    private String title;
    
    /**
     * 推广内容描述
     */
    @Schema(description = "推广内容描述", example = "通过朋友圈分享理财产品信息，吸引潜在客户")
    private String description;
    
    /**
     * 推广平台
     */
    @Schema(description = "推广平台", example = "微信朋友圈", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "推广平台不能为空")
    @Size(max = 50, message = "推广平台长度不能超过50字符")
    private String platform;
    
    /**
     * 推广内容URL
     */
    @Schema(description = "推广内容URL", example = "https://example.com/promotion/123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "推广链接不能为空")
    @Size(max = 512, message = "推广链接长度不能超过512字符")
    @Pattern(regexp = "^https?://.*", message = "推广链接必须是有效的HTTP或HTTPS地址")
    private String contentUrl;
    
    /**
     * 内容标签
     */
    @Schema(description = "内容标签", example = "[\"理财\", \"投资\", \"稳健\"]")
    private List<String> tags;
    
    /**
     * 代理期望奖励
     */
    @Schema(description = "代理期望奖励", example = "100.00")
    @DecimalMin(value = "0.00", message = "期望奖励不能为负数")
    private BigDecimal expectedReward;
    
    // Constructors
    public CreatePromotionRequest() {}
    
    public CreatePromotionRequest(Long agentId, String title, String platform, String contentUrl) {
        this.agentId = agentId;
        this.title = title;
        this.platform = platform;
        this.contentUrl = contentUrl;
    }
    
    // Getters and Setters
    public Long getAgentId() {
        return agentId;
    }
    
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getContentUrl() {
        return contentUrl;
    }
    
    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public BigDecimal getExpectedReward() {
        return expectedReward;
    }
    
    public void setExpectedReward(BigDecimal expectedReward) {
        this.expectedReward = expectedReward;
    }
    
    @Override
    public String toString() {
        return "CreatePromotionRequest{" +
                "agentId=" + agentId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", platform='" + platform + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", tags=" + tags +
                ", expectedReward=" + expectedReward +
                '}';
    }
}