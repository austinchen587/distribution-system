# 使用多阶段构建减小镜像体积
FROM maven:3.8.6-openjdk-8 AS builder

# 设置工作目录
WORKDIR /app

# 复制pom文件
COPY pom.xml .
COPY common/pom.xml common/
COPY data-access/pom.xml data-access/
COPY user-service/pom.xml user-service/

# 下载依赖（利用Docker缓存）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY common/src common/src
COPY data-access/src data-access/src
COPY user-service/src user-service/src

# 构建项目
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:8-jre-alpine

# 安装必要的工具
RUN apk add --no-cache curl

# 创建用户
RUN addgroup -g 1000 spring && \
    adduser -D -s /bin/sh -u 1000 -G spring spring

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder /app/user-service/target/user-service-1.0-SNAPSHOT.jar app.jar

# 修改文件权限
RUN chown -R spring:spring /app

# 切换到非root用户
USER spring:spring

# 暴露端口
EXPOSE 8086

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD curl -f http://localhost:8086/users/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
