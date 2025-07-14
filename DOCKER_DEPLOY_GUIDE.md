# 🐳 Docker 部署指南

## 前置条件

1. 服务器已安装 Docker 和 Docker Compose
2. 服务器开放端口：8081（Auth Service）、3306（MySQL）、6379（Redis）

## 部署步骤

### 1. 准备代码

```bash
# 克隆项目到服务器
git clone [你的项目地址]
cd Distribution-System
```

### 2. 构建镜像并启动服务

```bash
# 使用docker-compose构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f auth-service
```

### 3. 初始化数据库

如果有数据库初始化脚本：

```bash
# 进入MySQL容器
docker exec -it mysql mysql -uroot -proot123

# 执行初始化脚本
source /path/to/your/init.sql
```

### 4. 验证部署

```bash
# 检查健康状态
curl http://localhost:8081/auth/actuator/health

# 访问Swagger文档
# 浏览器打开：http://服务器IP:8081/auth/swagger-ui.html
```

## 常用命令

### 查看日志
```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs -f auth-service

# 查看最新100行日志
docker-compose logs --tail=100 auth-service
```

### 重启服务
```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart auth-service
```

### 停止服务
```bash
# 停止所有服务
docker-compose stop

# 停止并删除容器
docker-compose down

# 停止并删除容器、网络、卷
docker-compose down -v
```

### 更新部署
```bash
# 拉取最新代码
git pull

# 重新构建并部署
docker-compose up -d --build auth-service
```

## 生产环境优化

### 1. 使用环境变量文件

创建 `.env` 文件：
```env
MYSQL_ROOT_PASSWORD=your_secure_password
REDIS_PASSWORD=your_redis_password
JWT_SECRET=your_jwt_secret
```

### 2. 配置nginx反向代理

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location /auth/ {
        proxy_pass http://localhost:8081/auth/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 3. 资源限制

在docker-compose.yml中添加资源限制：
```yaml
auth-service:
  deploy:
    resources:
      limits:
        cpus: '1'
        memory: 1G
      reservations:
        cpus: '0.5'
        memory: 512M
```

### 4. 日志管理

配置日志驱动：
```yaml
auth-service:
  logging:
    driver: "json-file"
    options:
      max-size: "10m"
      max-file: "3"
```

## 监控

### 使用docker stats监控资源使用
```bash
docker stats
```

### 配置Prometheus + Grafana（可选）
详见监控配置文档

## 故障排查

### 1. 服务无法启动
- 检查端口是否被占用：`netstat -tulpn | grep 8081`
- 查看详细日志：`docker-compose logs auth-service`

### 2. 数据库连接失败
- 确认MySQL已启动：`docker-compose ps mysql`
- 测试连接：`docker exec -it mysql mysql -uroot -proot123`

### 3. Redis连接失败
- 确认Redis已启动：`docker-compose ps redis`
- 测试连接：`docker exec -it redis redis-cli ping`