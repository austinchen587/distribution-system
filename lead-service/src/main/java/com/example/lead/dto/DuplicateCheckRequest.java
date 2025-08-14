package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "重复检查请求")
public class DuplicateCheckRequest implements Serializable {
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "微信号", example = "zhangsan_wx")
    private String wechatId;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getWechatId() { return wechatId; }
    public void setWechatId(String wechatId) { this.wechatId = wechatId; }
}

