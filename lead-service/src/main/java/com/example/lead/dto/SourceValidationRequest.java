package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Map;

@Schema(description = "来源验证请求")
public class SourceValidationRequest implements Serializable {
    @Schema(description = "来源ID", example = "website")
    private String source;
    @Schema(description = "来源详细")
    private String sourceDetail;
    @Schema(description = "UTM参数")
    private Map<String, String> utmParams;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getSourceDetail() { return sourceDetail; }
    public void setSourceDetail(String sourceDetail) { this.sourceDetail = sourceDetail; }
    public Map<String, String> getUtmParams() { return utmParams; }
    public void setUtmParams(Map<String, String> utmParams) { this.utmParams = utmParams; }
}

