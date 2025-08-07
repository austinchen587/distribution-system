package com.example.common.event;

/**
 * 事件类型枚举
 * 
 * <p>定义系统中所有的领域事件类型，用于事件分类、路由和处理器匹配。
 * 事件类型按照业务域进行组织，便于管理和扩展。
 * 
 * <p>事件分类：
 * <ul>
 *   <li>USER_EVENTS: 用户相关事件（注册、更新、状态变更）</li>
 *   <li>LEAD_EVENTS: 客资相关事件（创建、审核、分配）</li>
 *   <li>PROMOTION_EVENTS: 推广相关事件（提交、审核、奖励）</li>
 *   <li>REWARD_EVENTS: 奖励相关事件（计算、发放、结算）</li>
 *   <li>INVITATION_EVENTS: 邀请相关事件（生成、使用、统计）</li>
 *   <li>SAGA_EVENTS: 事务协调事件（开始、步骤、补偿）</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
public enum EventType {
    
    // ========== 用户相关事件 ==========
    USER_CREATED("user.created", "用户创建事件"),
    USER_UPDATED("user.updated", "用户信息更新事件"),
    USER_DELETED("user.deleted", "用户删除事件"),
    USER_STATUS_CHANGED("user.status.changed", "用户状态变更事件"),
    USER_ROLE_CHANGED("user.role.changed", "用户角色变更事件"),
    USER_LOGIN("user.login", "用户登录事件"),
    USER_LOGOUT("user.logout", "用户登出事件"),
    
    // ========== 客资相关事件 ==========
    LEAD_CREATED("lead.created", "客资创建事件"),
    LEAD_UPDATED("lead.updated", "客资信息更新事件"),
    LEAD_APPROVED("lead.approved", "客资审核通过事件"),
    LEAD_REJECTED("lead.rejected", "客资审核拒绝事件"),
    LEAD_ASSIGNED("lead.assigned", "客资分配事件"),
    LEAD_CONVERTED("lead.converted", "客资转换成功事件"),
    LEAD_DUPLICATE_DETECTED("lead.duplicate.detected", "客资重复检测事件"),
    
    // ========== 推广相关事件 ==========
    PROMOTION_SUBMITTED("promotion.submitted", "推广任务提交事件"),
    PROMOTION_APPROVED("promotion.approved", "推广任务审核通过事件"),
    PROMOTION_REJECTED("promotion.rejected", "推广任务审核拒绝事件"),
    PROMOTION_REWARD_CALCULATED("promotion.reward.calculated", "推广奖励计算事件"),
    PROMOTION_SECOND_AUDIT_REQUESTED("promotion.second.audit.requested", "推广二次审核申请事件"),
    
    // ========== 奖励相关事件 ==========
    REWARD_CALCULATED("reward.calculated", "奖励计算事件"),
    REWARD_DISTRIBUTED("reward.distributed", "奖励发放事件"),
    REWARD_SETTLEMENT_STARTED("reward.settlement.started", "周结算开始事件"),
    REWARD_SETTLEMENT_COMPLETED("reward.settlement.completed", "周结算完成事件"),
    COMMISSION_UPDATED("commission.updated", "佣金更新事件"),
    
    // ========== 邀请相关事件 ==========
    INVITATION_CODE_GENERATED("invitation.code.generated", "邀请码生成事件"),
    INVITATION_CODE_USED("invitation.code.used", "邀请码使用事件"),
    INVITATION_RELATIONSHIP_ESTABLISHED("invitation.relationship.established", "邀请关系建立事件"),
    INVITATION_STATS_UPDATED("invitation.stats.updated", "邀请统计更新事件"),
    
    // ========== Saga事务协调事件 ==========
    SAGA_STARTED("saga.started", "Saga事务开始事件"),
    SAGA_STEP_COMPLETED("saga.step.completed", "Saga步骤完成事件"),
    SAGA_STEP_FAILED("saga.step.failed", "Saga步骤失败事件"),
    SAGA_COMPLETED("saga.completed", "Saga事务完成事件"),
    SAGA_COMPENSATING("saga.compensating", "Saga补偿开始事件"),
    SAGA_COMPENSATED("saga.compensated", "Saga补偿完成事件"),
    SAGA_FAILED("saga.failed", "Saga事务失败事件"),
    
    // ========== 系统事件 ==========
    SYSTEM_CONFIG_UPDATED("system.config.updated", "系统配置更新事件"),
    SYSTEM_MAINTENANCE_STARTED("system.maintenance.started", "系统维护开始事件"),
    SYSTEM_MAINTENANCE_COMPLETED("system.maintenance.completed", "系统维护完成事件");
    
    private final String code;
    private final String description;
    
    /**
     * 构造函数
     * 
     * @param code 事件类型编码
     * @param description 事件类型描述
     */
    EventType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 获取事件类型编码
     * 
     * @return 编码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取事件类型描述
     * 
     * @return 描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据编码获取事件类型
     * 
     * @param code 事件类型编码
     * @return 事件类型
     * @throws IllegalArgumentException 当编码不存在时抛出
     */
    public static EventType fromCode(String code) {
        for (EventType eventType : EventType.values()) {
            if (eventType.getCode().equals(code)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("未知事件类型编码: " + code);
    }
    
    /**
     * 判断是否为用户相关事件
     * 
     * @return 是否为用户事件
     */
    public boolean isUserEvent() {
        return this.code.startsWith("user.");
    }
    
    /**
     * 判断是否为客资相关事件
     * 
     * @return 是否为客资事件
     */
    public boolean isLeadEvent() {
        return this.code.startsWith("lead.");
    }
    
    /**
     * 判断是否为推广相关事件
     * 
     * @return 是否为推广事件
     */
    public boolean isPromotionEvent() {
        return this.code.startsWith("promotion.");
    }
    
    /**
     * 判断是否为奖励相关事件
     * 
     * @return 是否为奖励事件
     */
    public boolean isRewardEvent() {
        return this.code.startsWith("reward.") || this.code.startsWith("commission.");
    }
    
    /**
     * 判断是否为邀请相关事件
     * 
     * @return 是否为邀请事件
     */
    public boolean isInvitationEvent() {
        return this.code.startsWith("invitation.");
    }
    
    /**
     * 判断是否为Saga事务协调事件
     * 
     * @return 是否为Saga事件
     */
    public boolean isSagaEvent() {
        return this.code.startsWith("saga.");
    }
    
    /**
     * 获取事件的交换机名称
     * <p>根据事件类型返回对应的RabbitMQ交换机名称
     * 
     * @return 交换机名称
     */
    public String getExchangeName() {
        if (isUserEvent()) {
            return "user.exchange";
        } else if (isLeadEvent()) {
            return "lead.exchange";
        } else if (isPromotionEvent()) {
            return "promotion.exchange";
        } else if (isRewardEvent()) {
            return "reward.exchange";
        } else if (isInvitationEvent()) {
            return "invitation.exchange";
        } else if (isSagaEvent()) {
            return "saga.exchange";
        } else {
            return "system.exchange";
        }
    }
    
    /**
     * 获取事件的路由键
     * <p>用于RabbitMQ路由规则匹配
     * 
     * @return 路由键
     */
    public String getRoutingKey() {
        return this.code;
    }
}