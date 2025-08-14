# User Service Event Layer

## 概述

User Service Event Layer是基于事件驱动架构的用户服务事件处理层，负责处理用户生命周期中的各种领域事件。该层实现了发布-订阅模式，通过RabbitMQ消息队列实现服务间的异步通信和解耦。

## 架构设计

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   UserService   │───▶│ UserEventPublisher│───▶│   RabbitMQ      │
│                 │    │                  │    │  user.exchange  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  EventHandlers  │◀───│ UserEventListener│◀───│ user.service.   │
│                 │    │                  │    │     queue       │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## 目录结构

```
event/
├── README.md                        # 本文档
├── publisher/
│   └── UserEventPublisher.java      # 用户事件发布器
├── listener/
│   └── UserEventListener.java       # 用户事件监听器
└── handler/
    ├── UserCreatedEventHandler.java      # 用户创建事件处理器
    ├── UserUpdatedEventHandler.java      # 用户更新事件处理器
    ├── UserRoleChangedEventHandler.java  # 用户角色变更事件处理器
    └── UserStatusChangedEventHandler.java # 用户状态变更事件处理器
```

## 核心组件

### 1. UserEventPublisher（事件发布器）

**职责：**
- 封装用户相关事件的发布逻辑
- 提供类型安全的事件发布接口
- 处理事件发布的异常情况
- 生成唯一的关联ID用于追踪

**主要方法：**
```java
// 发布用户创建事件
CommonResult<Void> publishUserCreated(User user)
CommonResult<Void> publishUserCreated(User user, String invitationCode, Long inviterId)

// 发布用户更新事件
CommonResult<Void> publishUserUpdated(User user, String... updatedFields)

// 发布用户角色变更事件
CommonResult<Void> publishUserRoleChanged(User user, String oldRole, String newRole)

// 发布用户状态变更事件
CommonResult<Void> publishUserStatusChanged(User user, String oldStatus, String newStatus)

// 发布用户删除事件
CommonResult<Void> publishUserDeleted(Long userId, String username)
```

### 2. UserEventListener（事件监听器）

**职责：**
- 监听RabbitMQ中的用户相关事件
- 验证事件数据的完整性
- 将事件分发给对应的处理器
- 处理监听过程中的异常情况

**监听配置：**
- **队列：** user.service.queue
- **交换机：** user.exchange
- **路由键：** user.*

### 3. Event Handlers（事件处理器）

#### UserCreatedEventHandler
处理用户创建后的业务逻辑：
- 为用户生成邀请码
- 建立邀请关系（如果通过邀请注册）
- 初始化用户统计数据
- 同步用户信息到相关服务
- 发送欢迎通知

#### UserUpdatedEventHandler
处理用户信息更新后的业务逻辑：
- 更新缓存数据
- 同步信息到相关服务
- 更新搜索索引
- 记录审计日志
- 发送变更通知

#### UserRoleChangedEventHandler
处理用户角色变更后的业务逻辑：
- 更新用户权限和数据访问范围
- 重新计算佣金比例
- 更新层级关系
- 同步角色信息到各服务
- 通知奖励系统重新计算
- 记录角色变更审计

#### UserStatusChangedEventHandler
处理用户状态变更后的业务逻辑：
- 更新用户权限状态
- 处理在途业务数据
- 更新缓存状态
- 同步状态到相关服务
- 发送状态变更通知
- 记录状态变更审计

## 事件类型

| 事件类型 | 路由键 | 描述 |
|---------|--------|------|
| USER_CREATED | user.created | 用户创建事件 |
| USER_UPDATED | user.updated | 用户信息更新事件 |
| USER_DELETED | user.deleted | 用户删除事件 |
| USER_STATUS_CHANGED | user.status.changed | 用户状态变更事件 |
| USER_ROLE_CHANGED | user.role.changed | 用户角色变更事件 |

## 配置说明

### RabbitMQ配置

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    # 发布确认配置
    publisher-confirm-type: correlated
    publisher-returns: true
    # 消费者配置
    listener:
      simple:
        acknowledge-mode: auto
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          max-interval: 10000
          multiplier: 2
```

### 队列配置

- **队列名：** user.service.queue
- **交换机：** user.exchange (Topic类型)
- **路由键模式：** user.*
- **消息TTL：** 5分钟
- **死信队列：** dlx.queue
- **重试机制：** 最多3次，指数退避

## 使用示例

### 发布事件

```java
@Service
public class UserServiceImpl {
    @Autowired
    private UserEventPublisher userEventPublisher;
    
    public CommonResult<UserResponse> createUser(CreateUserRequest request) {
        // ... 创建用户逻辑
        
        // 发布用户创建事件
        CommonResult<Void> result = userEventPublisher.publishUserCreated(user);
        if (!result.isSuccess()) {
            log.error("发布用户创建事件失败: {}", result.getMessage());
        }
        
        return CommonResult.success(userResponse);
    }
}
```

### 处理事件

事件处理是自动进行的，不需要手动调用。当事件发布到RabbitMQ后，UserEventListener会自动接收并分发给对应的处理器。

## 最佳实践

### 1. 事务性考虑
- 所有事件处理器方法都标记为`@Transactional`
- 事件发布失败不应阻断主业务流程
- 使用补偿机制处理事件处理失败的情况

### 2. 异常处理
- 事件处理器中的异常会触发消息重试
- 重试3次后消息会进入死信队列
- 记录详细的错误日志用于故障排查

### 3. 性能优化
- 使用异步处理避免阻塞主线程
- 合理设置消费者并发数（2-10）
- 避免在事件处理器中执行耗时操作

### 4. 监控和告警
- 监控事件发布成功率
- 监控消息消费延迟
- 监控死信队列消息数量
- 设置关键指标的告警阈值

## 扩展指南

### 添加新的事件类型

1. 在`EventType`枚举中添加新的事件类型
2. 创建对应的事件实体类（继承`DomainEvent`）
3. 在`UserEventPublisher`中添加发布方法
4. 创建对应的事件处理器
5. 在`UserEventListener`中添加监听方法

### 集成新的服务

在事件处理器中通过依赖注入的方式集成新的服务：

```java
@Component
public class UserCreatedEventHandler {
    @Autowired
    private NewService newService;  // 注入新服务
    
    public void handle(UserCreatedEvent event) {
        // ... 现有逻辑
        
        // 调用新服务
        newService.handleUserCreated(event.getUserId());
    }
}
```

## 故障排查

### 常见问题

1. **事件发布失败**
   - 检查RabbitMQ连接状态
   - 检查交换机和队列是否正确创建
   - 查看应用日志中的错误信息

2. **消息消费失败**
   - 检查消费者是否正常启动
   - 查看死信队列中的消息
   - 检查事件处理器的业务逻辑

3. **消息堆积**
   - 检查消费者性能瓶颈
   - 增加消费者并发数
   - 优化事件处理逻辑

### 调试技巧

- 开启DEBUG日志查看详细的事件处理流程
- 使用RabbitMQ管理界面监控队列状态
- 通过correlationId追踪事件处理链路

## 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0.0 | 2025-08-12 | 初始版本，实现基础事件驱动架构 |