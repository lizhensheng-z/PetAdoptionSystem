# 宠物在线领养系统（SpringBoot + Vue3）README（整体设计版）

> 目标：构建连接救助机构与领养人的一体化平台，集**信息聚合、全流程领养、智能匹配、附近优先推荐、信用成长体系**于一体，并支持后续扩展健康AI模块。  
> 技术栈：SpringBoot / MyBatis-Plus / Spring Security + JWT / MySQL / Redis；Vue3 / TS / Pinia / Element Plus。

---

## 1. 项目简介

### 1.1 背景与痛点
- 救助机构：缺少统一的数字化工具（宠物档案、申请管理、审核流程、回访记录）。
- 领养人：信息不透明、筛选成本高、流程线下化、缺少可信机制。
- 行业问题：匹配效率低、弃养风险、地域分散、平台“信息黄页化”。

### 1.2 核心价值
- **全流程线上化**：发布—申请—审核—签约/交接—回访打卡—信用成长。
- **智能匹配**：偏好/标签匹配 + 行为协同过滤推荐。
- **附近优先**：基于腾讯位置服务（LBS）按距离优先展示。
- **信用体系**：鼓励长期责任，形成机构审核参考依据与激励机制。

---

## 2. 功能范围（按角色）

### 2.1 角色定义
- 游客（未登录）
- 领养人（普通用户）
- 救助机构（机构用户）
- 管理员（平台运营/审核）

### 2.2 游客
- 浏览宠物列表/详情
- 条件筛选、附近浏览（需授权定位）
- 注册/登录

### 2.3 领养人（用户端）
- 个人资料、偏好设置（期望类型/体型/年龄/性格/预算/城市等）
- 收藏、浏览历史
- 发起领养申请（填写问卷、上传资料）
- 查看申请进度（受理/面谈/家访/通过/拒绝/撤回）
- 领养后打卡（图文/视频）、回访问卷
- 信用分查看、成长等级、徽章

### 2.4 救助机构（机构端）
- 机构资料管理（地址、经纬度、联系方式、资质）
- 宠物发布与管理（档案、标签、领养要求、状态流转）
- 申请处理（审核、约谈、备注、通知、导出）
- 回访管理（查看领养人打卡、异常提醒、黑名单建议）

### 2.5 管理员
- 用户/机构审核与封禁
- 宠物内容审核（反作弊、敏感词、图片违规）
- 运营配置（标签库、推荐权重、信用规则、公告）
- 数据看板（领养成功率、申请转化、活跃、地区分布）

---

## 3. 系统总体架构

### 3.1 架构图（逻辑）
- **前端（Vue3）**：用户端 + 机构端 + 管理端（可按路由与权限区分）
- **后端（SpringBoot）**：REST API（认证鉴权、业务服务、推荐服务、LBS适配）
- **数据层（MySQL）**：用户、宠物、申请、打卡、信用、标签、操作日志
- **缓存（Redis）**：热点宠物列表、附近列表缓存、验证码、JWT黑名单/限流
- **第三方服务**：腾讯位置服务（地理编码/周边搜索/距离计算），对象存储（图片视频）

### 3.2 分层设计（后端）
- controller：REST接口层
- service：业务编排（流程、权限、推荐、信用）
- manager/domain：领域模型（申请流转、评分规则）
- mapper：MyBatis-Plus数据访问
- common：统一返回、异常、日志、工具、常量
- security：Spring Security + JWT

---

## 4. 数据库设计（核心表）

> 建议：统一定义 `id(bigint)`、`create_time`、`update_time`、`deleted(tinyint)`（逻辑删除）、`version`（乐观锁可选）。

### 4.1 用户与权限
- `sys_user`
    - id, username, password_hash, phone, email
    - role（USER/ORG/ADMIN）
    - avatar, status（NORMAL/BANNED）
- `org_profile`
    - user_id(FK), org_name, license_no, contact_name, contact_phone
    - address, province/city/district
    - lng, lat（用于LBS）
    - verify_status（PENDING/APPROVED/REJECTED）

### 4.2 宠物与标签
- `pet`
    - id, org_id, name, species(猫/狗/其他), breed, gender, age_month
    - size(S/M/L), color, sterilized, vaccinated, dewormed
    - health_desc, personality_desc
    - status（DRAFT/PUBLISHED/APPLYING/ADOPTED/REMOVED）
    - lng, lat（可继承机构坐标，也可单独设置）
- `pet_media`
    - id, pet_id, url, type(image/video), sort
- `tag`
    - id, name, type（SPECIES/PERSONALITY/HEALTH/FEATURE）
- `pet_tag`
    - id, pet_id, tag_id

### 4.3 领养申请与流程
- `adoption_application`
    - id, pet_id, user_id
    - questionnaire_json（结构化问卷存JSON）
    - status（SUBMITTED/UNDER_REVIEW/INTERVIEW/HOME_VISIT/APPROVED/REJECTED/CANCELLED）
    - reject_reason, org_remark
- `adoption_flow_log`
    - id, application_id, from_status, to_status, operator_id, remark, create_time

### 4.4 收藏/行为数据（推荐用）
- `user_favorite`
    - id, user_id, pet_id
- `user_behavior`
    - id, user_id, pet_id, type(VIEW/FAVORITE/APPLY/SHARE), weight, create_time

### 4.5 打卡回访与信用
- `checkin_post`
    - id, user_id, pet_id, content, media_urls(json), create_time
- `credit_account`
    - user_id, score, level, last_calc_time
- `credit_log`
    - id, user_id, delta, reason, ref_type(checkin/apply/violation), ref_id, create_time

### 4.6 运营与审计
- `sys_notice`、`sys_config`
- `audit_log`（关键操作：删除宠物、封禁用户、状态流转等）

---

## 5. 核心业务流程设计

### 5.1 宠物发布流程
1) 机构创建宠物草稿（DRAFT）
2) 上传媒体、打标签、完善健康信息
3) 提交发布 → 审核（可选：管理员或自动审核）
4) 发布成功（PUBLISHED）进入推荐池

### 5.2 领养申请流程（状态机）
- SUBMITTED（用户提交）
- UNDER_REVIEW（机构初审）
- INTERVIEW（线上/线下面谈）
- HOME_VISIT（可选）
- APPROVED → 生成领养完成记录 & 宠物状态 ADOPTED
- REJECTED / CANCELLED（结束）

> 约束：同一宠物在 APPLYING 时可限制同时申请人数上限，或允许多申请但只批准一个，批准后自动关闭其他申请。

### 5.3 回访与信用成长
- 领养后 N 天内引导打卡（例如 7/30/90 天）
- 依据打卡频率、内容质量、互动行为计算信用分
- 低活跃或异常举报触发“风险提示”给机构

---

## 6. 智能推荐与匹配（设计方案）

### 6.1 基于偏好与标签的“规则 + 打分”匹配（MVP可先做）
**输入：**
- 用户偏好：species、size、age_range、性格标签、是否绝育/疫苗等
- 宠物标签/属性：pet表字段 + pet_tag

**输出：**
- 匹配分 `match_score`（0-100）
- 解释项：为何推荐（命中哪些偏好/标签）

**示例评分：**
- 物种匹配 +30
- 体型匹配 +15
- 年龄区间匹配 +15
- 性格标签命中每个 +8（最多3个）
- 健康要求（疫苗/驱虫/绝育）命中 +10
- 距离加权（见6.3）

### 6.2 协同过滤推荐（如何实现：轻量可落地）
**目标：** 利用“浏览/收藏/申请”等行为，推荐相似用户喜欢的宠物或相似宠物。

**数据准备：**
- 行为表 `user_behavior`，对不同动作赋权重：
    - VIEW=1，FAVORITE=3，APPLY=5，CHECKIN=2
- 形成用户-宠物隐式反馈矩阵（稀疏）

**实现路线（两种）**
- **方案A（MVP优先）：Item-based CF（无需训练复杂模型）**
    - 计算宠物之间共现相似度（同一用户交互过的宠物集合）
    - 对用户交互过的宠物求相似宠物TopN
    - 优点：实现简单、可增量更新
- **方案B（进阶）：ALS/隐语义模型**
    - 定期离线训练（每日/每周），将结果写入 `recommend_result` 表或Redis
    - 使用 Python（scikit-learn/implicit库）训练，后端读取推荐结果

> 建议：先做A保证可用性，再做B提升效果。

### 6.3 “附近优先”融合策略
最终排序分：
```
final_score = α * match_score + β * cf_score + γ * distance_score + δ * freshness
```
- distance_score：距离越近分越高（如 0~20 分）
- freshness：新发布加权，避免冷启动
- α、β、γ、δ 存在 `sys_config` 支持运营调参

### 6.4 LBS接入（腾讯位置服务）
- 机构/宠物存储 `lng/lat`
- 用户端获取定位（H5/APP/浏览器）
- 后端：
    - 方案1：调用腾讯“周边搜索”返回附近机构/宠物（再二次过滤）
    - 方案2：本地Haversine计算 + MySQL空间索引（后期可替换）
- 缓存：同城/同坐标附近宠物TopN缓存到Redis，降低频繁请求成本

---

## 7. 权限与安全设计

### 7.1 认证
- Spring Security + JWT
- 登录签发 access_token（短）+ refresh_token（可选）
- Redis可存 JWT 黑名单（退出/封禁即时生效）

### 7.2 授权（RBAC简化）
- ADMIN：全权限
- ORG：仅管理自身 org_id 下的宠物与申请
- USER：仅管理自身资料、申请、收藏、打卡

### 7.3 安全策略
- 参数校验（JSR-380）
- 接口限流（Redis滑动窗口/令牌桶）
- 文件上传：类型校验、大小限制、病毒扫描（可选）
- 审计日志：关键操作入库

---

## 8. 前端设计（Vue3）

### 8.1 技术选型
- Vue3 + TypeScript
- Pinia（状态）
- Vue Router（路由与守卫）
- Element Plus（组件）
- Axios（请求封装、拦截器处理Token/错误码）

### 8.2 目录建议
```
src/
  api/                // 接口封装
  assets/
  components/
  layouts/
  pages/
    user/             // 用户端页面
    org/              // 机构端页面
    admin/            // 管理端页面
  router/
  store/
  utils/
```

### 8.3 核心页面
- 首页：推荐流（附近/智能匹配）
- 宠物详情：标签、健康信息、机构信息、申请入口
- 申请表：分步表单、上传材料
- 个人中心：偏好、收藏、申请进度、信用成长
- 机构后台：宠物管理、申请管理、回访与信用查看
- 管理后台：审核、配置、数据看板

---

## 9. 后端接口规范（示例）

> 统一前缀：`/api`；统一返回：`{ code, message, data }`

### 9.1 认证
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET  /api/auth/me`

### 9.2 宠物
- `GET  /api/pets`（筛选/分页/排序/附近）
- `GET  /api/pets/{id}`
- `POST /api/org/pets`（机构创建）
- `PUT  /api/org/pets/{id}`
- `POST /api/org/pets/{id}/publish`
- `DELETE /api/org/pets/{id}`

### 9.3 领养申请
- `POST /api/applications`（用户申请）
- `GET  /api/applications/my`
- `GET  /api/org/applications`（机构侧）
- `POST /api/org/applications/{id}/status`（流转）

### 9.4 收藏/行为
- `POST /api/favorites/{petId}`
- `DELETE /api/favorites/{petId}`
- `POST /api/behavior`（埋点：view/favorite/apply）

### 9.5 推荐
- `GET /api/recommend`（个性化推荐）
- `GET /api/recommend/nearby`（附近优先）

### 9.6 打卡与信用
- `POST /api/checkins`
- `GET  /api/checkins/my`
- `GET  /api/credit/me`

---

## 10. 性能与可用性设计

- Redis缓存：
    - 热门宠物列表、附近列表、标签字典
    - 申请状态查询可做短缓存
- 异步任务：
    - 推荐计算（离线/定时）
    - 信用分定时结算
    - 邮件/短信通知（可选）
- 分页与索引：
    - pet：status、species、org_id、create_time 索引
    - application：pet_id、user_id、status 索引
    - behavior：user_id、pet_id、type、create_time 索引

---

## 11. 部署方案

### 11.1 环境
- JDK 17（或 8/11，建议统一）
- MySQL 8.0
- Redis 6+
- Nginx（反代前端与后端）
- 对象存储（腾讯云COS/OSS/MinIO）

### 11.2 部署结构（推荐）
- `adopt-api`：SpringBoot jar（8080）
- `adopt-web`：Vue3打包dist由Nginx托管
- Nginx：
    - `/` -> 前端
    - `/api` -> 反代后端

### 11.3 配置项（后端）
- `application.yml`：
    - datasource、redis
    - jwt secret & expire
    - tencent lbs key
    - upload endpoint（对象存储）

---

## 12. 开发里程碑（建议）

1) **M1：基础业务可用**
- 登录注册、RBAC
- 宠物发布/浏览/筛选
- 领养申请与状态流转

2) **M2：平台差异化能力**
- 附近优先（LBS）
- 偏好/标签匹配打分
- 信用体系（打卡+积分+等级）

3) **M3：体验与智能增强**
- Item-based CF推荐
- 运营后台（配置推荐权重、信用规则）
- 性能优化与缓存

4) **M4（二期）：健康AI**
- 图片检测服务化（独立服务/微服务）
- 健康报告与提醒

---



## 14. 测试策略

- 单元测试：核心规则（匹配打分、信用计算、状态机）
- 集成测试：申请流转、权限边界、接口幂等
- 压测点：
    - 首页推荐列表
    - 附近列表
    - 宠物详情
- 安全测试：越权、注入、文件上传、接口限流

---

## 15. 未来扩展点

- 宠物健康AI识别（图像模型微调/第三方API）
- 与宠物医院/商家合作：优惠券、就医预约
- 领养合同电子签（合规模块）
- 举报与反虐待机制（内容审核、黑名单共享）

---

体字段与计算规则（可直接写成代码的伪代码/公式）**。