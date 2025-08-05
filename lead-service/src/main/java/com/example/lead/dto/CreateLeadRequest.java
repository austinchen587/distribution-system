package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 创建客资请求DTO
 * 包含完整的JSR-303验证规则
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "创建客资请求")
public class CreateLeadRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 客户姓名
     */
    @Schema(description = "客户姓名", example = "李四", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户姓名不能为空")
    @Size(max = 100, message = "客户姓名长度不能超过100字符")
    private String name;
    
    /**
     * 客户手机号
     */
    @Schema(description = "客户手机号", example = "13900139000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 客户微信号
     */
    @Schema(description = "客户微信号", example = "wechat_lisi")
    @Size(max = 64, message = "微信号长度不能超过64字符")
    private String wechatId;
    
    /**
     * 来源渠道
     */
    @Schema(description = "来源渠道", example = "微信群", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "来源渠道不能为空")
    @Size(max = 100, message = "来源渠道长度不能超过100字符")
    private String source;
    
    /**
     * 来源详情
     */
    @Schema(description = "来源详情", example = "XX理财群推荐")
    @Size(max = 255, message = "来源详情长度不能超过255字符")
    private String sourceDetail;
    
    /**
     * 归属销售ID
     */
    @Schema(description = "归属销售ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "归属销售不能为空")
    private Long salespersonId;
    
    /**
     * 跟进备注
     */
    @Schema(description = "跟进备注", example = "客户对理财产品比较感兴趣")
    private String notes;
    
    /**
     * 推荐码
     */
    @Schema(description = "推荐码", example = "REF123456")
    @Size(max = 50, message = "推荐码长度不能超过50字符")
    private String referralCode;
    
    // Constructors
    public CreateLeadRequest() {}
    
    public CreateLeadRequest(String name, String phone, String source, Long salespersonId) {
        this.name = name;
        this.phone = phone;
        this.source = source;
        this.salespersonId = salespersonId;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getWechatId() {
        return wechatId;
    }
    
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSourceDetail() {
        return sourceDetail;
    }
    
    public void setSourceDetail(String sourceDetail) {
        this.sourceDetail = sourceDetail;
    }
    
    public Long getSalespersonId() {
        return salespersonId;
    }
    
    public void setSalespersonId(Long salespersonId) {
        this.salespersonId = salespersonId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getReferralCode() {
        return referralCode;
    }
    
    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
    
    @Override
    public String toString() {
        return "CreateLeadRequest{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", wechatId='" + wechatId + '\'' +
                ", source='" + source + '\'' +
                ", sourceDetail='" + sourceDetail + '\'' +
                ", salespersonId=" + salespersonId +
                ", notes='" + notes + '\'' +
                ", referralCode='" + referralCode + '\'' +
                '}';
    }
}