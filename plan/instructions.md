# 分销系统开发项目说明文档（完整版）

## 📌 项目概览
本系统基于 Spring Boot + Spring Cloud 微服务架构，构建一个支持多级分销、客资管理、佣金结算与推广任务激励的业务平台，覆盖 Web 管理后台 + 微信小程序双端。系统采用 RBAC 权限、异步消息驱动、容器化部署，支持后续 SaaS 平台化扩展。

---

## 📦 微服务模块结构

| 模块名称              | 服务名称             | 功能说明                                                                 |
|-----------------------|----------------------|--------------------------------------------------------------------------|
| 用户中心服务          | user-service         | 注册登录、角色管理、上下级关系、权限控制（JWT + RBAC）                  |
| 分销代理服务          | agent-service        | 代理等级管理、团队结构维护、提成配置、业绩归属、三级分销关系链计算       |
| 客户与客资服务        | customer-service     | 客资录入、客户唯一性判定、客户归属、跟进记录                             |
| 商品与成交服务        | deal-service         | 商品录入、生成推广链接、成交记录、链接归属解析、成交确认机制             |
| 推广任务服务          | promotion-service    | 推广任务上传、平台识别、机审+人工审核、奖励统计                           |
| 佣金结算服务          | commission-service   | 提成结算逻辑（多级分销提成计算）、底薪逻辑、每月结算调度任务              |
| 审核与审批服务        | audit-service        | 等级审核、退单流程审批、佣金确认与驳回                                   |
| 运营管理后台          | admin-web            | 管理员后台界面（商品、权限、等级配置等）                                 |
| 分销代理小程序端      | agent-miniapp        | 代理注册、提交客资、推广任务上传、查看佣金与等级                         |
| 销售人员工作台（Web） | sales-web            | 销售查看代理数据、客资状态、生成推广链接、成交录入                       |

---

## 🧠 技术栈汇总

| 层级        | 技术组件                                        |
|-------------|-------------------------------------------------|
| 微服务架构  | Spring Boot + Spring Cloud + Nacos             |
| 消息驱动    | RabbitMQ                                        |
| 鉴权系统    | JWT + RBAC + 数据权限（上下级关系）             |
| 数据存储    | MySQL + Redis                                   |
| 前端后台    | Vue3 + Vite + Element Plus                      |
| 小程序端    | 微信原生小程序（支持微信支付、Storage 缓存）    |
| 接口文档    | Swagger / Springdoc OpenAPI                     |
| 部署系统    | Docker + Jenkins + GitHub Actions + ACK + ACR  |
| 日志监控    | ELK / Loki + Prometheus + Grafana（可选）       |

---

## 🚀 启动方式

1. 构建所有服务：
   mvn clean package

2. 启动所有服务：
   docker-compose up --build -d

3. 访问接口文档：
   http://localhost:8090

---

## 🔑 核心业务流程

- 销售注册 → 生成邀请码邀请代理
- 代理注册 → 提交客资（手机号/微信）
- 销售查看并跟进客资 → 成交后生成记录
- 系统按三级分销 + 代理等级计算佣金
- 推广任务上传审核 → 系统识别、奖励自动生成
- 后台支持代理等级、佣金比例、审批流程配置
- 客户与代理归属支持手动转移、继承与权限控制

---

## 📎 数据规范与权限要求

- 客户手机号为唯一标识，重复提交自动拒绝
- 每个销售生成专属推广链接，成交记录归属绑定
- 成交记录仅超级管理员可修改，支持退单审批
- 佣金按月结算（不跨月累计），支持底薪与提成结合
- 等级需管理员审核，不自动升降

---

## 🛠️ 项目目录结构（示意）

distribution-system/
├── docker-compose.yml
├── config/
├── scripts/
├── common/
├── gateway/
├── auth-service/
├── lead-service/
├── deal-service/
├── product-service/
├── promotion-service/
├── level-service/
├── api-docs/
├── admin-web/
└── agent-miniapp/

---

## 🔌 外部对接能力（预留）

| 模块       | 功能                                 |
|------------|--------------------------------------|
| 财务系统   | 佣金结算与退款通知、自动对账         |
| 微信支付   | 小程序下单与付款                     |
| 云短信/邮件 | 验证码下发、异常通知                 |
| 对账系统   | 成交记录与实付金额核对               |

---

## 📑 接口说明（Swagger 风格示例）

POST /api/auth/register  
POST /api/auth/login  
POST /api/leads/submit  
GET  /api/leads/mine  
POST /api/deals/create  
GET  /api/deals/mine  
GET  /api/commissions/mine  
POST /api/promotions/upload  
GET  /api/promotions/mine  

> 所有接口统一携带 JWT Token：  
> Authorization: Bearer {token}

---

## 🧾 数据库结构说明（关键表）

users  
agent_levels  
user_agent_level  
customer_leads  
deals  
commissions  
promotions  
agent_level_audit

---

## 📱 小程序页面结构

pages/
├── auth/
├── home/
├── leads/
├── deals/
├── commission/
├── promotion/
├── profile/
├── components/
└── utils/

---

✅ 本文档整合系统架构、模块划分、部署方式、接口规范、数据库结构与前后端页面草图，适用于：
- ChatGPT 项目设置说明（project.instructions）
- GitHub 项目 README.md
- 团队协作 Onboarding 文档

建议统一上传至项目根目录供查阅。
