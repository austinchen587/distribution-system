-- 测试环境初始数据
-- Test Data for Data Access Layer Tests

-- 插入测试用户数据
INSERT INTO users (id, username, email, phone, password, role, status, commission_rate, parent_id, created_at, updated_at) VALUES
(1, 'admin', 'admin@example.com', '13800000001', 'encrypted_admin_password', 'super_admin', 'ACTIVE', 0.0000, NULL, NOW(), NOW()),
(2, 'director1', 'director1@example.com', '13800000002', 'encrypted_director_password', 'director', 'ACTIVE', 0.0500, 1, NOW(), NOW()),
(3, 'leader1', 'leader1@example.com', '13800000003', 'encrypted_leader_password', 'leader', 'ACTIVE', 0.0800, 2, NOW(), NOW()),
(4, 'agent1', 'agent1@example.com', '13800000004', 'encrypted_agent_password', 'agent', 'ACTIVE', 0.1000, 3, NOW(), NOW()),
(5, 'agent2', 'agent2@example.com', '13800000005', 'encrypted_agent_password', 'agent', 'INACTIVE', 0.1200, 3, NOW(), NOW()),
(6, 'sales1', 'sales1@example.com', '13800000006', 'encrypted_sales_password', 'sales', 'ACTIVE', 0.0600, 2, NOW(), NOW());

-- 插入代理级别数据
INSERT INTO agent_levels (id, level_name, min_gmv, max_gmv, commission_rate, description, created_at, updated_at) VALUES
(1, '新手代理', 0.00, 9999.99, 0.0500, '新手代理级别', NOW(), NOW()),
(2, '初级代理', 10000.00, 49999.99, 0.0800, '初级代理级别', NOW(), NOW()),
(3, '中级代理', 50000.00, 99999.99, 0.1000, '中级代理级别', NOW(), NOW()),
(4, '高级代理', 100000.00, 499999.99, 0.1200, '高级代理级别', NOW(), NOW()),
(5, '顶级代理', 500000.00, NULL, 0.1500, '顶级代理级别', NOW(), NOW());

-- 插入商品信息数据
INSERT INTO products (id, product_name, description, price, category, status, created_at, updated_at) VALUES
(1, '理财产品A', '低风险理财产品', 10000.00, '理财', 'ACTIVE', NOW(), NOW()),
(2, '理财产品B', '中等风险理财产品', 50000.00, '理财', 'ACTIVE', NOW(), NOW()),
(3, '保险产品A', '人寿保险产品', 5000.00, '保险', 'ACTIVE', NOW(), NOW()),
(4, '保险产品B', '意外险产品', 1200.00, '保险', 'ACTIVE', NOW(), NOW()),
(5, '投资产品A', '高收益投资产品', 100000.00, '投资', 'INACTIVE', NOW(), NOW());

-- 插入客户资源数据
INSERT INTO customer_leads (id, agent_id, customer_name, phone, description, lead_status, follow_up_date, audit_status, created_at, updated_at) VALUES
(1, 4, '张三', '13900000001', '有投资理财需求', 'NEW', DATE_ADD(NOW(), INTERVAL 1 DAY), 'PENDING', NOW(), NOW()),
(2, 4, '李四', '13900000002', '咨询保险产品', 'CONTACTED', DATE_ADD(NOW(), INTERVAL 2 DAY), 'APPROVED', NOW(), NOW()),
(3, 4, '王五', '13900000003', '高净值客户', 'INTERESTED', DATE_ADD(NOW(), INTERVAL 3 DAY), 'APPROVED', NOW(), NOW()),
(4, 5, '赵六', '13900000004', '小额理财需求', 'NEW', DATE_ADD(NOW(), INTERVAL 1 DAY), 'PENDING', NOW(), NOW()),
(5, 6, '钱七', '13900000005', '企业客户', 'CONTACTED', DATE_ADD(NOW(), INTERVAL 5 DAY), 'APPROVED', NOW(), NOW());

-- 插入推广任务数据
INSERT INTO promotions (id, agent_id, title, description, platform, content_url, tags, expected_reward, actual_reward, audit_status, submitted_at, updated_at) VALUES
(1, 4, '理财产品推广', '在朋友圈推广理财产品A', '微信朋友圈', 'https://example.com/promotion1', '理财,投资', 500.00, NULL, 'PENDING', NOW(), NOW()),
(2, 4, '保险产品宣传', '线下活动推广保险产品', '线下活动', NULL, '保险,健康', 800.00, 600.00, 'APPROVED', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(3, 5, '社交媒体推广', '在微博推广投资产品', '微博', 'https://weibo.com/promotion3', '投资,理财', 300.00, NULL, 'REJECTED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW());

-- 插入成交记录数据
INSERT INTO deals (id, customer_lead_id, product_id, sales_id, sales_owner_id, deal_amount, status, deal_at, created_at, updated_at) VALUES
(1, 2, 1, 6, 4, 10000.00, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NOW()),
(2, 3, 2, 6, 4, 50000.00, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), NOW()),
(3, 5, 3, 6, 6, 5000.00, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), NOW());

-- 插入佣金记录数据
INSERT INTO commissions (id, user_id, deal_id, commission_level, commission_rate, commission_amount, settlement_month, status, created_at, updated_at) VALUES
(1, 4, 1, 'DIRECT', 0.1000, 1000.00, '2025-08', 'PENDING', NOW(), NOW()),
(2, 3, 1, 'INDIRECT', 0.0300, 300.00, '2025-08', 'PENDING', NOW(), NOW()),
(3, 2, 1, 'OVERRIDE', 0.0200, 200.00, '2025-08', 'PENDING', NOW(), NOW()),
(4, 4, 2, 'DIRECT', 0.1000, 5000.00, '2025-08', 'SETTLED', NOW(), NOW()),
(5, 6, 3, 'DIRECT', 0.0600, 300.00, '2025-08', 'SETTLED', NOW(), NOW());

-- 插入邀请码数据
INSERT INTO invitation_codes (id, user_id, code, target_role, status, usage_count, max_uses, expires_at, created_at, updated_at) VALUES
(1, 2, 'INVITE001', 'leader', 'ACTIVE', 0, 5, DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
(2, 3, 'INVITE002', 'agent', 'ACTIVE', 2, 10, DATE_ADD(NOW(), INTERVAL 60 DAY), NOW(), NOW()),
(3, 4, 'INVITE003', 'agent', 'USED', 1, 1, DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
(4, 2, 'INVITE004', 'sales', 'EXPIRED', 0, 3, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NOW());