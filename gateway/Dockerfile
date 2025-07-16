# 基础镜像选择说明：
# 1. openjdk:8-jdk-alpine - 使用Java 8是因为父pom.xml中配置的java.version=1.8
# 2. alpine版本 - 基于Alpine Linux的轻量级镜像，体积小，安全性高，适合微服务部署
# 3. 如果需要更高版本的Java，可以修改为：
#    - openjdk:11-jdk-alpine (Java 11 LTS版本)
#    - openjdk:17-jdk-alpine (Java 17 LTS版本，推荐)
#    - openjdk:21-jdk-alpine (Java 21 LTS版本，最新)
FROM openjdk:8-jdk-alpine

# 设置时区为上海时间，保持与应用配置一致
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 创建临时文件挂载点，用于Spring Boot的临时文件
VOLUME /tmp

# JVM参数配置说明：
# JAVA_OPTS - JVM启动参数，可通过docker-compose或k8s配置传入
# 常用参数示例：
# -Xms256m -Xmx512m : 设置JVM堆内存最小256MB，最大512MB
# -XX:+UseG1GC : 使用G1垃圾收集器（推荐用于低延迟应用）
# -Djava.security.egd=file:/dev/./urandom : 加快随机数生成，提升启动速度
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

# 应用参数配置说明：
# APP_OPTS - Spring Boot应用参数，可动态传入
# 常用参数示例：
# --spring.profiles.active=docker : 激活docker配置文件
# --server.port=8080 : 动态修改端口
# --spring.cloud.nacos.discovery.server-addr=nacos:8848 : 动态配置Nacos地址
ENV APP_OPTS=""

# 暴露网关服务端口
EXPOSE 8080

# 复制编译后的jar包到容器中
# 使用通配符以适应不同版本号
COPY target/gateway-*.jar app.jar

# 健康检查配置
# 每30秒检查一次，超时3秒，连续3次失败则认为不健康
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动命令说明：
# 1. 使用sh -c 执行，确保环境变量正确解析
# 2. $JAVA_OPTS 在java命令之后，用于JVM参数
# 3. $APP_OPTS 在jar包之后，用于Spring Boot参数
# 4. 完整启动命令示例：
#    java -Xms256m -Xmx512m -jar /app.jar --spring.profiles.active=docker
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar $APP_OPTS"]