# PetDetail 页面开发规划与接口设计

> **创建时间**: 2026-02-14
> **页面路径**: `/src/views/user/PetDetail.vue`
> **路由**: `/pets/:id`

---

## 📋 目录

1. [页面功能概述](#页面功能概述)
2. [数据库模型分析](#数据库模型分析)
3. [接口需求分析](#接口需求分析)
4. [页面功能设计](#页面功能设计)
5. [接口详细设计](#接口详细设计)
6. [开发任务分解](#开发任务分解)
7. [技术实现要点](#技术实现要点)

---

## 1. 页面功能概述

### 1.1 页面定位
宠物详情页是用户浏览宠物信息的核心页面，提供完整的宠物档案展示、互动功能和领养申请入口。

### 1.2 核心功能模块

| 模块 | 功能描述 | 权限要求 |
|------|---------|---------|
| **宠物基本信息** | 展示宠物照片、名称、品种、年龄、性别等基础信息 | 无需认证 |
| **健康状态** | 显示疫苗、绝育、驱虫等健康状态标签 | 无需认证 |
| **性格与特征** | 展示宠物性格描述和特征标签 | 无需认证 |
| **机构信息** | 显示发布机构的基本信息和联系方式 | 无需认证 |
| **收藏功能** | 用户可收藏/取消收藏宠物 | 需认证 |
| **领养申请** | 提交领养申请 | 需认证 |
| **分享功能** | 分享宠物信息到社交平台 | 无需认证 |
| **相似推荐** | 展示相似宠物列表 | 无需认证 |
| **行为埋点** | 记录用户浏览行为（用于推荐算法） | 无需认证 |

### 1.3 页面布局设计

```
┌─────────────────────────────────────────┐
│  顶部导航栏 (AppHeader)                  │
├─────────────────────────────────────────┤
│  页面标题栏 (PageHeader)                 │
├─────────────────────────────────────────┤
│  ┌───────────────────────────────────┐  │
│  │  图片轮播 (3-9张)                 │  │
│  └───────────────────────────────────┘  │
│  ┌─────────────────┬─────────────────┐  │
│  │  宠物名称       │  状态标签        │  │
│  │  品种·年龄·性别 │  收藏按钮        │  │
│  ├─────────────────┴─────────────────┤  │
│  │  基本信息卡片                      │  │
│  ├───────────────────────────────────┤  │
│  │  健康状态标签                      │  │
│  ├───────────────────────────────────┤  │
│  │  性格特征标签                      │  │
│  ├───────────────────────────────────┤  │
│  │  领养要求描述                      │  │
│  ├───────────────────────────────────┤  │
│  │  发布机构信息                      │  │
│  └───────────────────────────────────┘  │
│  ┌───────────────────────────────────┐  │
│  │  相似宠物推荐 (横向滚动)           │  │
│  └───────────────────────────────────┘  │
│  ┌───────────────────────────────────┐  │
│  │  底部固定操作栏                    │  │
│  │  [分享] [收藏] [申请领养]          │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

---

## 2. 数据库模型分析

### 2.1 核心数据表

#### 2.1.1 pet（宠物档案表）

| 字段 | 类型 | 说明 | 前端用途 |
|------|------|------|---------|
| id | BIGINT | 主键ID | 宠物唯一标识 |
| org_user_id | BIGINT | 机构用户ID | 关联机构信息 |
| name | VARCHAR(64) | 宠物名字 | 显示宠物名称 |
| species | VARCHAR(16) | 物种：CAT/DOG/OTHER | 物种筛选和显示 |
| breed | VARCHAR(64) | 品种 | 显示品种信息 |
| gender | VARCHAR(16) | 性别：MALE/FEMALE/UNKNOWN | 性别显示 |
| age_month | INT | 年龄（月） | 年龄计算显示 |
| size | VARCHAR(8) | 体型：S/M/L | 体型显示 |
| color | VARCHAR(32) | 毛色/颜色 | 毛色显示 |
| sterilized | TINYINT | 是否绝育：0否1是 | 健康状态标签 |
| vaccinated | TINYINT | 是否疫苗：0否1是 | 健康状态标签 |
| dewormed | TINYINT | 是否驱虫：0否1是 | 健康状态标签 |
| health_desc | VARCHAR(1000) | 健康描述 | 健康详细信息 |
| personality_desc | VARCHAR(1000) | 性格描述 | 性格文本展示 |
| adopt_requirements | VARCHAR(1000) | 领养要求 | 领养要求展示 |
| status | VARCHAR(16) | 状态：PUBLISHED/APPLYING/ADOPTED | 申请按钮控制 |
| lng, lat | DECIMAL(10,6) | 经纬度 | 距离计算 |
| cover_url | VARCHAR(512) | 封面URL | 封面图展示 |
| published_time | DATETIME | 发布时间 | 时间显示 |

#### 2.1.2 pet_media（宠物媒体表）

| 字段 | 类型 | 说明 | 前端用途 |
|------|------|------|---------|
| id | BIGINT | 主键ID | 唯一标识 |
| pet_id | BIGINT | 宠物ID | 关联宠物 |
| url | VARCHAR(512) | 媒体URL | 图片/视频展示 |
| media_type | VARCHAR(16) | 类型：IMAGE/VIDEO | 媒体类型区分 |
| sort | INT | 排序号 | 图片轮播顺序 |

#### 2.1.3 pet_tag（宠物-标签关联表）

| 字段 | 类型 | 说明 | 前端用途 |
|------|------|------|---------|
| id | BIGINT | 主键ID | 唯一标识 |
| pet_id | BIGINT | 宠物ID | 关联宠物 |
| tag_id | BIGINT | 标签ID | 关联标签 |
| 关联 tag 表 | - | - | 获取标签名称和类型 |

#### 2.1.4 org_profile（机构资料表）

| 字段 | 类型 | 说明 | 前端用途 |
|------|------|------|---------|
| id | BIGINT | 主键ID | 唯一标识 |
| user_id | BIGINT | 机构用户ID | 关联用户 |
| org_name | VARCHAR(128) | 机构名称 | 机构名称显示 |
| contact_name | VARCHAR(64) | 联系人姓名 | 联系人显示 |
| contact_phone | VARCHAR(32) | 联系人电话 | 联系电话显示 |
| address | VARCHAR(255) | 详细地址 | 地址显示 |
| city | VARCHAR(64) | 城市 | 城市显示 |

#### 2.1.5 user_favorite（用户收藏表）

| 字段 | 类型 | 说明 | 前端用途 |
|------|------|------|---------|
| id | BIGINT | 主键ID | 唯一标识 |
| user_id | BIGINT | 用户ID | 关联当前用户 |
| pet_id | BIGINT | 宠物ID | 关联当前宠物 |
| create_time | DATETIME | 创建时间 | 收藏时间显示 |

#### 2.1.6 adoption_application（领养申请表）

| 字段 | 类型 | 说明 | 前端用途 |
|------|------|------|---------|
| id | BIGINT | 主键ID | 申请ID |
| pet_id | BIGINT | 宠物ID | 关联当前宠物 |
| user_id | BIGINT | 用户ID | 关联当前用户 |
| questionnaire_json | JSON | 申请问卷 | 申请表单数据 |
| status | VARCHAR(24) | 申请状态 | 状态显示 |

---

## 3. 接口需求分析

### 3.1 必需接口（3个）

#### 3.1.1 获取宠物详情
- **路径**: `GET /api/pets/:id`
- **权限**: 无需认证
- **优先级**: 🔴 高（核心接口）

#### 3.1.2 收藏/取消收藏
- **路径**: `POST /api/favorites/toggle`
- **权限**: 需认证
- **优先级**: 🟡 中（核心功能）

#### 3.1.3 提交领养申请
- **路径**: `POST /api/applications`
- **权限**: 需认证
- **优先级**: 🔴 高（核心业务）

### 3.2 可选接口（3个）

#### 3.2.1 检查收藏状态
- **路径**: `GET /api/favorites/check?petId=:id`
- **权限**: 需认证
- **优先级**: 🟢 低（优化体验）

#### 3.2.2 获取相似宠物
- **路径**: `GET /api/pets/:id/similar`
- **权限**: 无需认证
- **优先级**: 🟡 中（推荐功能）

#### 3.2.3 记录浏览行为
- **路径**: `POST /api/behavior`
- **权限**: 无需认证
- **优先级**: 🟢 低（推荐数据源）

---

## 4. 页面功能设计

### 4.1 响应式数据定义

```typescript
// 宠物基本信息
const petDetail = ref({
  id: null,
  name: '',
  species: '',
  breed: '',
  gender: '',
  ageMonth: 0,
  ageText: '',
  size: '',
  color: '',
  coverUrl: '',
  images: [],        // 图片数组
  tags: [],          // 标签数组
  healthStatus: {    // 健康状态
    sterilized: false,
    vaccinated: false,
    dewormed: false,
    healthDesc: ''
  },
  personality: {
    desc: '',
    tags: []
  },
  adoptRequirements: '',
  status: '',        // PUBLISHED/APPLYING/ADOPTED
  publishedTime: '',
  distance: 0,       // 距离（km）
  org: {             // 机构信息
    id: null,
    name: '',
    contactName: '',
    contactPhone: '',
    address: '',
    city: ''
  }
})

// 页面状态
const loading = ref(true)
const error = ref(null)

// 用户互动状态
const isFavorited = ref(false)
const favoriteLoading = ref(false)
const applying = ref(false)

// 相似推荐
const similarPets = ref([])
const loadingSimilar = ref(false)

// 图片轮播
const currentImageIndex = ref(0)
```

### 4.2 计算属性

```typescript
// 年龄格式化
const ageText = computed(() => {
  const months = petDetail.value.ageMonth || 0
  if (months < 12) {
    return `${months}个月`
  }
  const years = Math.floor(months / 12)
  const remainingMonths = months % 12
  return remainingMonths > 0 ? `${years}岁${remainingMonths}个月` : `${years}岁`
})

// 性别文本
const genderText = computed(() => {
  const gender = petDetail.value.gender
  return gender === 'MALE' ? '男孩' : gender === 'FEMALE' ? '女孩' : '未知'
})

// 物种文本
const speciesText = computed(() => {
  const species = petDetail.value.species
  return species === 'CAT' ? '猫咪' : species === 'DOG' ? '狗狗' : '其他'
})

// 体型文本
const sizeText = computed(() => {
  const size = petDetail.value.size
  return size === 'S' ? '小型' : size === 'M' ? '中型' : size === 'L' ? '大型' : '未知'
})

// 是否可申请
const canApply = computed(() => {
  return petDetail.value.status === 'PUBLISHED' && !applying.value
})

// 状态标签
const statusTag = computed(() => {
  const status = petDetail.value.status
  const statusMap = {
    'PUBLISHED': { text: '可领养', type: 'success' },
    'APPLYING': { text: '申请中', type: 'warning' },
    'ADOPTED': { text: '已领养', type: 'info' }
  }
  return statusMap[status] || { text: '未知', type: 'default' }
})
```

### 4.3 核心方法

```typescript
// 加载宠物详情
const loadPetDetail = async () => {
  try {
    loading.value = true
    const petId = route.params.id

    // 调用API
    const response = await petAPI.getPetDetail(petId)

    // 数据转换
    petDetail.value = {
      id: response.data.id,
      name: response.data.name || '未命名',
      species: response.data.species,
      breed: response.data.breed || '未知品种',
      gender: response.data.gender,
      ageMonth: response.data.age_month,
      size: response.data.size,
      color: response.data.color,
      coverUrl: response.data.cover_url,
      images: response.data.images || [],
      tags: response.data.tags || [],
      healthStatus: {
        sterilized: response.data.sterilized,
        vaccinated: response.data.vaccinated,
        dewormed: response.data.dewormed,
        healthDesc: response.data.health_desc || ''
      },
      personality: {
        desc: response.data.personality_desc || '',
        tags: response.data.personality_tags || []
      },
      adoptRequirements: response.data.adopt_requirements || '',
      status: response.data.status,
      publishedTime: response.data.published_time,
      distance: response.data.distance || 0,
      org: response.data.org || {}
    }

    // 记录浏览行为
    recordViewBehavior()

    // 加载相似推荐
    loadSimilarPets()

    // 检查收藏状态（如果已登录）
    if (authStore.isLoggedIn) {
      checkFavoriteStatus()
    }
  } catch (error) {
    console.error('加载宠物详情失败:', error)
    error.value = error.message
    ElMessage.error('加载宠物详情失败')
  } finally {
    loading.value = false
  }
}

// 切换收藏状态
const toggleFavorite = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    favoriteLoading.value = true
    await petAPI.toggleFavorite(petDetail.value.id)
    isFavorited.value = !isFavorited.value
    ElMessage.success(isFavorited.value ? '已收藏' : '已取消收藏')
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    favoriteLoading.value = false
  }
}

// 申请领养
const applyForAdoption = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  // 跳转到申请页面
  router.push(`/pets/${petDetail.value.id}/apply`)
}

// 检查收藏状态
const checkFavoriteStatus = async () => {
  try {
    const response = await petAPI.checkFavorite(petDetail.value.id)
    isFavorited.value = response.data.favorited
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 加载相似宠物
const loadSimilarPets = async () => {
  try {
    loadingSimilar.value = true
    const response = await petAPI.getSimilarPets(petDetail.value.id)
    similarPets.value = response.data.list || []
  } catch (error) {
    console.error('加载相似宠物失败:', error)
    similarPets.value = []
  } finally {
    loadingSimilar.value = false
  }
}

// 记录浏览行为
const recordViewBehavior = async () => {
  if (!authStore.isLoggedIn) return

  try {
    await petAPI.recordBehavior({
      petId: petDetail.value.id,
      behaviorType: 'VIEW'
    })
  } catch (error) {
    console.error('记录行为失败:', error)
  }
}

// 分享功能
const sharePet = async () => {
  const shareData = {
    title: `${petDetail.value.name} - 宠物领养`,
    text: `${petDetail.value.name}，${petDetail.value.breed}，${ageText.value}`,
    url: window.location.href
  }

  if (navigator.share) {
    try {
      await navigator.share(shareData)
    } catch (error) {
      console.log('分享取消:', error)
    }
  } else {
    // 复制链接到剪贴板
    await navigator.clipboard.writeText(shareData.url)
    ElMessage.success('链接已复制到剪贴板')
  }
}
```

---

## 5. 接口详细设计

### 5.1 获取宠物详情

#### 接口信息
- **路径**: `GET /api/pets/:id`
- **权限**: 无需认证
- **说明**: 获取宠物的完整详细信息

#### 请求参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|-----|------|
| id | path | Y | 宠物ID（路径参数） |
| lat | query | N | 用户纬度（用于计算距离） |
| lng | query | N | 用户经度（用于计算距离） |

#### 响应数据
```json
{
  "code": 10000,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orgUserId": 10,
    "name": "小橘",
    "species": "CAT",
    "breed": "橘猫",
    "gender": "MALE",
    "ageMonth": 12,
    "size": "M",
    "color": "橘白色",
    "sterilized": 1,
    "vaccinated": 1,
    "dewormed": 1,
    "healthDesc": "健康状况良好，已完成基础疫苗",
    "personalityDesc": "性格温顺，亲人",
    "adoptRequirements": "需要有固定住所，定期回访",
    "status": "PUBLISHED",
    "lng": 116.404,
    "lat": 39.915,
    "coverUrl": "https://example.com/pet1-cover.jpg",
    "publishedTime": "2024-01-15T10:00:00",
    "distance": 5.2,
    "images": [
      {
        "id": 1,
        "url": "https://example.com/pet1-1.jpg",
        "mediaType": "IMAGE",
        "sort": 1
      },
      {
        "id": 2,
        "url": "https://example.com/pet1-2.jpg",
        "mediaType": "IMAGE",
        "sort": 2
      }
    ],
    "tags": [
      {
        "id": 1,
        "name": "亲人",
        "tagType": "PERSONALITY"
      },
      {
        "id": 10,
        "name": "已绝育",
        "tagType": "HEALTH"
      }
    ],
    "org": {
      "id": 1,
      "userId": 10,
      "orgName": "爱心救助站",
      "contactName": "张三",
      "contactPhone": "13800000001",
      "address": "北京市朝阳区xxx路xxx号",
      "city": "北京"
    }
  }
}
```

#### 数据库查询
```sql
-- 主查询
SELECT
  p.*,
  pm.url AS image_url,
  pm.media_type,
  pm.sort,
  t.id AS tag_id,
  t.name AS tag_name,
  t.tag_type,
  op.id AS org_id,
  op.org_name,
  op.contact_name,
  op.contact_phone,
  op.address,
  op.city,
  ST_Distance_Sphere(
    POINT(p.lng, p.lat),
    POINT(:lng, :lat)
  ) / 1000 AS distance_km
FROM pet p
LEFT JOIN pet_media pm ON p.id = pm.pet_id AND pm.deleted = 0
LEFT JOIN pet_tag pt ON p.id = pt.pet_id AND pt.deleted = 0
LEFT JOIN tag t ON pt.tag_id = t.id AND t.deleted = 0
LEFT JOIN org_profile op ON p.org_user_id = op.user_id AND op.deleted = 0
WHERE p.id = :id
  AND p.deleted = 0
ORDER BY pm.sort, t.id;
```

---

### 5.2 收藏/取消收藏

#### 接口信息
- **路径**: `POST /api/favorites/toggle`
- **权限**: 需认证
- **说明**: 切换收藏状态，已收藏则取消，未收藏则添加

#### 请求体
```json
{
  "petId": 1
}
```

#### 响应数据
```json
{
  "code": 10000,
  "message": "操作成功",
  "data": {
    "favorited": true,
    "favoriteId": 100
  }
}
```

#### 业务规则
- 同一用户同一宠物只能收藏一次
- 如果已收藏，则取消收藏（软删除）
- 如果未收藏，则添加收藏记录

---

### 5.3 提交领养申请

#### 接口信息
- **路径**: `POST /api/applications`
- **权限**: 需认证
- **说明**: 提交领养申请

#### 请求体
```json
{
  "petId": 1,
  "questionnaire": {
    "hasExperience": true,
    "housingType": "apartment",
    "hasYard": false,
    "hasOtherPets": false,
    "hasChildren": false,
    "workHours": "8",
    "reason": "非常喜欢这只宠物"
  }
}
```

#### 响应数据
```json
{
  "code": 10000,
  "message": "申请提交成功",
  "data": {
    "applicationId": 200,
    "status": "SUBMITTED"
  }
}
```

#### 业务规则
- 同一用户同一宠物不能重复申请（数据库唯一约束）
- 宠物状态必须为 PUBLISHED（可领养）
- 申请后宠物状态可能变更为 APPLYING

---

### 5.4 检查收藏状态

#### 接口信息
- **路径**: `GET /api/favorites/check`
- **权限**: 需认证
- **说明**: 检查用户是否已收藏指定宠物

#### 请求参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|-----|------|
| petId | query | Y | 宠物ID |

#### 响应数据
```json
{
  "code": 10000,
  "message": "操作成功",
  "data": {
    "favorited": true,
    "favoriteId": 100
  }
}
```

---

### 5.5 获取相似宠物

#### 接口信息
- **路径**: `GET /api/pets/:id/similar`
- **权限**: 无需认证
- **说明**: 基于物种、品种、年龄等维度推荐相似宠物

#### 请求参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|-----|------|
| id | path | Y | 宠物ID（路径参数） |
| limit | query | N | 返回数量，默认6，最大12 |
| lat | query | N | 用户纬度（用于距离过滤） |
| lng | query | N | 用户经度（用于距离过滤） |

#### 响应数据
```json
{
  "code": 10000,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 2,
        "name": "咪咪",
        "species": "CAT",
        "breed": "英短",
        "gender": "FEMALE",
        "ageMonth": 10,
        "coverUrl": "https://example.com/pet2-cover.jpg",
        "distance": 3.5,
        "matchScore": 85
      }
    ]
  }
}
```

#### 推荐算法
1. **同物种**：物种相同
2. **同品种**：品种相同或相近
3. **年龄相近**：年龄相差不超过 12 个月
4. **距离优先**：距离用户 50km 以内
5. **排除自身**：不推荐当前宠物

---

### 5.6 记录浏览行为

#### 接口信息
- **路径**: `POST /api/behavior`
- **权限**: 无需认证
- **说明**: 记录用户浏览行为，用于推荐算法

#### 请求体
```json
{
  "petId": 1,
  "behaviorType": "VIEW"
}
```

#### 行为类型
| 类型 | 说明 | 权重 |
|------|------|------|
| VIEW | 浏览 | 1 |
| FAVORITE | 收藏 | 5 |
| APPLY | 申请 | 10 |
| SHARE | 分享 | 3 |

#### 响应数据
```json
{
  "code": 10000,
  "message": "记录成功"
}
```

#### 业务规则
- 未登录用户不记录
- 同一用户同一宠物同一行为，24小时内只记录一次
- 用于协同过滤推荐算法

---

## 6. 开发任务分解

### 6.1 Phase 1: 基础展示（核心功能）

#### Task 1.1: API 接口开发
- [ ] 实现 `GET /api/pets/:id` 接口
- [ ] 实现图片、标签、机构信息关联查询
- [ ] 实现距离计算（基于经纬度）
- [ ] 添加接口文档和 JSDoc 注释

#### Task 1.2: 前端页面开发
- [ ] 创建 PetDetail.vue 页面组件
- [ ] 实现宠物基本信息展示
- [ ] 实现图片轮播功能（使用 Element Plus Carousel）
- [ ] 实现健康状态标签展示
- [ ] 实现性格特征标签展示
- [ ] 实现机构信息展示
- [ ] 添加加载状态和错误处理

#### Task 1.3: 样式开发
- [ ] 实现响应式布局（移动端优先）
- [ ] 实现图片轮播样式
- [ ] 实现标签样式
- [ ] 实现机构信息卡片样式

---

### 6.2 Phase 2: 互动功能（核心业务）

#### Task 2.1: 收藏功能
- [ ] 实现 `POST /api/favorites/toggle` 接口
- [ ] 实现前端收藏按钮组件
- [ ] 添加收藏状态检查接口
- [ ] 实现收藏动画效果

#### Task 2.2: 领养申请
- [ ] 实现 `POST /api/applications` 接口
- [ ] 实现申请领养按钮
- [ ] 跳转到申请表单页面
- [ ] 实现申请状态检查（已申请则禁用按钮）

#### Task 2.3: 分享功能
- [ ] 实现分享按钮
- [ ] 实现 Web Share API
- [ ] 实现复制链接到剪贴板功能

---

### 6.3 Phase 3: 推荐功能（优化体验）

#### Task 3.1: 相似宠物推荐
- [ ] 实现 `GET /api/pets/:id/similar` 接口
- [ ] 实现推荐算法（基于物种、品种、年龄、距离）
- [ ] 实现前端相似宠物横向滚动列表
- [ ] 集成 PetCard 组件

#### Task 3.2: 行为埋点
- [ ] 实现 `POST /api/behavior` 接口
- [ ] 实现浏览行为记录
- [ ] 实现收藏行为记录
- [ ] 实现分享行为记录

---

### 6.4 Phase 4: 优化与测试

#### Task 4.1: 性能优化
- [ ] 图片懒加载
- [ ] 接口数据缓存（使用 Pinia）
- [ ] 路由懒加载

#### Task 4.2: 用户体验优化
- [ ] 添加骨架屏（Skeleton）
- [ ] 添加空状态展示
- [ ] 添加错误提示和重试
- [ ] 添加加载动画

#### Task 4.3: 测试
- [ ] 单元测试（API 接口）
- [ ] 集成测试（页面功能）
- [ ] 端到端测试（完整流程）

---

## 7. 技术实现要点

### 7.1 API 接口实现

#### 文件位置
`src/api/modules/pet.js`

#### 接口方法
```javascript
import http from '@/api/request.js'

/**
 * 获取宠物详情
 * @param {number} id - 宠物ID
 * @param {object} params - 可选参数（lat, lng）
 * @returns {Promise} 宠物详情数据
 */
export function getPetDetail(id, params = {}) {
  return http.get(`/pets/${id}`, params)
}

/**
 * 切换收藏状态
 * @param {number} petId - 宠物ID
 * @returns {Promise} 收藏状态
 */
export function toggleFavorite(petId) {
  return http.post('/favorites/toggle', { petId })
}

/**
 * 检查收藏状态
 * @param {number} petId - 宠物ID
 * @returns {Promise} 收藏状态
 */
export function checkFavorite(petId) {
  return http.get('/favorites/check', { petId })
}

/**
 * 提交领养申请
 * @param {object} data - 申请数据
 * @returns {Promise} 申请结果
 */
export function applyForAdoption(data) {
  return http.post('/applications', data)
}

/**
 * 获取相似宠物
 * @param {number} id - 宠物ID
 * @param {object} params - 可选参数
 * @returns {Promise} 相似宠物列表
 */
export function getSimilarPets(id, params = {}) {
  return http.get(`/pets/${id}/similar`, params)
}

/**
 * 记录浏览行为
 * @param {object} data - 行为数据
 * @returns {Promise} 操作结果
 */
export function recordBehavior(data) {
  return http.post('/behavior', data)
}
```

### 7.2 前端组件结构

#### PetDetail.vue 完整结构
```vue
<template>
  <AppLayout>
    <!-- 骨架屏（加载中） -->
    <el-skeleton v-if="loading" :rows="10" animated />

    <!-- 错误状态 -->
    <el-empty v-else-if="error" description="加载失败，请重试" />

    <!-- 正常内容 -->
    <template v-else>
      <PageHeader title="宠物详情">
        <template #actions>
          <el-button @click="sharePet">分享</el-button>
        </template>
      </PageHeader>

      <div class="pet-detail-container">
        <!-- 图片轮播 -->
        <el-carousel :autoplay="false" arrow="always">
          <el-carousel-item v-for="(image, index) in petDetail.images" :key="index">
            <el-image :src="image.url" fit="cover" />
          </el-carousel-item>
        </el-carousel>

        <!-- 宠物基本信息 -->
        <div class="pet-info-section">
          <div class="pet-header">
            <h1>{{ petDetail.name }}</h1>
            <el-tag :type="statusTag.type">{{ statusTag.text }}</el-tag>
          </div>

          <div class="pet-meta">
            <span>{{ speciesText }}</span>
            <span>{{ petDetail.breed }}</span>
            <span>{{ genderText }}</span>
            <span>{{ ageText }}</span>
          </div>
        </div>

        <!-- 健康状态 -->
        <div class="health-section">
          <h3>健康状态</h3>
          <div class="tags">
            <el-tag v-if="petDetail.healthStatus.sterilized" type="success">已绝育</el-tag>
            <el-tag v-if="petDetail.healthStatus.vaccinated" type="success">已疫苗</el-tag>
            <el-tag v-if="petDetail.healthStatus.dewormed" type="success">已驱虫</el-tag>
          </div>
          <p>{{ petDetail.healthStatus.healthDesc }}</p>
        </div>

        <!-- 性格特征 -->
        <div class="personality-section">
          <h3>性格特征</h3>
          <div class="tags">
            <el-tag v-for="tag in petDetail.tags" :key="tag.id">
              {{ tag.name }}
            </el-tag>
          </div>
          <p>{{ petDetail.personality.desc }}</p>
        </div>

        <!-- 领养要求 -->
        <div class="requirements-section">
          <h3>领养要求</h3>
          <p>{{ petDetail.adoptRequirements }}</p>
        </div>

        <!-- 机构信息 -->
        <div class="org-section">
          <h3>发布机构</h3>
          <div class="org-info">
            <p><strong>{{ petDetail.org.orgName }}</strong></p>
            <p>联系人：{{ petDetail.org.contactName }}</p>
            <p>电话：{{ petDetail.org.contactPhone }}</p>
            <p>地址：{{ petDetail.org.address }}</p>
          </div>
        </div>

        <!-- 相似推荐 -->
        <div class="similar-section">
          <h3>相似推荐</h3>
          <div class="similar-pets">
            <PetCard
              v-for="pet in similarPets"
              :key="pet.id"
              :pet="pet"
            />
          </div>
        </div>
      </div>

      <!-- 底部操作栏（固定） -->
      <div class="bottom-actions">
        <el-button @click="toggleFavorite" :loading="favoriteLoading">
          {{ isFavorited ? '已收藏' : '收藏' }}
        </el-button>
        <el-button
          type="primary"
          @click="applyForAdoption"
          :disabled="!canApply"
          :loading="applying"
        >
          申请领养
        </el-button>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
// ... 代码实现
</script>

<style scoped>
/* ... 样式实现 */
</style>
```

### 7.3 样式要点

#### 响应式设计
```scss
.pet-detail-container {
  padding: 0 16px;
  max-width: 600px;
  margin: 0 auto;
}

@media (min-width: 768px) {
  .pet-detail-container {
    padding: 0 24px;
    max-width: 800px;
  }
}

// 图片轮播
.el-carousel {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}

// 底部操作栏（移动端固定）
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 12px 16px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 12px;

  @media (min-width: 768px) {
    position: static;
    padding: 24px 0;
    box-shadow: none;
  }
}
```

### 7.4 性能优化

#### 图片懒加载
```vue
<el-image
  :src="image.url"
  fit="cover"
  :lazy="true"
  :preview-src-list="petDetail.images.map(img => img.url)"
/>
```

#### 数据缓存
```javascript
// 使用 Pinia 缓存宠物详情
const petStore = usePetStore()

const loadPetDetail = async () => {
  const petId = route.params.id

  // 先从缓存读取
  if (petStore.getPetById(petId)) {
    petDetail.value = petStore.getPetById(petId)
    return
  }

  // 缓存不存在，请求API
  const response = await petAPI.getPetDetail(petId)
  petDetail.value = response.data
  petStore.setPet(petId, response.data)
}
```

---

## 8. 总结

### 8.1 开发优先级

| 优先级 | 功能模块 | 说明 |
|--------|---------|------|
| P0 | 获取宠物详情 | 核心接口，必须优先实现 |
| P0 | 收藏/取消收藏 | 核心功能，必须优先实现 |
| P0 | 领养申请 | 核心业务，必须优先实现 |
| P1 | 相似宠物推荐 | 优化体验，可延后实现 |
| P1 | 行为埋点 | 推荐数据源，可延后实现 |
| P2 | 检查收藏状态 | 优化体验，可选实现 |

### 8.2 技术风险

| 风险项 | 风险等级 | 解决方案 |
|--------|---------|---------|
| 图片加载性能 | 中 | 使用懒加载 + CDN |
| 距离计算精度 | 低 | 使用 ST_Distance_Sphere 函数 |
| 推荐算法准确性 | 中 | 先实现简单规则，后续优化 |
| 移动端兼容性 | 低 | 使用 Element Plus 响应式组件 |

### 8.3 后续优化方向

1. **推荐算法优化**：引入协同过滤和深度学习
2. **AR/VR 展示**：3D 宠物模型展示
3. **视频通话**：与机构视频沟通
4. **智能问答**：AI 宠物咨询助手
5. **社交功能**：宠物社区、评论互动

---

**文档版本**: v1.0
**最后更新**: 2026-02-14
**负责人**: 开发团队