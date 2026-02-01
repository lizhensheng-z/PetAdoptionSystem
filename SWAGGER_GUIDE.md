# Swagger集成指南

## 项目已集成Swagger

宠物领养系统已成功集成Swagger/OpenAPI文档，使用SpringDoc OpenAPI 3.x版本。

## 访问地址

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI文档**: http://localhost:8081/v3/api-docs
- **JSON格式**: http://localhost:8081/v3/api-docs.json

## 配置说明

### 1. 依赖配置
项目已在`pom.xml`中配置了SpringDoc OpenAPI依赖：
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

### 2. 配置文件
在`application.yml`中配置了Swagger相关设置：
```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
```

### 3. 配置类
创建了`SwaggerConfig.java`配置类，配置了API文档的基本信息：
- 标题：宠物领养系统 API
- 版本：1.0.0
- 描述：宠物领养系统的RESTful API文档
- 联系信息：开发团队
- 服务器：本地开发环境和生产环境

## 使用示例

### 1. Controller层注解
```java
@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "用于验证Swagger集成的测试接口")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "获取问候消息", description = "返回一个简单的问候消息")
    @ApiResponse(responseCode = "200", description = "成功返回问候消息")
    public String hello() {
        return "Hello, Swagger! 宠物领养系统API已集成成功！";
    }
}
```

### 2. 模型注解
```java
@Data
@Schema(description = "宠物信息实体")
public class Pet {
    
    @Schema(description = "宠物ID", example = "1")
    private Long id;
    
    @Schema(description = "宠物名称", example = "小白", required = true)
    private String name;
    
    @Schema(description = "宠物类型", example = "狗", allowableValues = {"狗", "猫", "鸟", "其他"})
    private String type;
}
```

## 常用注解

| 注解 | 用途 | 示例 |
|------|------|------|
| `@Tag` | 控制器分组 | `@Tag(name = "用户管理", description = "用户相关接口")` |
| `@Operation` | 接口描述 | `@Operation(summary = "创建用户", description = "创建新用户账户")` |
| `@Parameter` | 参数描述 | `@Parameter(description = "用户ID", required = true)` |
| `@Schema` | 模型描述 | `@Schema(description = "用户信息实体")` |
| `@ApiResponse` | 响应描述 | `@ApiResponse(responseCode = "200", description = "操作成功")` |

## 启动项目

1. 确保MySQL和Redis服务已启动
2. 运行项目：
   ```bash
   ./mvnw spring-boot:run
   ```
3. 访问Swagger UI：http://localhost:8081/swagger-ui.html

## 注意事项

1. 生产环境建议关闭Swagger文档，可通过配置实现
2. 敏感接口需要添加适当的安全控制
3. 所有Controller和DTO都应该添加Swagger注解以提高文档质量