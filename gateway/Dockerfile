# =========================
# 1) Builder
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 这里加环境变量
ENV MAVEN_OPTS="-Dhttps.protocols=TLSv1.2,TLSv1.3 -Djava.net.preferIPv4Stack=true"


WORKDIR /app

# 如需加速，取消下一行注释并确保仓库根目录有 settings.xml（阿里云镜像）
COPY settings.xml /root/.m2/settings.xml

# 先拷父/子 POM（利用缓存）
COPY pom.xml ./                                  
COPY common/pom.xml common/pom.xml
COPY data-access/pom.xml data-access/pom.xml
COPY gateway/pom.xml gateway/pom.xml

# 安装父 POM（非递归），让各子模块能找到 parent
RUN mvn -B -N -Dmaven.test.skip=true install

# 预热并安装 common（完全跳过测试编译）
RUN mvn -B -Dmaven.test.skip=true -f common/pom.xml dependency:go-offline
COPY common/src/ common/src/
RUN mvn -B -Dmaven.test.skip=true -f common/pom.xml clean install

# 如果 gateway 依赖 data-access，一并安装
RUN mvn -B -Dmaven.test.skip=true -f data-access/pom.xml dependency:go-offline
COPY data-access/src/ data-access/src/
RUN mvn -B -Dmaven.test.skip=true -f data-access/pom.xml clean install

# 预热 gateway 依赖
RUN mvn -B -Dmaven.test.skip=true -f gateway/pom.xml dependency:go-offline

# 拷贝 gateway 源码并仅构建 gateway
COPY gateway/src/ gateway/src/
RUN mvn -B -Dmaven.test.skip=true -f gateway/pom.xml clean package


# =========================
# 2) Runtime
# =========================
FROM eclipse-temurin:17-jre
WORKDIR /app

# 设置时区（可选）
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ >/etc/timezone

# 非 root 运行（幂等创建：存在则跳过）
RUN if ! getent group spring >/dev/null; then groupadd spring; fi && \
    if ! id -u spring >/dev/null 2>&1; then useradd -m -g spring -s /bin/bash spring; fi
USER spring:spring

# 复制打包产物
COPY --from=builder /app/gateway/target/gateway-*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]