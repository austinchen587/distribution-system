-- 1. 用户表（含销售和代理）
CREATE TABLE users (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
password VARCHAR(255) NOT NULL COMMENT '密码',
role ENUM('super_admin', 'director', 'leader', 'sales', 'agent') NOT NULL COMMENT '角色',
inviter_id BIGINT DEFAULT NULL COMMENT '邀请人ID',

-- 状态相关
status ENUM('active', 'banned') DEFAULT 'active' COMMENT '用户状态',

-- 业务数据
total_gmv DECIMAL(15,2) DEFAULT 0.00 COMMENT '总GMV',

-- 审计字段
created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

-- 外键约束
FOREIGN KEY (inviter_id) REFERENCES users(id) ON DELETE SET NULL,

-- 索引
INDEX idx_phone (phone),
INDEX idx_role (role),
INDEX idx_inviter_id (inviter_id),
INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';



-- 2. 代理等级表（SV1 - SV6）
CREATE TABLE agent_levels (
id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(10) NOT NULL COMMENT '等级名称',
commission_rate DECIMAL(5,2) NOT NULL COMMENT '佣金比例',
min_gmv DECIMAL(15,2) DEFAULT NULL COMMENT '最小GMV要求',
max_gmv DECIMAL(15,2) DEFAULT NULL COMMENT '最大GMV要求',
level_order INT NOT NULL UNIQUE COMMENT '等级顺序',

-- 索引
INDEX idx_level_order (level_order),
INDEX idx_is_active (is_active),

-- 数据完整性约束
CONSTRAINT chk_commission_rate CHECK (commission_rate >= 0 AND commission_rate <= 100),
CONSTRAINT chk_gmv_range CHECK (min_gmv IS NULL OR max_gmv IS NULL OR min_gmv <= max_gmv)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代理等级表';



-- 3. 用户与代理等级绑定表
CREATE TABLE user_agent_level (
user_id BIGINT PRIMARY KEY COMMENT '用户ID',
level_id INT NOT NULL COMMENT '等级ID',
assigned_by BIGINT DEFAULT NULL COMMENT '分配人ID',
assigned_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',

-- 状态管理
effective_date DATE DEFAULT NULL COMMENT '生效日期',
expiry_date DATE DEFAULT NULL COMMENT '过期日期',

-- 业务数据
current_period_gmv DECIMAL(15,2) DEFAULT 0.00 COMMENT '当前周期GMV',
current_period_start DATE DEFAULT NULL COMMENT '当前周期开始日期',
current_period_end DATE DEFAULT NULL COMMENT '当前周期结束日期',

-- 审计字段
created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

-- 外键约束
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (level_id) REFERENCES agent_levels(id) ON DELETE RESTRICT,
FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,

-- 索引
INDEX idx_level_id (level_id),
INDEX idx_assigned_by (assigned_by),
INDEX idx_status (status),
INDEX idx_effective_date (effective_date),
INDEX idx_expiry_date (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户代理等级关联表';



-- 4. 等级审核记录表
CREATE TABLE agent_level_audit (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
user_id BIGINT NOT NULL COMMENT '用户ID',
before_level INT DEFAULT NULL COMMENT '之前等级',
after_level INT NOT NULL COMMENT '之后等级',
status ENUM('approved','rejected','pending') DEFAULT 'pending' COMMENT '审核状态',
audit_by BIGINT DEFAULT NULL COMMENT '审核人',
audit_feedback TEXT DEFAULT NULL COMMENT '审核反馈',
audited_at DATETIME DEFAULT NULL COMMENT '审核时间',
created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

-- 外键约束
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (after_level) REFERENCES agent_levels(id) ON DELETE RESTRICT,
FOREIGN KEY (audit_by) REFERENCES users(id) ON DELETE SET NULL,

-- 索引
INDEX idx_user_id (user_id),
INDEX idx_status (status),
INDEX idx_audit_by (audit_by),
INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='等级审核记录表';



-- 5. 客户资源表
CREATE TABLE customer_leads (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
phone VARCHAR(20) UNIQUE COMMENT '客户手机号',
wechat VARCHAR(64) DEFAULT NULL COMMENT '微信号',
qq VARCHAR(64) DEFAULT NULL COMMENT 'QQ号',
submitted_by BIGINT NOT NULL COMMENT '提交人ID',
assigned_sales_id BIGINT DEFAULT NULL COMMENT '分配销售ID',
status ENUM('new', 'contacted', 'interested', 'closed', 'invalid') DEFAULT 'new' COMMENT '状态',
follow_up_notes TEXT DEFAULT NULL COMMENT '跟进备注',
created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

-- 外键约束
FOREIGN KEY (submitted_by) REFERENCES users(id) ON DELETE RESTRICT,
FOREIGN KEY (assigned_sales_id) REFERENCES users(id) ON DELETE SET NULL,

-- 索引
INDEX idx_phone (phone),
INDEX idx_submitted_by (submitted_by),
INDEX idx_assigned_sales_id (assigned_sales_id),
INDEX idx_status (status),
INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户资源表';



-- 6. 商品信息表
CREATE TABLE products (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL COMMENT '商品名称',
description TEXT DEFAULT NULL COMMENT '商品描述',
price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '商品状态',
created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

-- 索引
INDEX idx_status (status),
INDEX idx_price (price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品信息表';



-- 7. 成交记录表
CREATE TABLE deals (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
customer_id BIGINT NOT NULL COMMENT '客户ID',
product_id BIGINT NOT NULL COMMENT '商品ID',
sales_id BIGINT NOT NULL COMMENT '销售ID',
amount DECIMAL(10,2) NOT NULL COMMENT '成交金额',
status ENUM('pending', 'completed', 'refunded') DEFAULT 'completed' COMMENT '状态',
deal_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '成交时间',
created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

-- 外键约束
FOREIGN KEY (customer_id) REFERENCES customer_leads(id) ON DELETE RESTRICT,
FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
FOREIGN KEY (sales_id) REFERENCES users(id) ON DELETE RESTRICT,

-- 索引
INDEX idx_customer_id (customer_id),
INDEX idx_product_id (product_id),
INDEX idx_sales_id (sales_id),
INDEX idx_status (status),
INDEX idx_deal_date (deal_date),
INDEX idx_amount (amount)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成交记录表';



-- 8. 佣金记录表
CREATE TABLE commissions (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
user_id BIGINT NOT NULL COMMENT '用户ID',
deal_id BIGINT NOT NULL COMMENT '成交记录ID',
level ENUM('direct', 'indirect') NOT NULL COMMENT '佣金层级',
rate DECIMAL(5,2) NOT NULL COMMENT '佣金比例',
amount DECIMAL(10,2) NOT NULL COMMENT '佣金金额',
status ENUM('pending', 'paid', 'cancelled') DEFAULT 'pending' COMMENT '状态',
created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

-- 外键约束
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
FOREIGN KEY (deal_id) REFERENCES deals(id) ON DELETE RESTRICT,

-- 索引
INDEX idx_user_id (user_id),
INDEX idx_deal_id (deal_id),
INDEX idx_status (status),
INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金记录表';



-- 9. 推广任务表
CREATE TABLE promotions (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
agent_id BIGINT NOT NULL COMMENT '代理ID',
platform ENUM('douyin', 'xiaohongshu', 'kuaishou') NOT NULL COMMENT '平台',
type ENUM('text', 'video', 'real_person') DEFAULT 'text' COMMENT '类型',
url TEXT DEFAULT NULL COMMENT '推广链接',
status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '状态',
audit_by BIGINT DEFAULT NULL COMMENT '审核人',
audit_reason TEXT DEFAULT NULL COMMENT '审核原因',
reward_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '奖励金额',
created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
audited_at DATETIME DEFAULT NULL COMMENT '审核时间',

-- 外键约束
FOREIGN KEY (agent_id) REFERENCES users(id) ON DELETE RESTRICT,
FOREIGN KEY (audit_by) REFERENCES users(id) ON DELETE SET NULL,

-- 索引
INDEX idx_agent_id (agent_id),
INDEX idx_platform (platform),
INDEX idx_status (status),
INDEX idx_audit_by (audit_by),
INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广任务表';

-- ========================================
-- 初始化数据
-- ========================================

-- 插入代理等级初始数据
INSERT INTO agent_levels (name, commission_rate, min_gmv, max_gmv, level_order) VALUES
('SV1', 5.00, 0.00, 9999.99, 1),
('SV2', 6.00, 10000.00, 19999.99, 2),
('SV3', 7.00, 20000.00, 39999.99, 3),
('SV4', 8.00, 400000.00, 59999.99, 4),
('SV5', 9.00, 600000.00, 79999.99, 5),
('SV6', 10.00, 800000.00, NULL, 6);

-- 创建超级管理员账户
INSERT INTO users (phone, password, role, status) VALUES
('13800138000', '10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'super_admin', 'active');