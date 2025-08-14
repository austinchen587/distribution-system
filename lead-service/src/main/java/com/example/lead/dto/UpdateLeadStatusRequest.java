package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(description = "更新客资状态请求")
public class UpdateLeadStatusRequest implements Serializable {
    @Schema(description = "客资跟进状态", allowableValues = {"PENDING","FOLLOWING","CONVERTED","INVALID"}, example = "FOLLOWING")
    @NotBlank(message = "状态不能为空")
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

