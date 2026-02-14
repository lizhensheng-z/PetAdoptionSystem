# 阿里云 OSS 配置指南

## 错误说明
您遇到的错误是因为应用程序无法找到阿里云 OSS 的配置参数：
```
java.lang.IllegalArgumentException: Could not resolve placeholder 'aliyun.oss.endpoint' in value "${aliyun.oss.endpoint}"
```

## 解决方案

### 1. 配置阿里云 OSS 参数

在 `src/main/resources/application.yml` 文件中已添加了 OSS 配置项，您需要将占位符替换为实际的阿里云 OSS 信息：

```yaml
# 阿里云 OSS 配置
aliyun:
  oss:
    endpoint: "oss-cn-beijing.aliyuncs.com"          # OSS 服务接入点
    access-key-id: "your-access-key-id"              # 阿里云 AccessKey ID
    access-key-secret: "your-access-key-secret"      # 阿里云 AccessKey Secret
    bucket-name: "your-bucket-name"                  # OSS 存储空间名称
    domain: "https://your-bucket-name.oss-cn-beijing.aliyuncs.com"  # 访问域名
```

### 2. 获取阿里云 OSS 配置信息

#### 2.1 创建阿里云账号
- 访问 [阿里云官网](https://www.aliyun.com/)
- 注册并登录阿里云控制台

#### 2.2 开通 OSS 服务
- 在控制台搜索 "对象存储 OSS"
- 点击 "立即开通"

#### 2.3 创建存储空间 (Bucket)
- 进入 OSS 控制台
- 点击 "创建 Bucket"
- 设置 Bucket 名称（如：pet-adoption-images）
- 选择地域（建议选择距离用户较近的地域）
- 读写权限选择 "公共读" 或 "私有"

#### 2.4 获取 AccessKey
- 进入 [AccessKey 管理页面](https://ram.console.aliyun.com/manage/ak)
- 创建 AccessKey（建议创建子账号的 AccessKey，更安全）
- 保存 AccessKey ID 和 AccessKey Secret

#### 2.5 获取 Endpoint
- Endpoint 格式：`oss-{region}.aliyuncs.com`
- 常用地域对应：
  - 华北1（青岛）：`oss-cn-qingdao.aliyuncs.com`
  - 华北2（北京）：`oss-cn-beijing.aliyuncs.com`
  - 华东1（杭州）：`oss-cn-hangzhou.aliyuncs.com`
  - 华东2（上海）：`oss-cn-shanghai.aliyuncs.com`
  - 华南1（深圳）：`oss-cn-shenzhen.aliyuncs.com`

### 3. 配置示例

```yaml
# 阿里云 OSS 配置示例
aliyun:
  oss:
    endpoint: "oss-cn-beijing.aliyuncs.com"
    access-key-id: "LTAI5t6D8mQz9Xp2rTn1uYv3"
    access-key-secret: "your-secret-key-here"
    bucket-name: "pet-adoption-images"
    domain: "https://pet-adoption-images.oss-cn-beijing.aliyuncs.com"
```

### 4. 权限配置建议

#### 4.1 RAM 用户权限（推荐）
创建 RAM 用户并分配最小权限：
```json
{
    "Version": "1",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "oss:PutObject",
                "oss:GetObject",
                "oss:DeleteObject"
            ],
            "Resource": [
                "acs:oss:*:*:your-bucket-name/*"
            ]
        }
    ]
}
```

#### 4.2 Bucket 权限设置
- 对于图片存储，建议设置为 "公共读"
- 可以通过 Bucket Policy 进行更精细的权限控制

### 5. 测试配置

配置完成后，可以运行以下测试代码验证配置是否正确：

```java
@RestController
@RequestMapping("/test")
public class OssTestController {
    
    @Autowired
    private UploadService uploadService;
    
    @GetMapping("/oss-config")
    public String testOssConfig() {
        return "OSS 配置正常";
    }
}
```

### 6. 常见问题

#### Q: AccessKey 泄露怎么办？
A: 立即在阿里云控制台禁用或删除该 AccessKey，创建新的 AccessKey

#### Q: 上传文件提示权限不足？
A: 检查 RAM 用户权限策略是否正确配置

#### Q: 访问图片 404？
A: 检查 Bucket 名称和 Endpoint 是否正确，确认文件已成功上传

#### Q: 如何使用自定义域名？
A: 在阿里云 OSS 控制台绑定自定义域名，并在配置中使用该域名

### 7. 安全建议

1. **不要在代码中硬编码 AccessKey**
2. **使用子账号的 AccessKey**，避免使用主账号
3. **定期轮换 AccessKey**
4. **启用日志审计**，监控 OSS 操作
5. **设置合理的权限策略**，遵循最小权限原则

### 8. 开发环境临时解决方案

如果暂时没有阿里云 OSS 账号，可以使用以下临时配置进行开发：

```yaml
# 本地文件存储配置（临时方案）
aliyun:
  oss:
    endpoint: "oss-cn-beijing.aliyuncs.com"
    access-key-id: "test-key-id"
    access-key-secret: "test-key-secret"
    bucket-name: "test-bucket"
    domain: "http://localhost:8080/uploads"
```

注意：这种配置仅用于开发测试，生产环境必须使用真实的阿里云 OSS 服务。