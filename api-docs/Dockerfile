# 使用一个基础的Web服务器镜像
FROM nginx:alpine

# 复制Swagger UI文件到Nginx的HTML目录
COPY config/swagger-config.yaml /usr/share/nginx/html/swagger.yaml

# 暴露80端口
EXPOSE 80
