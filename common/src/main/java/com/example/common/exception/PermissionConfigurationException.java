package com.example.common.exception;

/**
 * 权限配置异常
 * 
 * <p>当权限配置出现错误或不一致时抛出此异常。
 * 该异常通常在权限配置管理、验证或初始化过程中发生。
 * 
 * <p>异常场景：
 * <ul>
 *   <li>权限配置数据格式错误</li>
 *   <li>权限配置冲突或重复</li>
 *   <li>权限配置初始化失败</li>
 *   <li>权限配置验证失败</li>
 * </ul>
 * 
 * <p>异常处理：
 * <ul>
 *   <li>记录详细的错误日志</li>
 *   <li>阻止系统启动或配置更新</li>
 *   <li>提供配置修复建议</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
public class PermissionConfigurationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    private final String configKey;
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public PermissionConfigurationException(String message) {
        super(message);
        this.errorCode = "PERMISSION_CONFIG_ERROR";
        this.configKey = null;
    }
    
    /**
     * 构造函数（带错误码）
     * 
     * @param message 错误消息
     * @param errorCode 错误码
     */
    public PermissionConfigurationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.configKey = null;
    }
    
    /**
     * 构造函数（带配置键）
     *
     * @param message 错误消息
     * @param configKey 配置键
     */
    public PermissionConfigurationException(String message, String configKey, boolean isConfigKey) {
        super(message);
        this.errorCode = "PERMISSION_CONFIG_ERROR";
        this.configKey = isConfigKey ? configKey : null;
    }
    
    /**
     * 构造函数（带错误码和配置键）
     * 
     * @param message 错误消息
     * @param errorCode 错误码
     * @param configKey 配置键
     */
    public PermissionConfigurationException(String message, String errorCode, String configKey) {
        super(message);
        this.errorCode = errorCode;
        this.configKey = configKey;
    }
    
    /**
     * 构造函数（带原因异常）
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public PermissionConfigurationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PERMISSION_CONFIG_ERROR";
        this.configKey = null;
    }
    
    /**
     * 构造函数（带错误码和原因异常）
     * 
     * @param message 错误消息
     * @param errorCode 错误码
     * @param cause 原因异常
     */
    public PermissionConfigurationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.configKey = null;
    }
    
    /**
     * 构造函数（完整参数）
     * 
     * @param message 错误消息
     * @param errorCode 错误码
     * @param configKey 配置键
     * @param cause 原因异常
     */
    public PermissionConfigurationException(String message, String errorCode, String configKey, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.configKey = configKey;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getConfigKey() {
        return configKey;
    }
    
    /**
     * 获取详细的错误信息
     * 
     * @return 包含错误码和配置键的详细错误描述
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("权限配置异常");
        
        if (errorCode != null) {
            sb.append(" - 错误码: ").append(errorCode);
        }
        
        if (configKey != null) {
            sb.append(" - 配置键: ").append(configKey);
        }
        
        sb.append(" - 消息: ").append(getMessage());
        
        return sb.toString();
    }
}
