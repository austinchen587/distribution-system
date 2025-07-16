-- ========================================
-- 清理环境：按依赖关系倒序删除表
-- ========================================
DROP TABLE IF EXISTS `commissions`;
DROP TABLE IF EXISTS `promotions`;
DROP TABLE IF EXISTS `deals`;
DROP TABLE IF EXISTS `customer_leads`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `agent_level_audit`;
DROP TABLE IF EXISTS `user_agent_level`;
DROP TABLE IF EXISTS `agent_levels`;
DROP TABLE IF EXISTS `users`;

-- ========================================
-- 表结构定义
-- ========================================

-- 1. 用户表（含销售和代理）
CREATE TABLE `users` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- OPTIMIZATION: 主键使用 UNSIGNED，确保非负。
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '用户昵称（常用于小程序等场景）',
    `invite_code` VARCHAR(20) DEFAULT NULL UNIQUE COMMENT '用户专属邀请码',
    `openid` VARCHAR(64) DEFAULT NULL UNIQUE COMMENT '微信小程序的唯一用户标识',
    `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    `password` VARCHAR(255) NOT NULL COMMENT '哈希处理后的密码',
    `role` ENUM('super_admin', 'director', 'leader', 'sales', 'agent') NOT NULL COMMENT '角色',
    `inviter_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '邀请人ID', -- OPTIMIZATION: 外键使用 UNSIGNED，与引用的主键类型保持一致。

    -- 状态相关
    `status` ENUM('active', 'banned') NOT NULL DEFAULT 'active' COMMENT '用户状态: active-正常, banned-封禁', -- OPTIMIZATION: 明确 NOT NULL 约束，并提供默认值。

    -- 业务数据
    `total_gmv` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '累计总GMV', -- OPTIMIZATION: 明确 NOT NULL 约束，避免 NULL 计算问题。

    -- 审计字段
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', -- OPTIMIZATION: 使用 TIMESTAMP 类型，更节省空间且支持时区。明确 NOT NULL。
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', -- OPTIMIZATION: 使用 TIMESTAMP 类型。明确 NOT NULL。

    -- 外键约束
    FOREIGN KEY (`inviter_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    -- 索引
    -- OPTIMIZATION: `phone`、`invite_code`、`openid` 已有 UNIQUE 约束，会自动创建索引，无需重复定义。
    INDEX `idx_role_status` (`role`, `status`), -- OPTIMIZATION: 复合索引，优化按角色和状态的筛选，比单个索引更高效。
    INDEX `idx_inviter_id` (`inviter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';



-- 2. 代理等级表（SV1 - SV6）
CREATE TABLE `agent_levels` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- OPTIMIZATION: 主键使用 UNSIGNED。
    `name` VARCHAR(50) NOT NULL COMMENT '等级名称', -- OPTIMIZATION: 适当放宽长度以备用。
    `commission_rate` DECIMAL(5,2) NOT NULL COMMENT '佣金比例(%)',
    `base_salary` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '等级底薪, 支持底薪+提成模式',
    `min_gmv` DECIMAL(15,2) DEFAULT NULL COMMENT '最小GMV要求 (包含)',
    `max_gmv` DECIMAL(15,2) DEFAULT NULL COMMENT '最大GMV要求 (不包含)',
    `level_order` INT NOT NULL UNIQUE COMMENT '等级顺序，用于排序',

    -- 索引
    -- OPTIMIZATION: `level_order` 有 UNIQUE 约束，自动带索引，无需重复创建。

    -- 数据完整性约束
    CONSTRAINT `chk_commission_rate` CHECK (`commission_rate` >= 0 AND `commission_rate` <= 100),
    CONSTRAINT `chk_gmv_range` CHECK (`min_gmv` IS NULL OR `max_gmv` IS NULL OR `min_gmv` < `max_gmv`), -- OPTIMIZATION: 通常是小于关系，如果包含则用 <=。
    CONSTRAINT `chk_base_salary_positive` CHECK (`base_salary` >= 0) -- OPTIMIZATION: 增加底薪非负约束。
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代理等级表';



-- 3. 用户与代理等级绑定表
CREATE TABLE `user_agent_level` (
    `user_id` BIGINT UNSIGNED NOT NULL PRIMARY KEY COMMENT '用户ID', -- OPTIMIZATION: 主键且为外键，使用 UNSIGNED。
    `level_id` INT UNSIGNED NOT NULL COMMENT '等级ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `assigned_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '分配人ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `assigned_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间', -- OPTIMIZATION: 使用 TIMESTAMP 类型。明确 NOT NULL。

    -- 状态管理
    `effective_date` DATE NOT NULL COMMENT '生效日期', -- OPTIMIZATION: 关键业务日期，应为 NOT NULL。
    `expiry_date` DATE DEFAULT NULL COMMENT '过期日期 (NULL表示永久有效)',

    -- 业务数据
    `current_period_gmv` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '当前考核周期GMV', -- OPTIMIZATION: 明确 NOT NULL。
    `current_period_start` DATE NOT NULL COMMENT '当前考核周期开始日期', -- OPTIMIZATION: 关键业务日期，应为 NOT NULL。
    `current_period_end` DATE NOT NULL COMMENT '当前考核周期结束日期', -- OPTIMIZATION: 关键业务日期，应为 NOT NULL。

    -- 审计字段
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 外键约束
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`level_id`) REFERENCES `agent_levels`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`assigned_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    -- 索引
    INDEX `idx_level_id` (`level_id`),
    INDEX `idx_date_range` (`effective_date`, `expiry_date`) -- OPTIMIZATION: 复合索引，用于快速查找某个时间点有效的用户。
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户代理等级关联表';



-- 4. 等级审核记录表
CREATE TABLE `agent_level_audit` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- OPTIMIZATION: 主键使用 UNSIGNED。
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '申请人用户ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `before_level_id` INT UNSIGNED DEFAULT NULL COMMENT '调整前等级ID', -- OPTIMIZATION: 统一命名为 id 后缀，并使用 UNSIGNED。
    `after_level_id` INT UNSIGNED NOT NULL COMMENT '申请调整后等级ID', -- OPTIMIZATION: 统一命名为 id 后缀，并使用 UNSIGNED。
    `status` ENUM('approved','rejected','pending') NOT NULL DEFAULT 'pending' COMMENT '审核状态', -- OPTIMIZATION: 明确 NOT NULL。
    `audit_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '审核人ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `audit_feedback` TEXT DEFAULT NULL COMMENT '审核反馈或备注',
    `audited_at` TIMESTAMP DEFAULT NULL COMMENT '审核时间', -- OPTIMIZATION: 使用 TIMESTAMP。

    -- 审计字段
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', -- OPTIMIZATION: 增加 updated_at 字段保持一致性。

    -- 外键约束
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`before_level_id`) REFERENCES `agent_levels`(`id`) ON DELETE RESTRICT, -- OPTIMIZATION: 增加 before_level_id 的外键约束。
    FOREIGN KEY (`after_level_id`) REFERENCES `agent_levels`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`audit_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    -- 索引
    INDEX `idx_user_status` (`user_id`, `status`) -- OPTIMIZATION: 复合索引，优化“查询某用户的审核记录”场景。
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='等级变更审核记录表';



-- 5. 客户资源表 (Leads)
CREATE TABLE `customer_leads` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- OPTIMIZATION: 主键使用 UNSIGNED。
    `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '客户手机号 (作为主要标识应 NOT NULL)', -- OPTIMIZATION: 手机号作为核心字段，应为 NOT NULL。
    `wechat` VARCHAR(64) DEFAULT NULL COMMENT '微信号',
    `qq` VARCHAR(64) DEFAULT NULL COMMENT 'QQ号',
    `submitted_by` BIGINT UNSIGNED NOT NULL COMMENT '提交人ID (销售/代理)', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `assigned_sales_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '跟进的销售ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `status` ENUM('new', 'contacted', 'interested', 'closed', 'invalid') NOT NULL DEFAULT 'new' COMMENT '线索状态', -- OPTIMIZATION: 明确 NOT NULL。
    `follow_up_notes` TEXT DEFAULT NULL COMMENT '跟进备注',
    
    -- 审计字段
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 外键约束
    FOREIGN KEY (`submitted_by`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`assigned_sales_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    -- 索引
    -- OPTIMIZATION: `phone` 已有 UNIQUE 约束，自带索引。
    INDEX `idx_sales_status` (`assigned_sales_id`, `status`) -- OPTIMIZATION: 复合索引，核心场景：销售查看自己名下各种状态的线索。
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户资源（线索）表';



-- 6. 商品信息表
CREATE TABLE `products` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- OPTIMIZATION: 主键使用 UNSIGNED。
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `description` TEXT DEFAULT NULL COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `status` ENUM('active', 'inactive') NOT NULL DEFAULT 'active' COMMENT '商品状态: active-上架, inactive-下架', -- OPTIMIZATION: 明确 NOT NULL。
    
    -- 审计字段
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 索引
    INDEX `idx_status` (`status`),
    
    -- 数据完整性约束
    CONSTRAINT `chk_price_positive` CHECK (`price` >= 0) -- OPTIMIZATION: 增加价格非负约束。
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品信息表';



-- 7. 成交记录表
CREATE TABLE `deals` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- OPTIMIZATION: 主键使用 UNSIGNED。
    `customer_lead_id` BIGINT UNSIGNED NOT NULL COMMENT '客户线索ID', -- OPTIMIZATION: 命名更精确，并使用 UNSIGNED。
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `sales_id` BIGINT UNSIGNED NOT NULL COMMENT '成交销售ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `sales_owner_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '成交归属销售ID（系统根据代理关系推导）', -- OPTIMIZATION: 新增成交归属销售 id 字段。
    `deal_amount` DECIMAL(10,2) NOT NULL COMMENT '成交金额', -- OPTIMIZATION: 命名统一为 deal_amount，与 reward_amount 区分。
    `status` ENUM('pending', 'completed', 'refunded') NOT NULL DEFAULT 'completed' COMMENT '状态', -- OPTIMIZATION: 明确 NOT NULL。
    `deal_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '成交时间(业务时间)', -- OPTIMIZATION: 命名统一为 xxx_at，使用 TIMESTAMP。

    -- 审计字段
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(记录时间)', -- OPTIMIZATION: 注释清晰区分业务时间和记录时间。
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', -- OPTIMIZATION: 增加 updated_at 保持一致性。

    -- 外键约束
    FOREIGN KEY (`customer_lead_id`) REFERENCES `customer_leads`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`sales_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`sales_owner_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    -- 索引
    INDEX `idx_sales_status_date` (`sales_id`, `status`, `deal_at`) -- OPTIMIZATION: 强大复合索引，满足“查询某销售某段时间内某种状态的订单”核心需求。
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成交记录表';



-- 8. 佣金记录表
CREATE TABLE `commissions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- OPTIMIZATION: 主键使用 UNSIGNED。
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '佣金获得者ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `deal_id` BIGINT UNSIGNED NOT NULL COMMENT '关联的成交记录ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `commission_level` ENUM('direct', 'indirect') NOT NULL COMMENT '佣金层级', -- OPTIMIZATION: 命名统一为xxx_level，明确 NOT NULL。
    `commission_rate` DECIMAL(5,2) NOT NULL COMMENT '计算时使用的佣金比例(%)', -- OPTIMIZATION: 命名统一为 xxx_rate。
    `commission_amount` DECIMAL(10,2) NOT NULL COMMENT '佣金金额', -- OPTIMIZATION: 命名统一为 xxx_amount。
    `settlement_month` VARCHAR(7) NOT NULL COMMENT '佣金结算月份 (格式: YYYY-MM)',
    `status` ENUM('pending', 'paid', 'cancelled') NOT NULL DEFAULT 'pending' COMMENT '状态', -- OPTIMIZATION: 明确 NOT NULL。

    -- 审计字段
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',  -- OPTIMIZATION: 增加 updated_at 保持一致性。
    
    -- 外键约束
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`deal_id`) REFERENCES `deals`(`id`) ON DELETE RESTRICT,

    -- 数据完整性约束
    CONSTRAINT `chk_commission_amount_positive` CHECK (`commission_amount` >= 0), -- OPTIMIZATION: 增加佣金非负约束。

    -- 索引
    INDEX `idx_user_status_month` (`user_id`, `status`, `settlement_month`) -- OPTIMIZATION: 核心复合索引，用于“查询某用户某月的待结算佣金”。
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='佣金记录表';



-- 9. 推广任务表
CREATE TABLE `promotions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- OPTIMIZATION: 主键使用 UNSIGNED。
    `agent_id` BIGINT UNSIGNED NOT NULL COMMENT '代理ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `platform` ENUM('douyin', 'xiaohongshu', 'kuaishou') NOT NULL COMMENT '推广平台', -- OPTIMIZATION: 明确 NOT NULL。
    `task_type` ENUM('text', 'video', 'real_person') NOT NULL DEFAULT 'text' COMMENT '任务类型', -- OPTIMIZATION: 命名为 task_type 避免与关键字 type 冲突，明确 NOT NULL。
    `url` TEXT DEFAULT NULL COMMENT '推广作品链接',
    `status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '审核状态', -- OPTIMIZATION: 明确 NOT NULL。
    `audit_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '审核人ID', -- OPTIMIZATION: 外键使用 UNSIGNED。
    `audit_reason` TEXT DEFAULT NULL COMMENT '审核原因或反馈',
    `reward_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '任务奖励金额', -- OPTIMIZATION: 明确 NOT NULL。
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `audited_at` TIMESTAMP DEFAULT NULL COMMENT '审核时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',  -- OPTIMIZATION: 增加 updated_at 保持一致性。

    -- 外键约束
    FOREIGN KEY (`agent_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`audit_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    -- 数据完整性约束
    CONSTRAINT `chk_reward_amount_positive` CHECK (`reward_amount` >= 0), -- OPTIMIZATION: 增加奖励金额非负约束。

    -- 索引
    INDEX `idx_agent_status` (`agent_id`, `status`) -- OPTIMIZATION: 复合索引，用于代理查询自己的推广任务。
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推广任务表';

-- 10.等级历史记录表
CREATE TABLE `agent_level_history` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `level_id` INT UNSIGNED NOT NULL,
    `assigned_by` BIGINT UNSIGNED DEFAULT NULL,
    `change_type` ENUM('manual', 'audit', 'system') NOT NULL DEFAULT 'manual',
    `change_reason` TEXT DEFAULT NULL,
    `assigned_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`level_id`) REFERENCES `agent_levels`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`assigned_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_user_time` (`user_id`, `assigned_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代理等级历史记录表';

-- ========================================
-- 初始化数据
-- ========================================

-- 插入代理等级初始数据
INSERT INTO `agent_levels` (`name`, `commission_rate`, `base_salary`, `min_gmv`, `max_gmv`, `level_order`) VALUES
('SV1', 5.00, 0.00, 0.00, 10000.00, 1),
('SV2', 6.00, 1000.00, 10000.00, 20000.00, 2),
('SV3', 7.00, 2000.00, 20000.00, 40000.00, 3),
('SV4', 8.00, 3000.00, 40000.00, 60000.00, 4),
('SV5', 9.00, 4000.00, 60000.00, 80000.00, 5),
('SV6', 10.00, 5000.00, 80000.00, NULL, 6);

-- 创建超级管理员账户
-- 密码为 'password' 的 bcrypt 哈希值
INSERT INTO `users` (`phone`, `password`, `role`, `status`) VALUES
('13800138000', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'super_admin', 'active');
