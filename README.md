# Distribution-System
# 分销系统（Distribution System）

[![Language](https://img.shields.io/badge/Language-Java-blue.svg)](https://www.java.com)
[![Framework](https://img.shields.io/badge/Framework-Spring%20Cloud-green.svg)](https://spring.io/projects/spring-cloud)
[![Frontend](https://img.shields.io/badge/Frontend-Vue%20%7C%20Mini%20Program-orange)](https://vuejs.org/)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20Redis-yellow.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](https://opensource.org/licenses/MIT)

## 📌 项目概览

本系统是一个基于 **Spring Boot + Spring Cloud** 微服务架构构建的现代化分销业务平台。支持多级分销、客资管理、自动化佣金结算与推广任务激励，覆盖 **Web 管理后台** 与 **微信小程序** 双端。

系统采用异步消息驱动、容器化部署，具备高扩展的特性，可支持未来向 SaaS 平台化演进。

---

## ✨ 核心业务功能

-   **用户与角色管理**：支持销售、代理、组长、总监、管理员等多级角色。
-   **分销关系链**：通过邀请码建立上下级绑定关系，支持三级分销。
-   **客资管理**：代理/销售提交客资，系统通过手机号/微信号确保唯一性，实现“先到先得”。
-   **成交与跟进**：销售录入成交记录，并标记客户跟进状态。
-   **佣金自动结算**：成交后通过消息队列异步触发佣金计算，根据代理等级和分销层级自动分发。
-   **代理等级体系**：支持 V1-V6 等级，不同等级对应不同的底薪和提成比例。
-   **推广任务激励**：代理人可提交推广任务（如短视频链接），审核通过后自动发放奖励。
-   **客户资源继承**：支持销售/代理离职时，其名下客户和团队资源平滑交接。

---

## ⚙️ 技术架构

系统采用微服务架构，各模块职责清晰、解耦，通过 API 网关统一对外提供服务。

-   **架构模式**：微服务 + API 网关 + 消息驱动
-   **异步通信**：通过 RabbitMQ 实现服务间的异步任务处理（如佣金计算、任务审核通知）。
-   **统一鉴权**：Spring Cloud Gateway + JWT 实现全局统一的认证与授权。

### 技术栈总览

| 层级         | 技术组件                                        |
|--------------|-------------------------------------------------|
| 微服务架构   | Spring Boot + Spring Cloud + Nacos              |
| 消息驱动     | RabbitMQ                                        |
| 鉴权系统     | JWT + RBAC + 数据权限（上下级关系）             |
| 数据存储     | MySQL 8 + Redis                                |
| 前端后台     | Vue3 + Vite + Element Plus                      |
| 小程序端     | 微信原生小程序                                  |
| 接口文档     | Swagger / Springdoc OpenAPI                     |
| 部署系统     | Docker + Docker Compose + Jenkins + K8s (ACK)   |

## ☕ 分销系统 · 微服务项目骨架结构

```bash
distribution-system/
├── README.md
├── pom.xml                         # 父级项目 POM（聚合模块依赖）
├── docker-compose.yml             # 容器编排（包含服务 + MySQL + Redis）
├── config/                        # 公共配置中心（如 Nacos 配置文件）
│
├── common/                        # 公共模块（工具类、通用响应、JWT 处理）
│   └── src/
│       └── main/java/com/example/common/
│           ├── dto/
│           ├── utils/
│           ├── enums/
│           ├── constants/
│           └── exception/
│
├── gateway/                       # API 网关（Spring Cloud Gateway）
│   └── src/
│       └── main/java/com/example/gateway/
│           ├── config/
│           └── filters/
│
├── auth-service/                  # 用户注册、登录、权限管理服务
│   └── src/
│       └── main/java/com/example/auth/
│           ├── controller/
│           ├── service/
│           ├── entity/
│           ├── repository/
│           └── config/
│
├── lead-service/                  # 客资管理服务
│   └── src/
│       └── main/java/com/example/lead/
│           ├── controller/
│           ├── service/
│           ├── entity/
│           ├── repository/
│           └── dto/
│
├── deal-service/                  # 成交与佣金计算服务
│   └── src/
│       └── main/java/com/example/deal/
│           ├── controller/
│           ├── service/
│           ├── entity/
│           ├── repository/
│           └── event/            # 异步事件处理（基于 MQ）
│
├── product-service/               # 商品管理服务
│   └── src/
│       └── main/java/com/example/product/
│           ├── controller/
│           ├── service/
│           ├── entity/
│           └── repository/
│
├── promotion-service/             # 推广任务上传 + 审核模块
│   └── src/
│       └── main/java/com/example/promotion/
│           ├── controller/
│           ├── service/
│           ├── entity/
│           ├── audit/
│           └── repository/
│
├── level-service/                 # 代理等级评定服务
│   └── src/
│       └── main/java/com/example/level/
│           ├── controller/
│           ├── service/
│           ├── entity/
│           └── repository/
│
├── api-docs/                      # Swagger API 文档聚合模块
│   └── config/
│       └── swagger-config.yaml
│
├── logs/                          # 日志目录（挂载到容器）
└── scripts/                       # 启动脚本、DB 初始化 SQL、测试脚本等



🔧 每个服务都是独立 Spring Boot 应用，均可单独启动、打包、部署
🔁 服务注册发现通过 Nacos 或 Eureka
🧩 MQ（如 RabbitMQ）用于佣金分发、推广任务通知等异步处理
🔐 JWT + Gateway 实现全系统统一鉴权
🐳 可通过 docker-compose up 一键部署所有模块（含数据库、Redis）