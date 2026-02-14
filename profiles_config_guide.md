# Spring Boot 多环境配置说明

## 配置文件结构

Spring Boot 支持多环境配置，通过不同的配置文件来管理不同环境的设置：

```
src/main/resources/
├── application.yml          # 主配置文件
├── application-dev.yml      # 开发环境配置
├── application-test.yml     # 测试环境配置
└── application-prod.yml     # 生产环境配置
```

## 配置激活方式

### 1. 在主配置文件中指定环境

在 `application.yml` 中添加：
```yaml
spring:
  profiles:
    active: dev  # 激活开发环境
```

### 2. 通过命令行参数指定

```bash
# 启动时指定环境
java -jar your-app.jar --spring.profiles.active=dev

# 或者使用环境变量
export SPRING_PROFILES_ACTIVE=dev
java -jar your-app.jar
```

### 3. 在 IDE 中配置

在 IntelliJ IDEA 中：
1. Run/Debug Configurations
2. VM options 添加：`-Dspring.profiles.active=dev`

## 当前配置状态

### 主配置文件 (application.yml)
```yaml
spring:
  profiles:
    active: dev  # 已配置为使用开发环境
```

### 开发环境配置 (application-dev.yml)
已配置阿里云 OSS：
```yaml
aliyun:
  oss:
    endpoint: "oss-cn-hangzhou.aliyuncs.com"
    access-key-id: "LTAI5tD8nAtzG7n9wYHumwNv"
    access-key-secret: "t5rEzeuDb727OF7PpphZsgH6BXOe4L"
    bucket-name: "demo-lizhensheng"
    domain: "https://demo-lizhensheng.oss-cn-hangzhou.aliyuncs.com"
```

## 配置优先级

Spring Boot 配置加载顺序（优先级从高到低）：
1. 命令行参数
2. 环境变量
3. application-{profile}.yml
4. application.yml

## 验证配置是否生效

### 1. 启动日志检查
启动应用时查看日志：
```
The following profiles are active: dev
```

### 2. 创建测试接口
```java
@RestController
@RequestMapping("/config")
public class ConfigTestController {
    
    @Value("${aliyun.oss.endpoint}")
    private String ossEndpoint;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    @GetMapping("/info")
    public Map<String, String> getConfigInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("activeProfile", activeProfile);
        info.put("ossEndpoint", ossEndpoint);
        return info;
    }
}
```

访问 `http://localhost:8081/config/info` 验证配置。

## 常见问题

### Q: 配置不生效怎么办？
A: 检查以下几点：
1. 确认 `spring.profiles.active` 配置正确
2. 确认配置文件命名正确（`application-{profile}.yml`）
3. 确认属性名称与代码中的 `@Value` 注解匹配

### Q: 如何切换环境？
A: 修改 `application.yml` 中的 `spring.profiles.active` 值，或使用命令行参数

### Q: 生产环境如何配置？
A: 创建 `application-prod.yml` 文件，配置生产环境参数，然后设置 `spring.profiles.active=prod`

## 安全建议

1. **敏感信息不要提交到版本控制**
2. **使用配置中心管理生产环境配置**
3. **定期轮换 AccessKey**
4. **不同环境使用不同的 AccessKey**