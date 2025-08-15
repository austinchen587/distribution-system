# syntax=docker/dockerfile:1.6
FROM nginx:1.25-alpine
COPY config/swagger-config.yaml /usr/share/nginx/html/swagger.yaml
# 如有静态站点，解开下一行：
# COPY dist/ /usr/share/nginx/html/
EXPOSE 80