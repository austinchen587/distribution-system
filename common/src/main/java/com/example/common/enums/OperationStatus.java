package com.example.common.enums;

/**
 * 操作状态枚举
 * 
 * <p>定义数据操作的执行状态，用于审计日志记录和监控分析。
 * 
 * <p>状态类型说明：
 * <ul>
 *   <li>SUCCESS: 操作成功执行</li>
 *   <li>FAILED: 操作执行失败</li>
 *   <li>DENIED: 操作被权限控制拒绝</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
public enum OperationStatus {
    
    /**
     * 操作成功
     * 数据操作正常执行并完成
     */
    SUCCESS("SUCCESS", "操作成功"),
    
    /**
     * 操作失败
     * 数据操作执行过程中发生错误
     */
    FAILED("FAILED", "操作失败"),
    
    /**
     * 操作被拒绝
     * 数据操作被权限控制系统拒绝执行
     */
    DENIED("DENIED", "操作被拒绝");
    
    private final String code;
    private final String description;
    
    OperationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取操作状态
     * 
     * @param code 操作状态代码
     * @return 对应的操作状态枚举
     * @throws IllegalArgumentException 如果代码无效
     */
    public static OperationStatus fromCode(String code) {
        for (OperationStatus status : OperationStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知操作状态代码: " + code);
    }
    
    /**
     * 检查操作是否成功
     * 
     * @return 如果状态为SUCCESS，返回true
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }
    
    /**
     * 检查操作是否失败
     * 
     * @return 如果状态为FAILED，返回true
     */
    public boolean isFailed() {
        return this == FAILED;
    }
    
    /**
     * 检查操作是否被拒绝
     * 
     * @return 如果状态为DENIED，返回true
     */
    public boolean isDenied() {
        return this == DENIED;
    }
    
    /**
     * 检查操作是否未成功
     * 
     * @return 如果状态为FAILED或DENIED，返回true
     */
    public boolean isNotSuccess() {
        return this == FAILED || this == DENIED;
    }
}
