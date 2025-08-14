package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Schema(description = "批量审核请求")
public class BatchAuditRequest implements Serializable {
    @NotEmpty
    @Schema(description = "客资ID列表")
    private List<Long> ids;

    @NotNull
    @Schema(description = "审核状态", allowableValues = {"APPROVED","REJECTED"})
    private String auditStatus;

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
}

