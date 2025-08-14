package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Map;

@Schema(description = "来源检测请求")
public class SourceDetectionRequest implements Serializable {
    @Schema(description = "referrer URL")
    private String referrer;
    @Schema(description = "UTM参数")
    private Map<String, String> utmParams;
    @Schema(description = "User-Agent")
    private String userAgent;
    @Schema(description = "推荐码")
    private String referralCode;

    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }
    public Map<String, String> getUtmParams() { return utmParams; }
    public void setUtmParams(Map<String, String> utmParams) { this.utmParams = utmParams; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
}

