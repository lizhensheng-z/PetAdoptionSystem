# MyBatis-Plus 代码生成器使用指南

## 项目概述

本项目是一个宠物领养系统，基于Spring Boot 3.2.5 + MyBatis-Plus 3.5.7开发。代码生成器可以自动生成实体类、Mapper接口、Service接口及实现类、Controller类，大大提高开发效率。

## 数据库结构

系统包含以下主要业务表：

### 1. 系统管理表
- `sys_user` - 系统用户表
- `sys_role` - 角色表
- `sys_permission` - 权限表
- `sys_user_role` - 用户角色关联表
- `sys_role_permission` - 角色权限关联表
- `sys_config` - 系统配置表
- `sys_notice` - 系统公告表
- `audit_log` - 审计日志表

### 2. 机构管理表
- `org_profile` - 机构资料表

### 3. 宠物管理表
- `pet` - 宠物档案表
- `pet_media` - 宠物媒体表
- `tag` - 标签字典表
- `pet_tag` - 宠物标签关联表
- `pet_audit` - 宠物审核记录表

### 4. 领养流程表
- `adoption_application` - 领养申请表
- `adoption_flow_log` - 申请状态流转日志表

### 5. 用户行为表
- `user_favorite` - 用户收藏表
- `user_behavior` - 用户行为埋点表

### 6. 信用系统表
- `checkin_post` - 领养后打卡表
- `credit_account` - 用户信用账户表
- `credit_log` - 信用分变更流水表

## 代码生成器使用方法

### 方法1：使用简单生成器（推荐）

直接运行以下类即可一键生成所有代码：

```bash
# 运行简单代码生成器
mvn test-compile exec:java -Dexec.mainClass="com.yr.pet.adoption.SimpleCodeGenerator"
```

或者在IDE中直接运行：`SimpleCodeGenerator.main()`

### 方法2：使用高级生成器（交互式）

运行高级代码生成器，支持自定义配置：

```bash
# 运行高级代码生成器
mvn test-compile exec:java -Dexec.mainClass="com.yr.pet.adoption.AdvancedCodeGenerator"
```

运行后会提示输入：
- 数据库连接信息
- 包名配置
- 选择要生成的表
- 是否覆盖已存在文件

### 方法3：使用基础生成器

运行基础代码生成器：

```bash
# 运行基础代码生成器
mvn test-compile exec:java -Dexec.mainClass="com.yr.pet.adoption.CodeGenerator"
```

## 生成文件结构

代码生成后，文件将按以下结构组织：

```
src/main/java/com/yr/pet/adoption/
├── entity/           # 实体类
│   ├── SysUserEntity.java
│   ├── PetEntity.java
│   └── ...
├── mapper/           # Mapper接口
│   ├── SysUserMapper.java
│   ├── PetMapper.java
│   └── ...
├── service/          # Service接口
│   ├── SysUserService.java
│   ├── PetService.java
│   └── ...
├── service/impl/     # Service实现类
│   ├── SysUserServiceImpl.java
│   ├── PetServiceImpl.java
│   └── ...
└── controller/       # Controller类
    ├── SysUserController.java
    ├── PetController.java
    └── ...

src/main/resources/mapper/
├── SysUserMapper.xml
├── PetMapper.xml
└── ...
```

## 配置说明

### 数据库连接配置

在生成器中修改以下配置：

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/petAdoptionSystem?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "root";
```

### 包名配置

```java
private static final String PARENT_PACKAGE = "com.yr.pet.adoption";
```

### 生成选项

- **enableLombok()**: 启用Lombok注解
- **enableChainModel()**: 启用链式调用
- **enableActiveRecord()**: 启用ActiveRecord模式
- **logicDeleteColumnName("deleted")**: 逻辑删除字段
- **versionColumnName("version")**: 乐观锁字段

## 使用建议

1. **首次生成**：建议使用`SimpleCodeGenerator`一键生成所有表的基础代码
2. **增量生成**：后续新增表时，可以修改`getTables()`方法指定特定表名
3. **自定义模板**：如需自定义模板，可以修改`templateConfig`部分
4. **字段填充**：已配置自动填充创建时间和更新时间

## 注意事项

1. **数据库连接**：确保MySQL服务已启动，且数据库`petAdoptionSystem`已创建
2. **表结构**：确保所有表都已创建，且字段命名符合规范
3. **包路径**：生成的代码包路径为`com.yr.pet.adoption.*`
4. **依赖检查**：确保`pom.xml`中已包含MyBatis-Plus相关依赖

## 常见问题

### Q: 生成后代码有编译错误？
A: 检查是否缺少Lombok依赖，确保IDE已安装Lombok插件

### Q: 如何只生成特定表？
A: 修改`getTables()`方法返回的表名列表

### Q: 如何自定义模板？
A: 在`templateConfig`中指定自定义模板路径

### Q: 生成后如何测试？
A: 可以创建测试类验证生成的Mapper和Service是否正常工作

## 后续开发

生成代码后，建议：

1. 检查生成的实体类字段类型是否正确
2. 根据业务需求添加自定义的Service方法
3. 在Controller中添加具体的业务逻辑
4. 配置MyBatis-Plus分页插件（已配置）
5. 添加全局异常处理

## 技术支持

如有问题，请检查：
- 数据库连接配置
- 表结构是否正确
- 依赖版本是否匹配
- 包路径是否正确