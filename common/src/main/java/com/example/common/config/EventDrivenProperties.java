package com.example.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件驱动架构配置属性
 * 
 * <p>从配置文件中读取事件驱动架构相关的配置参数，
 * 包括RabbitMQ连接、Saga事务、事件发布等配置。
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "event-driven")
public class EventDrivenProperties {

    /**
     * RabbitMQ配置
     */
    private RabbitMQConfig rabbitmq = new RabbitMQConfig();

    /**
     * Saga配置
     */
    private SagaConfig saga = new SagaConfig();

    /**
     * 事件发布配置
     */
    private EventPublishConfig eventPublish = new EventPublishConfig();

    /**
     * 服务发现配置
     */
    private ServiceDiscoveryConfig serviceDiscovery = new ServiceDiscoveryConfig();

    // Getters and Setters
    public RabbitMQConfig getRabbitmq() { return rabbitmq; }
    public void setRabbitmq(RabbitMQConfig rabbitmq) { this.rabbitmq = rabbitmq; }

    public SagaConfig getSaga() { return saga; }
    public void setSaga(SagaConfig saga) { this.saga = saga; }

    public EventPublishConfig getEventPublish() { return eventPublish; }
    public void setEventPublish(EventPublishConfig eventPublish) { this.eventPublish = eventPublish; }

    public ServiceDiscoveryConfig getServiceDiscovery() { return serviceDiscovery; }
    public void setServiceDiscovery(ServiceDiscoveryConfig serviceDiscovery) { this.serviceDiscovery = serviceDiscovery; }

    /**
     * RabbitMQ配置类
     */
    public static class RabbitMQConfig {
        /**
         * 消息TTL（毫秒）
         */
        private long messageTtl = 300000L; // 5分钟

        /**
         * 最大重试次数
         */
        private int maxRetries = 3;

        /**
         * 重试间隔（毫秒）
         */
        private long retryInterval = 1000L;

        /**
         * 是否启用发布确认
         */
        private boolean publisherConfirms = true;

        /**
         * 是否启用返回确认
         */
        private boolean publisherReturns = true;

        /**
         * 预取数量
         */
        private int prefetchCount = 10;

        /**
         * 并发消费者数量
         */
        private int concurrentConsumers = 2;

        /**
         * 最大并发消费者数量
         */
        private int maxConcurrentConsumers = 10;

        // Getters and Setters
        public long getMessageTtl() { return messageTtl; }
        public void setMessageTtl(long messageTtl) { this.messageTtl = messageTtl; }

        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

        public long getRetryInterval() { return retryInterval; }
        public void setRetryInterval(long retryInterval) { this.retryInterval = retryInterval; }

        public boolean isPublisherConfirms() { return publisherConfirms; }
        public void setPublisherConfirms(boolean publisherConfirms) { this.publisherConfirms = publisherConfirms; }

        public boolean isPublisherReturns() { return publisherReturns; }
        public void setPublisherReturns(boolean publisherReturns) { this.publisherReturns = publisherReturns; }

        public int getPrefetchCount() { return prefetchCount; }
        public void setPrefetchCount(int prefetchCount) { this.prefetchCount = prefetchCount; }

        public int getConcurrentConsumers() { return concurrentConsumers; }
        public void setConcurrentConsumers(int concurrentConsumers) { this.concurrentConsumers = concurrentConsumers; }

        public int getMaxConcurrentConsumers() { return maxConcurrentConsumers; }
        public void setMaxConcurrentConsumers(int maxConcurrentConsumers) { this.maxConcurrentConsumers = maxConcurrentConsumers; }
    }

    /**
     * Saga配置类
     */
    public static class SagaConfig {
        /**
         * Saga事务默认超时时间（毫秒）
         */
        private long defaultTimeout = 300000L; // 5分钟

        /**
         * Saga步骤默认超时时间（毫秒）
         */
        private long stepTimeout = 30000L; // 30秒

        /**
         * 最大重试次数
         */
        private int maxRetries = 3;

        /**
         * 重试间隔（毫秒）
         */
        private long retryInterval = 1000L;

        /**
         * 是否启用补偿机制
         */
        private boolean compensationEnabled = true;

        /**
         * 清理已完成事务的间隔（毫秒）
         */
        private long cleanupInterval = 3600000L; // 1小时

        /**
         * 事务保留时间（毫秒）
         */
        private long retentionPeriod = 86400000L; // 24小时

        /**
         * 最大并发执行的Saga数量
         */
        private int maxConcurrentSagas = 100;

        // Getters and Setters
        public long getDefaultTimeout() { return defaultTimeout; }
        public void setDefaultTimeout(long defaultTimeout) { this.defaultTimeout = defaultTimeout; }

        public long getStepTimeout() { return stepTimeout; }
        public void setStepTimeout(long stepTimeout) { this.stepTimeout = stepTimeout; }

        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

        public long getRetryInterval() { return retryInterval; }
        public void setRetryInterval(long retryInterval) { this.retryInterval = retryInterval; }

        public boolean isCompensationEnabled() { return compensationEnabled; }
        public void setCompensationEnabled(boolean compensationEnabled) { this.compensationEnabled = compensationEnabled; }

        public long getCleanupInterval() { return cleanupInterval; }
        public void setCleanupInterval(long cleanupInterval) { this.cleanupInterval = cleanupInterval; }

        public long getRetentionPeriod() { return retentionPeriod; }
        public void setRetentionPeriod(long retentionPeriod) { this.retentionPeriod = retentionPeriod; }

        public int getMaxConcurrentSagas() { return maxConcurrentSagas; }
        public void setMaxConcurrentSagas(int maxConcurrentSagas) { this.maxConcurrentSagas = maxConcurrentSagas; }
    }

    /**
     * 事件发布配置类
     */
    public static class EventPublishConfig {
        /**
         * 批量发布大小
         */
        private int batchSize = 100;

        /**
         * 批量发布间隔（毫秒）
         */
        private long batchInterval = 1000L;

        /**
         * 是否启用异步发布
         */
        private boolean asyncEnabled = true;

        /**
         * 异步发布队列大小
         */
        private int asyncQueueSize = 1000;

        /**
         * 发布超时时间（毫秒）
         */
        private long publishTimeout = 5000L;

        /**
         * 是否启用事件去重
         */
        private boolean deduplicationEnabled = true;

        /**
         * 去重窗口时间（毫秒）
         */
        private long deduplicationWindow = 60000L; // 1分钟

        // Getters and Setters
        public int getBatchSize() { return batchSize; }
        public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

        public long getBatchInterval() { return batchInterval; }
        public void setBatchInterval(long batchInterval) { this.batchInterval = batchInterval; }

        public boolean isAsyncEnabled() { return asyncEnabled; }
        public void setAsyncEnabled(boolean asyncEnabled) { this.asyncEnabled = asyncEnabled; }

        public int getAsyncQueueSize() { return asyncQueueSize; }
        public void setAsyncQueueSize(int asyncQueueSize) { this.asyncQueueSize = asyncQueueSize; }

        public long getPublishTimeout() { return publishTimeout; }
        public void setPublishTimeout(long publishTimeout) { this.publishTimeout = publishTimeout; }

        public boolean isDeduplicationEnabled() { return deduplicationEnabled; }
        public void setDeduplicationEnabled(boolean deduplicationEnabled) { this.deduplicationEnabled = deduplicationEnabled; }

        public long getDeduplicationWindow() { return deduplicationWindow; }
        public void setDeduplicationWindow(long deduplicationWindow) { this.deduplicationWindow = deduplicationWindow; }
    }

    /**
     * 服务发现配置类
     */
    public static class ServiceDiscoveryConfig {
        /**
         * 服务端点映射
         */
        private Map<String, String> endpoints = new HashMap<>();

        /**
         * 健康检查间隔（毫秒）
         */
        private long healthCheckInterval = 30000L; // 30秒

        /**
         * 连接超时时间（毫秒）
         */
        private long connectTimeout = 5000L;

        /**
         * 读取超时时间（毫秒）
         */
        private long readTimeout = 10000L;

        /**
         * 熔断器失败阈值
         */
        private int circuitBreakerFailureThreshold = 5;

        /**
         * 熔断器恢复时间（毫秒）
         */
        private long circuitBreakerRecoveryTime = 60000L; // 1分钟

        // 默认服务端点配置
        public ServiceDiscoveryConfig() {
            endpoints.put("auth-service", "http://localhost:8081");
            endpoints.put("lead-service", "http://localhost:8082");
            endpoints.put("promotion-service", "http://localhost:8083");
            endpoints.put("reward-service", "http://localhost:8084");
            endpoints.put("invitation-service", "http://localhost:8085");
        }

        // Getters and Setters
        public Map<String, String> getEndpoints() { return endpoints; }
        public void setEndpoints(Map<String, String> endpoints) { this.endpoints = endpoints; }

        public long getHealthCheckInterval() { return healthCheckInterval; }
        public void setHealthCheckInterval(long healthCheckInterval) { this.healthCheckInterval = healthCheckInterval; }

        public long getConnectTimeout() { return connectTimeout; }
        public void setConnectTimeout(long connectTimeout) { this.connectTimeout = connectTimeout; }

        public long getReadTimeout() { return readTimeout; }
        public void setReadTimeout(long readTimeout) { this.readTimeout = readTimeout; }

        public int getCircuitBreakerFailureThreshold() { return circuitBreakerFailureThreshold; }
        public void setCircuitBreakerFailureThreshold(int circuitBreakerFailureThreshold) { 
            this.circuitBreakerFailureThreshold = circuitBreakerFailureThreshold; 
        }

        public long getCircuitBreakerRecoveryTime() { return circuitBreakerRecoveryTime; }
        public void setCircuitBreakerRecoveryTime(long circuitBreakerRecoveryTime) { 
            this.circuitBreakerRecoveryTime = circuitBreakerRecoveryTime; 
        }
    }
}