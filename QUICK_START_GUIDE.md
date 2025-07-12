# 🚀 分销系统启动指南

## 📋 前置准备

### 1. 安装必需软件

#### Java 开发环境
- **JDK 8** 或更高版本
- 下载地址：https://www.oracle.com/java/technologies/downloads/
- 安装后验证：打开命令行输入 `java -version`

#### Maven 构建工具
- **Maven 3.6+**
- 下载地址：https://maven.apache.org/download.cgi
- 安装后验证：打开命令行输入 `mvn -version`

#### MySQL 数据库
- **MySQL 5.7** 或 **8.0**
- 下载地址：https://dev.mysql.com/downloads/mysql/
- 默认端口：3306
- 记住你设置的 root 密码！

#### Redis 缓存
- **Redis 5.0+**
- Windows版本：https://github.com/microsoftarchive/redis/releases
- Mac用户：`brew install redis`
- 默认端口：6379

#### 开发工具（推荐）
- **IntelliJ IDEA**（推荐）或 Eclipse
- 下载地址：https://www.jetbrains.com/idea/download/

---

## 🔧 环境配置

### 1. 配置 MySQL 数据库

```sql
-- 1. 登录 MySQL
mysql -u root -p

-- 2. 创建数据库
CREATE DATABASE distribution_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 创建用户（可选，使用 root 也可以）
CREATE USER 'distribution'@'localhost' IDENTIFIED BY 'distribution123';
GRANT ALL PRIVILEGES ON distribution_system.* TO 'distribution'@'localhost';
FLUSH PRIVILEGES;

-- 4. 使用数据库
USE distribution_system;

-- 5. 执行建表脚本（如果有的话）
-- source /path/to/database.sql
```

### 2. 启动 Redis

```bash
# Windows
redis-server.exe

# Mac/Linux
redis-server

# 验证Redis是否启动
redis-cli ping
# 应该返回 PONG
```

---

## 📦 项目构建

### 1. 下载项目代码
```bash
# 如果使用 Git
git clone [你的项目地址]
cd Distribution-System

# 或者直接解压下载的压缩包
```

### 2. 修改配置文件

编辑 `auth-service/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/distribution_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root  # 改成你的数据库用户名
    password: root  # 改成你的数据库密码
    
  redis:
    host: localhost  # Redis 地址
    port: 6379      # Redis 端口
```

### 3. 构建项目

在项目根目录执行：

```bash
# 清理并安装所有依赖
mvn clean install -DskipTests

# 如果下载很慢，可以配置阿里云镜像（见下方）
```

#### 配置 Maven 阿里云镜像（加速下载）

找到 Maven 的 `settings.xml` 文件：
- Windows: `C:\Users\{用户名}\.m2\settings.xml`
- Mac/Linux: `~/.m2/settings.xml`

添加以下内容：

```xml
<mirrors>
    <mirror>
        <id>aliyun</id>
        <mirrorOf>*</mirrorOf>
        <name>阿里云公共仓库</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

---

## 🎯 启动项目

### 方式一：使用 IntelliJ IDEA（推荐）

1. **打开项目**
   - File → Open → 选择项目根目录
   - 等待 IDEA 索引完成

2. **启动服务**
   - 找到 `auth-service/src/main/java/com/example/auth/AuthServiceApplication.java`
   - 右键 → Run 'AuthServiceApplication'
   - 或点击类旁边的绿色三角形按钮

3. **查看启动日志**
   - 看到 `Started AuthServiceApplication in X seconds` 表示启动成功
   - 默认端口：8081

### 方式二：使用命令行

```bash
# 1. 进入认证服务目录
cd auth-service

# 2. 运行项目
mvn spring-boot:run

# 或者先打包再运行
mvn clean package
java -jar target/auth-service-1.0-SNAPSHOT.jar
```

---

## ✅ 验证服务是否启动成功

### 1. 访问 Swagger 文档
打开浏览器，访问：http://localhost:8081/auth/swagger-ui.html

你应该能看到：
- API 文档界面
- 各种接口的说明
- 可以直接在页面上测试接口

### 2. 测试健康检查
```bash
# 使用 curl 命令
curl http://localhost:8081/auth/actuator/health

# 或直接在浏览器访问
http://localhost:8081/auth/actuator/health
```

---

## 🧪 测试 API 接口

### 使用 Swagger UI 测试（推荐）

1. **打开 Swagger UI**
   - http://localhost:8081/auth/swagger-ui.html

2. **测试登录接口**
   - 找到"用户认证"分组
   - 点击 `POST /api/auth/login`
   - 点击 "Try it out"
   - 输入测试数据：
     ```json
     {
       "phone": "13800138000",
       "password": "123456"
     }
     ```
   - 点击 "Execute"
   - 查看返回结果

3. **使用 Token 测试其他接口**
   - 复制登录返回的 token
   - 点击页面右上角的 "Authorize" 按钮
   - 输入：`Bearer {你的token}`
   - 点击 "Authorize"
   - 现在可以测试需要认证的接口了

### 使用 Postman 测试

1. **下载 Postman**
   - https://www.postman.com/downloads/

2. **创建请求**
   - 新建请求
   - 选择 POST 方法
   - URL: `http://localhost:8081/auth/api/auth/login`
   - Body 选择 raw → JSON
   - 输入：
     ```json
     {
       "phone": "13800138000",
       "password": "123456"
     }
     ```

3. **添加认证头**
   - Headers 添加：
   - Key: `Authorization`
   - Value: `Bearer {token}`

---

## 🐛 常见问题解决

### 1. 端口被占用
```
错误：Web server failed to start. Port 8081 was already in use.
```
**解决方案**：
- 修改 `application.yml` 中的 `server.port` 为其他端口
- 或者找到占用端口的程序并关闭

### 2. 数据库连接失败
```
错误：Failed to obtain JDBC Connection
```
**解决方案**：
- 检查 MySQL 是否启动
- 检查用户名密码是否正确
- 检查数据库是否创建

### 3. Redis 连接失败
```
错误：Unable to connect to Redis
```
**解决方案**：
- 检查 Redis 是否启动
- 检查端口是否正确（默认 6379）

### 4. 依赖下载失败
```
错误：Could not resolve dependencies
```
**解决方案**：
- 检查网络连接
- 配置 Maven 阿里云镜像
- 重新执行 `mvn clean install`

---

## 📱 项目结构说明

```
Distribution-System/
├── common/           # 公共模块（工具类、权限等）
├── auth-service/     # 认证服务（登录注册）
├── lead-service/     # 客资服务
├── deal-service/     # 成交服务
├── product-service/  # 产品服务
└── pom.xml          # 父项目配置
```

---

## 🎉 恭喜！

如果你看到 Swagger 页面并能成功调用接口，说明项目已经成功运行了！

### 下一步可以：
1. 探索 Swagger 文档中的各种接口
2. 查看 `common/example` 目录下的示例代码
3. 尝试添加自己的接口
4. 学习项目中的各种设计模式

### 需要帮助？
- 查看项目中的 README 文件
- 查看 `Context-Engineering/plan/` 目录下的设计文档
- 在 IDE 中使用代码提示和文档

---

## 💡 小贴士

1. **使用 IDEA 的优势**
   - 自动导入依赖
   - 代码提示和补全
   - 一键运行和调试
   - 内置的 Maven 和 Git 支持

2. **学习资源**
   - Spring Boot 官方文档：https://spring.io/projects/spring-boot
   - Swagger 使用指南：项目中的 `api-docs/SWAGGER_GUIDE.md`
   - 项目设计文档：`Context-Engineering/plan/` 目录

3. **调试技巧**
   - 查看控制台日志
   - 使用断点调试
   - 查看 Swagger 文档了解接口

祝你使用愉快！ 🚀