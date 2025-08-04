-- 测试环境数据库表结构定义
-- Test Database Schema for Data Access Layer Tests

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    commission_rate DECIMAL(5,4) DEFAULT 0.0000,
    parent_id BIGINT,
    last_login_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_email (email),
    INDEX idx_users_phone (phone),
    INDEX idx_users_role (role),
    INDEX idx_users_status (status),
    INDEX idx_users_parent_id (parent_id)
);

-- 代理级别表
CREATE TABLE IF NOT EXISTS agent_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL UNIQUE,
    min_gmv DECIMAL(15,2) NOT NULL,
    max_gmv DECIMAL(15,2),
    commission_rate DECIMAL(5,4) NOT NULL,
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_agent_levels_gmv (min_gmv, max_gmv)
);

-- 客户资源表
CREATE TABLE IF NOT EXISTS customer_leads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    customer_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    description TEXT,
    lead_status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    follow_up_date DATETIME,
    audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_leads_agent_id (agent_id),
    INDEX idx_customer_leads_phone (phone),
    INDEX idx_customer_leads_status (lead_status),
    INDEX idx_customer_leads_audit_status (audit_status),
    INDEX idx_customer_leads_follow_up (follow_up_date),
    FOREIGN KEY (agent_id) REFERENCES users(id)
);

-- 商品信息表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_products_name (product_name),
    INDEX idx_products_category (category),
    INDEX idx_products_status (status),
    INDEX idx_products_price (price)
);

-- 推广任务表
CREATE TABLE IF NOT EXISTS promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    platform VARCHAR(50),
    content_url VARCHAR(500),
    tags VARCHAR(200),
    expected_reward DECIMAL(10,2),
    actual_reward DECIMAL(10,2),
    audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_promotions_agent_id (agent_id),
    INDEX idx_promotions_status (audit_status),
    INDEX idx_promotions_platform (platform),
    INDEX idx_promotions_submitted (submitted_at),
    FOREIGN KEY (agent_id) REFERENCES users(id)
);

-- 成交记录表
CREATE TABLE IF NOT EXISTS deals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_lead_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    sales_id BIGINT NOT NULL,
    sales_owner_id BIGINT,
    deal_amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    deal_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_deals_customer_lead (customer_lead_id),
    INDEX idx_deals_product (product_id),
    INDEX idx_deals_sales (sales_id),
    INDEX idx_deals_owner (sales_owner_id),
    INDEX idx_deals_amount (deal_amount),
    INDEX idx_deals_status (status),
    INDEX idx_deals_date (deal_at),
    FOREIGN KEY (customer_lead_id) REFERENCES customer_leads(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (sales_id) REFERENCES users(id),
    FOREIGN KEY (sales_owner_id) REFERENCES users(id)
);

-- 佣金记录表
CREATE TABLE IF NOT EXISTS commissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    deal_id BIGINT NOT NULL,
    commission_level VARCHAR(20) NOT NULL,
    commission_rate DECIMAL(5,4) NOT NULL,
    commission_amount DECIMAL(15,2) NOT NULL,
    settlement_month VARCHAR(7) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_commissions_user (user_id),
    INDEX idx_commissions_deal (deal_id),
    INDEX idx_commissions_level (commission_level),
    INDEX idx_commissions_month (settlement_month),
    INDEX idx_commissions_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (deal_id) REFERENCES deals(id)
);

-- 邀请码管理表
CREATE TABLE IF NOT EXISTS invitation_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    target_role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    usage_count INT NOT NULL DEFAULT 0,
    max_uses INT NOT NULL DEFAULT 1,
    expires_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_invitation_codes_user (user_id),
    INDEX idx_invitation_codes_code (code),
    INDEX idx_invitation_codes_status (status),
    INDEX idx_invitation_codes_expires (expires_at),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 数据操作日志表
CREATE TABLE IF NOT EXISTS data_operation_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(50) NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    operation_type VARCHAR(20) NOT NULL,
    method_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    execute_time_ms BIGINT NOT NULL,
    description VARCHAR(200),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_data_logs_service (service_name),
    INDEX idx_data_logs_table (table_name),
    INDEX idx_data_logs_operation (operation_type),
    INDEX idx_data_logs_status (status),
    INDEX idx_data_logs_created (created_at)
);