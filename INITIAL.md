## 🎯 初始功能需求 (INITIAL.md)

## 功能需求:

- 基于 Spring Boot + Spring Cloud 微服务架构的分销系统
- 支持多级代理分销体系，三级佣金分成机制
- Web 管理后台 + 微信小程序双端支持
- 客资管理、成交记录、推广任务、佣金结算全链路业务
- 支持阿里云 ACK (Kubernetes) 容器化部署

## 核心模块:

在 `examples/` 文件夹中，有完整的微服务架构示例供你参考理解项目结构和最佳实践。

- `examples/microservices/` - 微服务架构最佳实践，包括服务发现、配置中心、API 网关
- `examples/security/` - JWT + RBAC 权限控制实现
- `examples/database/` - MySQL + Redis 数据库设计和连接池配置
- `examples/deployment/` - Docker + Kubernetes 部署配置

不要直接复制这些示例，它们用于不同的项目。但将其作为灵感和最佳实践的参考。

## 技术文档:

Spring Boot 文档: https://spring.io/projects/spring-boot 

Spring Cloud 文档: https://spring.io/projects/spring-cloud 

MyBatis 文档: https://mybatis.org/mybatis-3/

## 其他考虑事项:

- 包含 .env.example，README 包含设置说明，包括如何配置数据库连接和微服务注册中心
- 包含项目结构说明在 README 中
- 开发环境已经配置了必要的依赖
- 使用 Maven 进行依赖管理和项目构建
- 实现统一的异常处理和响应格式
- 预留财务系统接口、支付系统接口、第三方服务接口

## 项目目录结构:

```
distribution-system/
├── README.md
├── pom.xml                         # 父级项目 POM
├── docker-compose.yml             # 容器编排
├── common/                        # 公共模块
├── gateway/                       # API 网关
├── auth-service/                  # 认证服务
├── lead-service/                  # 客资管理服务
├── deal-service/                  # 成交管理服务
├── product-service/               # 商品管理服务
├── promotion-service/             # 推广任务服务
├── commission-service/            # 佣金结算服务
├── audit-service/                 # 审核服务
├── admin-web/                     # 管理后台
├── agent-miniapp/                 # 代理小程序
└── scripts/                       # 部署脚本
```

------

## 📁 示例参考 (examples/)

```
examples/
├── README.md                      # 示例说明文档
├── microservices/
│   ├── gateway-config.yml         # API 网关配置示例
│   ├── nacos-config.yml          # 服务注册发现配置
│   └── service-template/          # 微服务模板结构
├── security/
│   ├── JwtUtils.java              # JWT 工具类示例
│   ├── AuthInterceptor.java       # 权限拦截器示例
│   └── RolePermission.java        # 角色权限枚举
├── database/
│   ├── init.sql                   # 数据库初始化脚本
│   ├── mybatis-config.xml         # MyBatis 配置示例
│   └── RedisConfig.java           # Redis 配置类
└── deployment/
    ├── Dockerfile                 # 容器化构建文件
    ├── k8s-deployment.yaml        # Kubernetes 部署配置
    └── jenkins-pipeline.groovy    # CI/CD 流水线配置
```

## 业务流程示例:

1. **用户注册与认证**: 手机号验证码注册 → JWT Token 生成 → 角色权限分配
2. **客资管理流程**: 代理提交客户信息 → 系统去重验证 → 分配给销售跟进
3. **成交记录流程**: 销售录入成交信息 → 系统生成推广链接 → 自动计算佣金
4. **推广任务流程**: 代理上传推广内容 → 平台识别与审核 → 奖励发放
5. **佣金结算流程**: 按月统计业绩 → 计算多级分销提成 → 生成结算报表

## 📋 开发检查清单
开发新功能时，请检查：

- [ ] 是否遵循了微服务拆分原则？
- [ ] 是否实现了适当的权限控制？
- [ ] 是否设计了合理的数据库表结构？
- [ ] 是否编写了完整的API文档？
- [ ]  是否添加了相应的单元测试？
- [ ]  是否遵循了Git提交规范？
