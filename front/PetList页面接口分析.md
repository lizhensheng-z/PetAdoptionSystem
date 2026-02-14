# PetList.vue 页面接口需求分析

## 一、页面功能分析

### 1.1 页面位置
- 文件路径: `/Users/lizhensheng/Desktop/test/pet-adoption/src/views/user/PetList.vue`
- 路由路径: `/pets`
- 访问权限: 无需认证（游客可访问）

### 1.2 核心功能模块

#### A. 搜索和筛选功能
```javascript
// 当前代码中的筛选条件 (第153-160行)
const filters = reactive({
  keyword: '',      // 关键词搜索
  category: '',     // 物种分类（cat/dog/other）
  age: '',          // 年龄范围
  gender: '',       // 性别筛选
  vaccinated: '',   // 疫苗状态
  neutered: ''      // 绝育状态
})
```

#### B. 视图切换
- 网格视图（默认）
- 列表视图
- 切换按钮位于结果计数旁边

#### C. 分页功能
```javascript
const pagination = reactive({
  page: 1,           // 当前页码
  pageSize: 12       // 每页数量（可选：12, 24, 48）
})
```

#### D. 宠物数据展示
根据代码第207-220行的模拟数据，需要的字段包括：
```javascript
{
  id: number,              // 宠物ID
  name: string,            // 宠物名称
  breed: string,           // 品种
  age: number,             // 年龄（岁）
  gender: string,          // 性别（male/female）
  status: string,          // 状态
  images: string[],        // 图片URL数组
  tags: string[],          // 标签数组
  distance: number,        // 距离（可选）
  matchScore: number       // 匹配分数（可选，用于推荐）
}
```

#### E. URL参数支持
代码第235-240行显示需要从URL恢复筛选条件：
```javascript
// 从URL参数中恢复筛选条件
if (route.query.keyword) {
  filters.keyword = route.query.keyword
}
if (route.query.category) {
  filters.category = route.query.category
}
```

---

## 二、数据库模型分析

### 2.1 核心表结构

#### pet 表（宠物档案表）
```sql
CREATE TABLE pet (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_user_id BIGINT NOT NULL,           -- 发布机构用户ID
  name VARCHAR(64),                       -- 宠物名字
  species VARCHAR(16) NOT NULL,           -- 物种：CAT/DOG/OTHER
  breed VARCHAR(64),                      -- 品种
  gender VARCHAR(16),                     -- 性别：MALE/FEMALE/UNKNOWN
  age_month INT,                          -- 年龄（月）
  size VARCHAR(8),                        -- 体型：S/M/L
  color VARCHAR(32),                      -- 毛色
  sterilized TINYINT,                     -- 是否绝育：0否1是
  vaccinated TINYINT,                     -- 是否疫苗：0否1是
  dewormed TINYINT,                       -- 是否驱虫：0否1是
  health_desc VARCHAR(1000),              -- 健康描述
  personality_desc VARCHAR(1000),         -- 性格描述
  adopt_requirements VARCHAR(1000),       -- 领养要求
  status VARCHAR(16) DEFAULT 'DRAFT',     -- 状态：DRAFT/PENDING_AUDIT/PUBLISHED/APPLYING/ADOPTED/REMOVED
  audit_status VARCHAR(16) DEFAULT 'NONE',-- 审核状态：NONE/PENDING/APPROVED/REJECTED
  lng DECIMAL(10,6),                      -- 经度
  lat DECIMAL(10,6),                      -- 纬度
  cover_url VARCHAR(512),                 -- 封面URL
  published_time DATETIME,                -- 发布时间
  create_time DATETIME,
  update_time DATETIME,
  INDEX idx_org_status(org_user_id, status),
  INDEX idx_status_species(status, species),
  INDEX idx_published_time(published_time)
)
```

#### pet_media 表（宠物媒体表）
```sql
CREATE TABLE pet_media (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  pet_id BIGINT NOT NULL,
  url VARCHAR(512) NOT NULL,
  media_type VARCHAR(16) NOT NULL,        -- IMAGE/VIDEO
  sort INT DEFAULT 0,                     -- 排序
  create_time DATETIME,
  INDEX idx_pet_id(pet_id)
)
```

#### pet_tag 表（宠物标签关联表）
```sql
CREATE TABLE pet_tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  pet_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  create_time DATETIME,
  UNIQUE KEY uk_pet_tag(pet_id, tag_id)
)
```

#### tag 表（标签字典表）
```sql
CREATE TABLE tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  tag_type VARCHAR(32) NOT NULL,          -- SPECIES/PERSONALITY/HEALTH/FEATURE
  enabled TINYINT DEFAULT 1
)
```

### 2.2 字段映射关系

| 前端字段 | 数据库字段 | 数据类型 | 说明 |
|---------|-----------|---------|------|
| id | pet.id | BIGINT | 宠物ID |
| name | pet.name | VARCHAR(64) | 宠物名称 |
| species | pet.species | VARCHAR(16) | 物种：CAT/DOG/OTHER |
| breed | pet.breed | VARCHAR(64) | 品种 |
| age | pet.age_month | INT | 年龄（月），前端转换为岁/月 |
| gender | pet.gender | VARCHAR(16) | 性别：MALE/FEMALE/UNKNOWN |
| size | pet.size | VARCHAR(8) | 体型：S/M/L |
| color | pet.color | VARCHAR(32) | 毛色 |
| vaccinated | pet.vaccinated | TINYINT | 是否疫苗 |
| neutered | pet.sterilized | TINYINT | 是否绝育 |
| status | pet.status | VARCHAR(16) | 宠物状态 |
| images | pet_media.url | VARCHAR(512) | 多图片URL（需JOIN查询） |
| tags | tag.name | VARCHAR(64) | 标签名称数组（需JOIN查询） |
| organizationName | org_profile.org_name | VARCHAR(128) | 机构名称（需JOIN） |
| distance | 计算字段 | DECIMAL | 基于经纬度计算的距离 |
| matchScore | 计算字段 | INT | 推荐匹配分数 |

---

## 三、接口需求分析

### 3.1 必需接口

#### 接口1: 获取宠物列表（核心接口）

**接口路径**: `GET /api/pets`

**权限**: 无需认证（游客可访问）

**请求参数**:
```json
{
  "keyword": "string",      // 可选：搜索关键词（匹配 name、breed）
  "species": "string",      // 可选：物种筛选 CAT/DOG/OTHER
  "gender": "string",       // 可选：性别筛选 MALE/FEMALE
  "ageMin": "int",          // 可选：最小年龄（月）
  "ageMax": "int",          // 可选：最大年龄（月）
  "size": "string",         // 可选：体型筛选 S/M/L
  "vaccinated": "boolean",  // 可选：是否疫苗
  "sterilized": "boolean",  // 可选：是否绝育
  "status": "string",       // 可选：状态筛选（默认：PUBLISHED）
  "page": "int",            // 可选：页码（默认：1）
  "pageSize": "int",        // 可选：每页数量（默认：12，最大：48）
  "lng": "decimal",         // 可选：用户经度（用于计算距离）
  "lat": "decimal",         // 可选：用户纬度（用于计算距离）
  "maxDistance": "int"      // 可选：最大距离（km，默认：50）
}
```

**响应格式**:
```json
{
  "code": 10000,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "小橘",
        "species": "CAT",
        "breed": "橘猫",
        "ageMonth": 6,
        "gender": "MALE",
        "size": "M",
        "color": "橘色",
        "vaccinated": true,
        "sterilized": false,
        "status": "PUBLISHED",
        "coverUrl": "https://...",
        "images": [
          "https://...",
          "https://..."
        ],
        "tags": ["亲人", "活泼"],
        "orgUserId": 1001,
        "orgName": "爱心救助站",
        "publishedTime": "2024-01-20T10:00:00Z",
        "distance": 5.2,
        "matchScore": 85
      }
    ],
    "pageNo": 1,
    "pageSize": 12,
    "total": 120,
    "totalPages": 10
  },
  "timestamp": "2024-01-20T10:30:00Z"
}
```

**业务规则**:
1. 默认只返回状态为 `PUBLISHED`（已发布）且审核通过 `APPROVED` 的宠物
2. 必须包含封面图 `cover_url`
3. 必须关联查询图片列表 `pet_media`（按sort排序）
4. 必须关联查询标签 `pet_tag` + `tag`
5. 必须关联查询机构名称 `org_profile.org_name`
6. 如果提供了 `lng` 和 `lat`，计算距离
7. 支持关键词模糊搜索：匹配 `name`、`breed`、`personality_desc`
8. 分页参数验证：pageSize 最大 48
9. 排序规则：默认按 `published_time` 降序（最新发布的在前）

---

#### 接口2: 获取宠物详情

**接口路径**: `GET /api/pets/{id}`

**权限**: 无需认证（游客可访问）

**路径参数**:
```json
{
  "id": "long"  // 宠物ID
}
```

**响应格式**:
```json
{
  "code": 10000,
  "message": "success",
  "data": {
    "id": 1,
    "name": "小橘",
    "species": "CAT",
    "breed": "橘猫",
    "ageMonth": 6,
    "gender": "MALE",
    "size": "M",
    "color": "橘色",
    "sterilized": false,
    "vaccinated": true,
    "dewormed": true,
    "healthDesc": "身体健康，已完成基础疫苗",
    "personalityDesc": "性格亲人，活泼好动",
    "adoptRequirements": "需要有养猫经验，定期回访",
    "status": "PUBLISHED",
    "lng": 116.404,
    "lat": 39.915,
    "coverUrl": "https://...",
    "mediaList": [
      {
        "id": 1,
        "url": "https://...",
        "mediaType": "IMAGE",
        "sort": 0
      }
    ],
    "tags": ["亲人", "活泼", "已疫苗"],
    "orgUserId": 1001,
    "orgName": "爱心救助站",
    "publishedTime": "2024-01-20T10:00:00Z",
    "createTime": "2024-01-15T08:00:00Z"
  },
  "timestamp": "2024-01-20T10:30:00Z"
}
```

**业务规则**:
1. 返回完整信息（包括所有描述字段）
2. 必须包含媒体列表（图片和视频，按sort排序）
3. 必须包含标签列表
4. 必须包含发布机构信息
5. 如果宠物状态不是 `PUBLISHED`，非机构用户不可访问

---

### 3.2 可选接口（推荐功能）

#### 接口3: 推荐宠物列表

**接口路径**: `GET /api/pets/recommend`

**权限**: 需认证

**请求参数**:
```json
{
  "page": "int",           // 可选：页码（默认：1）
  "pageSize": "int",       // 可选：每页数量（默认：12）
  "lng": "decimal",        // 可选：用户经度
  "lat": "decimal"         // 可选：用户纬度
}
```

**响应格式**: 同接口1，增加 `matchScore` 字段

**业务规则**:
1. 基于用户偏好（`sys_user.preference_json`）进行智能匹配
2. 推荐算法权重配置（来自 `sys_config`）：
   - 偏好/标签匹配权重: α (默认 0.55)
   - 协同过滤权重: β (默认 0.20)
   - 距离权重: γ (默认 0.20)
   - 新鲜度权重: δ (默认 0.05)
3. 只推荐 `PUBLISHED` 状态的宠物
4. 排除用户已收藏和已申请的宠物

---

#### 接口4: 搜索建议

**接口路径**: `GET /api/pets/suggest`

**权限**: 无需认证

**请求参数**:
```json
{
  "keyword": "string"      // 搜索关键词（最少2个字符）
}
```

**响应格式**:
```json
{
  "code": 10000,
  "message": "success",
  "data": {
    "breeds": ["橘猫", "英短", "金毛", "柯基"],     // 品种建议
    "keywords": ["小橘", "咪咪", "旺财"],         // 宠物名建议
    "tags": ["亲人", "活泼", "可爱"]              // 标签建议
  },
  "timestamp": "2024-01-20T10:30:00Z"
}
```

---

## 四、数据库查询设计

### 4.1 宠物列表查询SQL

```sql
-- 基础查询（不包含距离计算）
SELECT
  p.id,
  p.name,
  p.species,
  p.breed,
  p.age_month,
  p.gender,
  p.size,
  p.color,
  p.sterilized,
  p.vaccinated,
  p.dewormed,
  p.health_desc,
  p.personality_desc,
  p.adopt_requirements,
  p.status,
  p.lng,
  p.lat,
  p.cover_url,
  p.published_time,
  p.create_time,
  org.org_name,
  GROUP_CONCAT(pm.url ORDER BY pm.sort SEPARATOR ',') as images,
  GROUP_CONCAT(t.name ORDER BY t.name SEPARATOR ',') as tags
FROM pet p
LEFT JOIN org_profile org ON p.org_user_id = org.user_id
LEFT JOIN pet_media pm ON p.id = pm.pet_id AND pm.deleted = 0
LEFT JOIN pet_tag pt ON p.id = pt.pet_id AND pt.deleted = 0
LEFT JOIN tag t ON pt.tag_id = t.id AND t.enabled = 1 AND t.deleted = 0
WHERE p.status = 'PUBLISHED'
  AND p.audit_status = 'APPROVED'
  AND p.deleted = 0
  AND (p.species = ? OR ? IS NULL)
  AND (p.gender = ? OR ? IS NULL)
  AND (p.age_month >= ? OR ? IS NULL)
  AND (p.age_month <= ? OR ? IS NULL)
  AND (p.vaccinated = ? OR ? IS NULL)
  AND (p.sterilized = ? OR ? IS NULL)
  AND (p.name LIKE ? OR p.breed LIKE ? OR p.personality_desc LIKE ? OR ? IS NULL)
GROUP BY p.id
ORDER BY p.published_time DESC
LIMIT ? OFFSET ?;
```

### 4.2 距离计算（如果需要）

```sql
-- 使用 ST_Distance_Sphere 计算距离（MySQL 5.7+）
SELECT
  p.*,
  (6371 * ACOS(
    COS(RADIANS(?)) * COS(RADIANS(p.lat)) *
    COS(RADIANS(p.lng) - RADIANS(?)) +
    SIN(RADIANS(?)) * SIN(RADIANS(p.lat))
  )) as distance
FROM pet p
WHERE ...
HAVING distance <= ? OR ? IS NULL
ORDER BY distance ASC, p.published_time DESC;
```

---

## 五、接口实现建议

### 5.1 前端API封装

```javascript
// src/api/modules/pet.js
import http from '../request.js'

export const petAPI = {
  // 获取宠物列表
  getPets(params = {}) {
    return http.get('/pets', params)
  },

  // 获取宠物详情
  getPetDetail(id) {
    return http.get(`/pets/${id}`)
  },

  // 推荐宠物列表
  getRecommendPets(params = {}) {
    return http.get('/pets/recommend', params)
  },

  // 搜索建议
  getSuggest(keyword) {
    return http.get('/pets/suggest', { keyword })
  }
}
```

### 5.2 状态管理

```javascript
// src/stores/pet.js
import { defineStore } from 'pinia'
import { petAPI } from '@/api/modules/pet.js'

export const usePetStore = defineStore('pet', {
  state: () => ({
    petList: [],
    currentPet: null,
    total: 0,
    filters: {
      keyword: '',
      species: '',
      gender: '',
      ageMin: null,
      ageMax: null,
      size: '',
      vaccinated: null,
      sterilized: null
    },
    pagination: {
      page: 1,
      pageSize: 12
    }
  }),

  actions: {
    async fetchPets() {
      const params = {
        ...this.filters,
        ...this.pagination
      }
      const response = await petAPI.getPets(params)
      this.petList = response.data.list
      this.total = response.data.total
      return response.data
    },

    async fetchPetDetail(id) {
      const response = await petAPI.getPetDetail(id)
      this.currentPet = response.data
      return response.data
    }
  }
})
```

---

## 六、性能优化建议

### 6.1 数据库优化
1. 为 `pet` 表的查询字段创建联合索引：
   ```sql
   CREATE INDEX idx_query ON pet(status, species, gender, published_time);
   ```
2. 为 `pet_media` 表创建索引：
   ```sql
   CREATE INDEX idx_pet_sort ON pet_media(pet_id, sort);
   ```

### 6.2 缓存策略
1. 热门宠物列表缓存（Redis，5分钟）
2. 宠物详情缓存（Redis，10分钟）
3. 标签字典缓存（内存，启动时加载）

### 6.3 分页优化
1. 使用游标分页替代传统的 OFFSET 分页（大数据量场景）
2. 限制最大返回数量（48条/页）

---

## 七、错误处理

### 7.1 常见错误码

| 错误码 | 说明 | 处理建议 |
|-------|------|---------|
| 1004 | 宠物不存在 | 跳转404页面或显示"宠物不存在"提示 |
| 1003 | 参数验证失败 | 提示用户检查输入参数 |
| 1007 | 服务器错误 | 显示"服务器繁忙，请稍后重试" |

### 7.2 空状态处理
- 列表为空时显示"暂无相关宠物"
- 详情不存在时显示"该宠物已被下架或删除"

---

## 八、测试用例

### 8.1 功能测试
1. 无筛选条件获取宠物列表
2. 按物种筛选（猫/狗/其他）
3. 按性别筛选
4. 按年龄范围筛选
5. 关键词搜索
6. 分页功能
7. 查看宠物详情

### 8.2 性能测试
1. 列表接口响应时间 < 500ms
2. 详情接口响应时间 < 300ms
3. 支持并发 100 QPS

### 8.3 安全测试
1. SQL注入防护
2. XSS防护
3. 权限验证（非PUBLISHED状态的宠物不可访问）