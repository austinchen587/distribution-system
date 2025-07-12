# Swagger/OpenAPI 文档使用指南

## 访问方式

启动任意一个微服务后，可以通过以下URL访问Swagger文档：

- **Swagger UI**: `http://localhost:{port}/{context-path}/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:{port}/{context-path}/v3/api-docs`

### 各服务访问地址

| 服务名称 | 端口 | Swagger UI地址 |
|---------|------|----------------|
| auth-service | 8081 | http://localhost:8081/auth/swagger-ui.html |
| lead-service | 8082 | http://localhost:8082/lead/swagger-ui.html |
| deal-service | 8083 | http://localhost:8083/deal/swagger-ui.html |
| product-service | 8084 | http://localhost:8084/product/swagger-ui.html |
| promotion-service | 8085 | http://localhost:8085/promotion/swagger-ui.html |
| level-service | 8086 | http://localhost:8086/level/swagger-ui.html |

## 使用JWT认证

1. 首先调用登录接口获取JWT token
2. 点击Swagger UI页面右上角的"Authorize"按钮
3. 在弹出的对话框中输入：`Bearer {你的token}`
4. 点击"Authorize"按钮完成认证
5. 之后所有需要认证的接口都会自动带上Authorization头

## Swagger注解使用示例

### 1. Controller级别注解

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户的增删改查接口")
public class UserController {
    // ...
}
```

### 2. 方法级别注解

```java
@Operation(summary = "创建用户", description = "创建一个新用户")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "创建成功"),
    @ApiResponse(responseCode = "400", description = "参数错误")
})
@PostMapping
public ApiResponse<User> createUser(@Valid @RequestBody UserCreateRequest request) {
    // ...
}
```

### 3. 参数注解

```java
@Parameter(description = "用户ID", required = true, example = "1")
@PathVariable Long id
```

### 4. 模型注解

```java
@Schema(description = "用户信息")
public class User {
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", required = true, example = "张三")
    private String username;
}
```

## 配置说明

在`application.yml`中配置Swagger：

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  packages-to-scan: com.example
  paths-to-match: /**
```

## 常见问题

1. **Q: 为什么看不到Swagger页面？**
   - A: 检查是否添加了springdoc-openapi-ui依赖，确认访问路径正确

2. **Q: 如何隐藏某些接口？**
   - A: 使用`@Hidden`注解或在配置中排除特定路径

3. **Q: 如何分组展示API？**
   - A: 使用`@Tag`注解对接口进行分组

4. **Q: 如何自定义响应示例？**
   - A: 使用`@ExampleObject`注解提供示例数据