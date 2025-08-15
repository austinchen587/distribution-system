# syntax=docker/dockerfile:1.6

# ========= Builder =========
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# 阿里云 Maven 源（确保 settings.xml 在仓库根目录）
COPY settings.xml /root/.m2/settings.xml

# 先拷 POM（父 + 子）
COPY pom.xml pom.xml
COPY common/pom.xml common/pom.xml
COPY data-access/pom.xml data-access/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml

# 安装父 POM（非递归）
RUN mvn -f pom.xml -N -B -Dmaven.test.skip=true install

# common
RUN mvn -f common/pom.xml -B -Dmaven.test.skip=true dependency:go-offline
COPY common/src/ common/src/
RUN mvn -f common/pom.xml -B -Dmaven.test.skip=true clean install

# data-access
RUN mvn -f data-access/pom.xml -B -Dmaven.test.skip=true dependency:go-offline
COPY data-access/src/ data-access/src/
RUN mvn -f data-access/pom.xml -B -Dmaven.test.skip=true clean install

# auth-service
RUN mvn -f auth-service/pom.xml -B -Dmaven.test.skip=true dependency:go-offline
COPY auth-service/src/ auth-service/src/
RUN mvn -f auth-service/pom.xml -B -Dmaven.test.skip=true clean package

# ========= Runtime =========
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 用阿里云 ubuntu-ports 源；注意发行版是 jammy（22.04）
RUN set -eux; \
  cat >/etc/apt/sources.list <<'EOF'
deb http://mirrors.aliyun.com/ubuntu-ports jammy main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu-ports jammy-updates main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu-ports jammy-backports main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu-ports jammy-security main restricted universe multiverse
EOF
# 加重试/禁并发，规避网络抖动；然后装 curl
RUN apt-get -o Acquire::Retries=5 \
            -o Acquire::http::No-Cache=true \
            -o Acquire::http::Pipeline-Depth=0 update && \
    apt-get install -y --no-install-recommends curl ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# 复制 JAR
COPY --from=builder /app/auth-service/target/*-SNAPSHOT.jar app.jar

# 非 root 运行
RUN groupadd -g 1000 spring && useradd -m -u 1000 -g spring -s /bin/bash spring
USER spring:spring

EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -fsS http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]