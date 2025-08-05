package com.example.common.constants;

/**
 * 统一错误码定义
 * 
 * 错误码格式: {MODULE}_{NUMBER}
 * - MODULE: 业务模块标识（大写英文）
 * - NUMBER: 3位数字（001-099每模块）
 * 
 * 每个错误码都映射到对应的HTTP状态码
 * 
 * @author Backend Team
 * @version 2.0.0
 */
public enum ErrorCode {
    
    // ============= HTTP标准状态码 =============
    SUCCESS(200, "SUCCESS", "操作成功"),
    BAD_REQUEST(400, "BAD_REQUEST", "请求参数错误"),
    UNAUTHORIZED(401, "UNAUTHORIZED", "未授权访问"),
    FORBIDDEN(403, "FORBIDDEN", "权限不足"),
    NOT_FOUND(404, "NOT_FOUND", "资源不存在"),
    CONFLICT(409, "CONFLICT", "资源冲突"),
    UNPROCESSABLE_ENTITY(422, "UNPROCESSABLE_ENTITY", "参数校验失败"),
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", "操作过于频繁"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "系统内部错误"),
    
    // ============= 通用错误码 (COMMON_001-099) =============
    COMMON_001(503, "COMMON_001", "系统维护中"),
    COMMON_002(408, "COMMON_002", "请求超时"),
    COMMON_003(500, "COMMON_003", "数据库连接失败"),
    COMMON_004(500, "COMMON_004", "缓存服务异常"),
    COMMON_005(502, "COMMON_005", "第三方服务异常"),

    // ============= 测试兼容性错误码 =============
    RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND", "资源不存在"),
    OPERATION_FAILED(500, "OPERATION_FAILED", "操作失败"),

    // ============= 权限控制错误码 =============
    DATA_ACCESS_DENIED(403, "DATA_ACCESS_DENIED", "数据访问被拒绝"),
    ROLE_NOT_ALLOWED(403, "ROLE_NOT_ALLOWED", "角色权限不足"),
    PERMISSION_DENIED(403, "PERMISSION_DENIED", "权限被拒绝"),
    
    // ============= 认证服务 (AUTH_001-099) =============
    AUTH_001(401, "AUTH_001", "用户名或密码错误"),
    AUTH_002(403, "AUTH_002", "账户已被锁定"),
    AUTH_003(403, "AUTH_003", "账户已被禁用"),
    AUTH_004(401, "AUTH_004", "Token已过期"),
    AUTH_005(401, "AUTH_005", "Token格式无效"),
    AUTH_006(401, "AUTH_006", "刷新Token无效"),
    AUTH_007(429, "AUTH_007", "登录失败次数过多"),
    AUTH_008(400, "AUTH_008", "验证码错误"),
    AUTH_009(400, "AUTH_009", "验证码已过期"),
    AUTH_010(422, "AUTH_010", "密码强度不足"),
    
    // ============= 用户管理 (USER_001-099) =============
    USER_001(404, "USER_001", "用户不存在"),
    USER_002(409, "USER_002", "用户名已存在"),
    USER_003(409, "USER_003", "邮箱已被使用"),
    USER_004(409, "USER_004", "手机号已被使用"),
    USER_005(422, "USER_005", "密码格式不正确"),
    USER_006(422, "USER_006", "用户状态异常"),
    USER_007(422, "USER_007", "层级关系错误"),
    USER_008(403, "USER_008", "权限不足"),
    USER_009(422, "USER_009", "批量操作数量超限"),
    USER_010(422, "USER_010", "导出数据过多"),
    
    // ============= 客资管理 (LEAD_001-099) =============
    LEAD_001(404, "LEAD_001", "客资不存在"),
    LEAD_002(409, "LEAD_002", "手机号已存在"),
    LEAD_003(422, "LEAD_003", "客资姓名不能为空"),
    LEAD_004(422, "LEAD_004", "手机号格式不正确"),
    LEAD_005(422, "LEAD_005", "来源信息无效"),
    LEAD_006(422, "LEAD_006", "客资状态异常"),
    LEAD_007(403, "LEAD_007", "权限不足"),
    LEAD_008(404, "LEAD_008", "销售人员不存在"),
    LEAD_009(500, "LEAD_009", "重复客资检查失败"),
    LEAD_010(500, "LEAD_010", "来源检测失败"),
    LEAD_011(422, "LEAD_011", "推荐码无效"),
    LEAD_012(500, "LEAD_012", "批量导入失败"),
    LEAD_013(422, "LEAD_013", "导出数据过多"),
    LEAD_014(422, "LEAD_014", "UTM参数格式错误"),
    LEAD_015(422, "LEAD_015", "来源验证失败"),
    
    // ============= 交易管理 (DEAL_001-099) =============
    DEAL_001(404, "DEAL_001", "交易不存在"),
    DEAL_002(422, "DEAL_002", "交易状态异常"),
    DEAL_003(422, "DEAL_003", "交易金额无效"),
    DEAL_004(500, "DEAL_004", "佣金计算失败"),
    DEAL_005(404, "DEAL_005", "产品不存在"),
    DEAL_006(422, "DEAL_006", "客资未分配"),
    DEAL_007(409, "DEAL_007", "重复交易"),
    DEAL_008(422, "DEAL_008", "交易已完成无法修改"),
    DEAL_009(403, "DEAL_009", "权限不足"),
    DEAL_010(422, "DEAL_010", "结算周期错误"),
    
    // ============= 产品管理 (PRODUCT_001-099) =============
    PRODUCT_001(404, "PRODUCT_001", "产品不存在"),
    PRODUCT_002(409, "PRODUCT_002", "产品名称已存在"),
    PRODUCT_003(422, "PRODUCT_003", "产品状态异常"),
    PRODUCT_004(422, "PRODUCT_004", "价格配置错误"),
    PRODUCT_005(422, "PRODUCT_005", "佣金比例无效"),
    PRODUCT_006(404, "PRODUCT_006", "产品分类不存在"),
    PRODUCT_007(422, "PRODUCT_007", "库存不足"),
    PRODUCT_008(422, "PRODUCT_008", "产品已下架"),
    PRODUCT_009(403, "PRODUCT_009", "权限不足"),
    PRODUCT_010(500, "PRODUCT_010", "批量操作失败"),
    
    // ============= 推广管理 (PROMOTION_001-099) =============
    PROMOTION_001(404, "PROMOTION_001", "推广任务不存在"),
    PROMOTION_002(403, "PROMOTION_002", "没有审核权限"),
    PROMOTION_003(403, "PROMOTION_003", "只能审核指定范围内的任务"),
    PROMOTION_004(409, "PROMOTION_004", "任务已被其他审核员处理"),
    PROMOTION_005(422, "PROMOTION_005", "任务当前状态不允许审核"),
    PROMOTION_006(403, "PROMOTION_006", "无法审核自己提交的任务"),
    PROMOTION_007(422, "PROMOTION_007", "审核意见不能为空"),
    PROMOTION_008(422, "PROMOTION_008", "审核意见长度超限"),
    PROMOTION_009(422, "PROMOTION_009", "奖励金额必须大于0"),
    PROMOTION_010(422, "PROMOTION_010", "奖励金额超出范围限制"),
    PROMOTION_011(422, "PROMOTION_011", "任务状态异常"),
    PROMOTION_012(408, "PROMOTION_012", "审核操作超时"),
    PROMOTION_013(422, "PROMOTION_013", "批量审核任务数量超限"),
    PROMOTION_014(429, "PROMOTION_014", "提交次数已达每日限制"),
    PROMOTION_015(422, "PROMOTION_015", "URL无法识别或不支持"),
    
    // ============= 代理管理 (AGENT_001-099) =============
    AGENT_001(404, "AGENT_001", "代理不存在"),
    AGENT_002(409, "AGENT_002", "邮箱已被使用"),
    AGENT_003(409, "AGENT_003", "手机号已被使用"),
    AGENT_004(422, "AGENT_004", "代理等级无效"),
    AGENT_005(404, "AGENT_005", "上级代理不存在"),
    AGENT_006(422, "AGENT_006", "存在下级代理，无法删除"),
    AGENT_007(500, "AGENT_007", "业绩数据计算失败"),
    AGENT_008(422, "AGENT_008", "层级关系冲突"),
    AGENT_009(422, "AGENT_009", "代理状态异常"),
    AGENT_010(403, "AGENT_010", "权限不足"),
    
    // ============= 邀请系统 (INVITE_001-099) =============
    INVITE_001(404, "INVITE_001", "邀请码无效或不存在"),
    INVITE_002(422, "INVITE_002", "邀请码已过期"),
    INVITE_003(403, "INVITE_003", "角色无法使用此邀请码"),
    INVITE_004(422, "INVITE_004", "不能使用自己的邀请码"),
    INVITE_005(403, "INVITE_005", "没有邀请权限"),
    INVITE_006(422, "INVITE_006", "邀请码使用次数已达上限"),
    INVITE_007(404, "INVITE_007", "邀请码不存在"),
    INVITE_008(409, "INVITE_008", "此邀请码已被使用过"),
    INVITE_009(422, "INVITE_009", "邀请码配额已达上限"),
    INVITE_010(422, "INVITE_010", "目标角色无效"),
    
    // ============= 奖励系统 (REWARD_001-099) =============
    REWARD_001(429, "REWARD_001", "提交次数已达每日限制"),
    REWARD_002(404, "REWARD_002", "结算周期不存在"),
    REWARD_003(422, "REWARD_003", "二次审核资格不符合"),
    REWARD_004(404, "REWARD_004", "奖励记录不存在"),
    REWARD_005(422, "REWARD_005", "结算状态异常"),
    REWARD_006(500, "REWARD_006", "奖励金额计算错误"),
    REWARD_007(403, "REWARD_007", "权限不足"),
    REWARD_008(422, "REWARD_008", "结算周期已锁定"),
    REWARD_009(409, "REWARD_009", "重复结算"),
    REWARD_010(500, "REWARD_010", "数据同步失败"),
    
    // ============= 系统配置 (CONFIG_001-099) =============
    CONFIG_001(404, "CONFIG_001", "配置不存在"),
    CONFIG_002(422, "CONFIG_002", "配置格式错误"),
    CONFIG_003(409, "CONFIG_003", "配置版本冲突"),
    CONFIG_004(422, "CONFIG_004", "配置审核失败"),
    CONFIG_005(500, "CONFIG_005", "数据同步失败"),
    CONFIG_006(403, "CONFIG_006", "权限不足"),
    CONFIG_007(422, "CONFIG_007", "配置类型无效"),
    CONFIG_008(422, "CONFIG_008", "配置状态异常"),
    CONFIG_009(422, "CONFIG_009", "配置规则验证失败"),
    CONFIG_010(500, "CONFIG_010", "批量操作失败"),
    
    // ============= 仪表盘统计 (DASHBOARD_001-099) =============
    DASHBOARD_001(404, "DASHBOARD_001", "数据不存在"),
    DASHBOARD_002(422, "DASHBOARD_002", "时间范围无效"),
    DASHBOARD_003(422, "DASHBOARD_003", "图表类型不支持"),
    DASHBOARD_004(500, "DASHBOARD_004", "数据计算失败"),
    DASHBOARD_005(403, "DASHBOARD_005", "权限不足"),
    DASHBOARD_006(422, "DASHBOARD_006", "数据量过大"),
    DASHBOARD_007(500, "DASHBOARD_007", "缓存失效"),
    DASHBOARD_008(500, "DASHBOARD_008", "导出失败"),
    DASHBOARD_009(422, "DASHBOARD_009", "筛选条件无效"),
    DASHBOARD_010(422, "DASHBOARD_010", "统计维度不支持");
    
    private final Integer httpCode;
    private final String errorCode;
    private final String message;
    
    ErrorCode(Integer httpCode, String errorCode, String message) {
        this.httpCode = httpCode;
        this.errorCode = errorCode;
        this.message = message;
    }
    
    /**
     * 获取HTTP状态码
     */
    public Integer getHttpCode() {
        return httpCode;
    }
    
    /**
     * 获取业务错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取错误消息
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * 向后兼容的方法 - 返回HTTP状态码
     * @deprecated 建议使用 getHttpCode()
     */
    @Deprecated
    public Integer getCode() {
        return httpCode;
    }
    
    @Override
    public String toString() {
        return errorCode + "(" + httpCode + "): " + message;
    }
    
    /**
     * 根据业务错误码查找ErrorCode枚举
     * @param errorCode 业务错误码，如 "USER_001"
     * @return ErrorCode枚举实例，如果未找到返回null
     */
    public static ErrorCode fromErrorCode(String errorCode) {
        for (ErrorCode code : values()) {
            if (code.getErrorCode().equals(errorCode)) {
                return code;
            }
        }
        return null;
    }
    
    /**
     * 根据HTTP状态码查找对应的标准错误码
     * @param httpCode HTTP状态码
     * @return ErrorCode枚举实例，优先返回标准HTTP错误码
     */
    public static ErrorCode fromHttpCode(Integer httpCode) {
        // 优先返回标准HTTP状态码
        for (ErrorCode code : values()) {
            if (code.getHttpCode().equals(httpCode) && 
                (code.name().equals("SUCCESS") || 
                 code.name().equals("BAD_REQUEST") || 
                 code.name().equals("UNAUTHORIZED") ||
                 code.name().equals("FORBIDDEN") ||
                 code.name().equals("NOT_FOUND") ||
                 code.name().equals("CONFLICT") ||
                 code.name().equals("UNPROCESSABLE_ENTITY") ||
                 code.name().equals("TOO_MANY_REQUESTS") ||
                 code.name().equals("INTERNAL_SERVER_ERROR"))) {
                return code;
            }
        }
        return null;
    }
}