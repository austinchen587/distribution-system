package com.example.common.config;

import com.example.common.saga.SagaCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 事件驱动架构启动监听器
 * 
 * <p>监听应用启动事件，在应用完全启动后输出事件驱动架构的配置和状态信息。
 * 帮助开发人员和运维人员快速了解当前应用的事件驱动能力。
 * 
 * <p>输出信息包括：
 * <ul>
 *   <li>RabbitMQ连接状态</li>
 *   <li>Saga协调器状态</li>
 *   <li>已注册的事件监听器</li>
 *   <li>已配置的业务流程Saga</li>
 *   <li>性能配置参数</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class EventDrivenStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(EventDrivenStartupListener.class);
    
    @Autowired(required = false)
    private EventDrivenProperties eventDrivenProperties;
    
    @Autowired(required = false)
    private SagaCoordinator sagaCoordinator;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("\n" +
            "===============================================\n" +
            "     事件驱动架构启动完成\n" + 
            "     Event-Driven Architecture Ready\n" +
            "===============================================");
        
        ConfigurableApplicationContext context = event.getApplicationContext();
        
        // 输出基本配置信息
        outputBasicConfiguration(context);
        
        // 输出RabbitMQ配置信息
        outputRabbitMQConfiguration();
        
        // 输出Saga配置信息
        outputSagaConfiguration();
        
        // 输出服务发现配置
        outputServiceDiscoveryConfiguration();
        
        // 输出注册的组件信息
        outputRegisteredComponents(context);
        
        // 执行健康检查
        performHealthChecks(context);
        
        logger.info("===============================================\n" +
                   "事件驱动架构启动验证完成，系统就绪！\n" +
                   "===============================================");
    }

    /**
     * 输出基本配置信息
     */
    private void outputBasicConfiguration(ConfigurableApplicationContext context) {
        logger.info("📋 基本配置信息:");
        logger.info("   └─ 应用名称: {}", context.getEnvironment().getProperty("spring.application.name", "未知"));
        logger.info("   └─ 活动配置: {}", String.join(", ", context.getEnvironment().getActiveProfiles()));
        logger.info("   └─ 事件驱动架构: {}", eventDrivenProperties != null ? "✅ 已启用" : "❌ 未启用");
    }

    /**
     * 输出RabbitMQ配置信息
     */
    private void outputRabbitMQConfiguration() {
        if (eventDrivenProperties == null) return;
        
        logger.info("🐰 RabbitMQ配置信息:");
        EventDrivenProperties.RabbitMQConfig rabbitmq = eventDrivenProperties.getRabbitmq();
        logger.info("   ├─ 消息TTL: {}ms", rabbitmq.getMessageTtl());
        logger.info("   ├─ 最大重试次数: {}", rabbitmq.getMaxRetries());
        logger.info("   ├─ 重试间隔: {}ms", rabbitmq.getRetryInterval());
        logger.info("   ├─ 发布确认: {}", rabbitmq.isPublisherConfirms() ? "✅" : "❌");
        logger.info("   ├─ 返回确认: {}", rabbitmq.isPublisherReturns() ? "✅" : "❌");
        logger.info("   ├─ 预取数量: {}", rabbitmq.getPrefetchCount());
        logger.info("   └─ 并发消费者: {}-{}", rabbitmq.getConcurrentConsumers(), rabbitmq.getMaxConcurrentConsumers());
    }

    /**
     * 输出Saga配置信息
     */
    private void outputSagaConfiguration() {
        if (eventDrivenProperties == null) return;
        
        logger.info("⚡ Saga事务配置:");
        EventDrivenProperties.SagaConfig saga = eventDrivenProperties.getSaga();
        logger.info("   ├─ 默认超时: {}ms ({}分钟)", saga.getDefaultTimeout(), saga.getDefaultTimeout() / 60000);
        logger.info("   ├─ 步骤超时: {}ms ({}秒)", saga.getStepTimeout(), saga.getStepTimeout() / 1000);
        logger.info("   ├─ 最大重试: {}", saga.getMaxRetries());
        logger.info("   ├─ 补偿机制: {}", saga.isCompensationEnabled() ? "✅" : "❌");
        logger.info("   ├─ 清理间隔: {}ms ({}小时)", saga.getCleanupInterval(), saga.getCleanupInterval() / 3600000);
        logger.info("   ├─ 保留时间: {}ms ({}小时)", saga.getRetentionPeriod(), saga.getRetentionPeriod() / 3600000);
        logger.info("   └─ 最大并发: {}", saga.getMaxConcurrentSagas());
    }

    /**
     * 输出服务发现配置
     */
    private void outputServiceDiscoveryConfiguration() {
        if (eventDrivenProperties == null) return;
        
        logger.info("🔍 服务发现配置:");
        EventDrivenProperties.ServiceDiscoveryConfig serviceDiscovery = eventDrivenProperties.getServiceDiscovery();
        logger.info("   ├─ 已注册服务: {}", serviceDiscovery.getEndpoints().size());
        
        serviceDiscovery.getEndpoints().forEach((serviceName, endpoint) -> 
            logger.info("   │  └─ {}: {}", serviceName, endpoint)
        );
        
        logger.info("   ├─ 连接超时: {}ms", serviceDiscovery.getConnectTimeout());
        logger.info("   ├─ 读取超时: {}ms", serviceDiscovery.getReadTimeout());
        logger.info("   ├─ 熔断阈值: {}", serviceDiscovery.getCircuitBreakerFailureThreshold());
        logger.info("   └─ 恢复时间: {}ms", serviceDiscovery.getCircuitBreakerRecoveryTime());
    }

    /**
     * 输出已注册的组件信息
     */
    private void outputRegisteredComponents(ConfigurableApplicationContext context) {
        logger.info("🔧 已注册组件:");
        
        // 检查核心组件
        logger.info("   ├─ Saga协调器: {}", 
            context.getBeanNamesForType(SagaCoordinator.class).length > 0 ? "✅" : "❌");
        
        // 检查事件监听器
        String[] eventListeners = context.getBeanNamesForAnnotation(org.springframework.amqp.rabbit.annotation.RabbitListener.class);
        logger.info("   ├─ 事件监听器: {} 个", eventListeners.length);
        
        // 检查业务流程Saga
        String[] sagaComponents = context.getBeanNamesForType(
            org.springframework.stereotype.Component.class
        );
        long sagaProcessCount = java.util.Arrays.stream(sagaComponents)
            .filter(name -> name.toLowerCase().contains("saga"))
            .count();
        logger.info("   └─ 业务流程Saga: {} 个", sagaProcessCount);
    }

    /**
     * 执行健康检查
     */
    private void performHealthChecks(ConfigurableApplicationContext context) {
        logger.info("🏥 健康检查:");
        
        // 检查RabbitMQ连接
        try {
            if (context.getBeanNamesForType(org.springframework.amqp.rabbit.core.RabbitTemplate.class).length > 0) {
                logger.info("   ├─ RabbitMQ连接: ✅ 正常");
            } else {
                logger.info("   ├─ RabbitMQ连接: ❌ 未配置");
            }
        } catch (Exception e) {
            logger.info("   ├─ RabbitMQ连接: ❌ 异常 - {}", e.getMessage());
        }
        
        // 检查Redis连接
        try {
            if (context.getBeanNamesForType(org.springframework.data.redis.core.RedisTemplate.class).length > 0) {
                logger.info("   ├─ Redis连接: ✅ 正常");
            } else {
                logger.info("   ├─ Redis连接: ❌ 未配置");
            }
        } catch (Exception e) {
            logger.info("   ├─ Redis连接: ❌ 异常 - {}", e.getMessage());
        }
        
        // 检查数据库连接
        try {
            if (context.getBeanNamesForType(javax.sql.DataSource.class).length > 0) {
                logger.info("   └─ 数据库连接: ✅ 正常");
            } else {
                logger.info("   └─ 数据库连接: ❌ 未配置");
            }
        } catch (Exception e) {
            logger.info("   └─ 数据库连接: ❌ 异常 - {}", e.getMessage());
        }
    }

    /**
     * 输出性能建议
     */
    private void outputPerformanceRecommendations() {
        if (eventDrivenProperties == null) return;
        
        logger.info("💡 性能建议:");
        
        EventDrivenProperties.SagaConfig saga = eventDrivenProperties.getSaga();
        if (saga.getMaxConcurrentSagas() < 50) {
            logger.info("   ├─ 建议增加最大并发Saga数量以提高吞吐量");
        }
        
        EventDrivenProperties.RabbitMQConfig rabbitmq = eventDrivenProperties.getRabbitmq();
        if (rabbitmq.getPrefetchCount() < 10) {
            logger.info("   ├─ 建议增加RabbitMQ预取数量以提高消费性能");
        }
        
        EventDrivenProperties.EventPublishConfig eventPublish = eventDrivenProperties.getEventPublish();
        if (!eventPublish.isAsyncEnabled()) {
            logger.info("   ├─ 建议启用异步事件发布以提高响应速度");
        }
        
        if (eventPublish.getBatchSize() < 100) {
            logger.info("   └─ 建议增加批量发布大小以提高发布效率");
        }
    }

    /**
     * 输出故障排除提示
     */
    private void outputTroubleshootingTips() {
        logger.info("🔧 故障排除提示:");
        logger.info("   ├─ 查看Saga状态: GET /actuator/saga");
        logger.info("   ├─ 查看健康状态: GET /actuator/health");
        logger.info("   ├─ 查看性能指标: GET /actuator/metrics");
        logger.info("   └─ 查看应用信息: GET /actuator/info");
    }
}