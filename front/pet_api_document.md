# PetController 前端对接文档

## 接口概览
- **基础路径**: `/api`
- **接口分类**: 宠物管理
- **功能描述**: 宠物档案相关接口

---

## 1. 获取宠物列表

### 接口信息
- **路径**: `GET /api/pets`
- **功能**: 获取公开的宠物列表，支持筛选和分页
- **权限**: 游客/用户通用，无需认证
- **描述**: 获取宠物列表，支持按物种、体型、年龄等条件筛选

### 请求参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| species | String | 否 | - | 物种（CAT/DOG/OTHER） |
| size | String | 否 | - | 体型（S/M/L） |
| gender | String | 否 | - | 性别（MALE/FEMALE/UNKNOWN） |
| ageMin | Integer | 否 | - | 最小年龄（月） |
| ageMax | Integer | 否 | - | 最大年龄（月） |
| status | String | 否 | PUBLISHED | 宠物状态 |
| lng | Double | 否 | - | 经度（用于距离排序） |
| lat | Double | 否 | - | 纬度（用于距离排序） |
| distanceKm | Integer | 否 | - | 距离范围（公里） |
| pageNo | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| sortBy | String | 否 | distance | 排序字段（distance/createTime） |
| order | String | 否 | asc | 排序方向（asc/desc） |

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orgName": "爱心救助站",
        "name": "小白",
        "species": "CAT",
        "breed": "英短",
        "gender": "FEMALE",
        "ageMonth": 6,
        "size": "S",
        "color": "白色",
        "sterilized": 1,
        "vaccinated": 1,
        "dewormed": 1,
        "healthDesc": "身体健康",
        "personalityDesc": "温顺亲人",
        "adoptRequirements": "有爱心，能定期回访",
        "status": "PUBLISHED",
        "lng": 116.3972,
        "lat": 39.9075,
        "coverUrl": "https://example.com/images/pet1.jpg",
        "publishedTime": "2024-01-15 10:30:00",
        "createTime": "2024-01-10 09:15:00",
        "distanceKm": 2.5
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### Vue 前端调用示例
```typescript
import { axiosInstance } from '@/utils/request'

interface PetQueryParams {
  species?: string
  size?: string
  gender?: string
  ageMin?: number
  ageMax?: number
  status?: string
  lng?: number
  lat?: number
  distanceKm?: number
  pageNo?: number
  pageSize?: number
  sortBy?: string
  order?: string
}

interface PetListResponse {
  code: number
  message: string
  data: {
    records: Array<{
      id: number
      orgName: string
      name: string
      species: string
      breed: string
      gender: string
      ageMonth: number
      size: string
      color: string
      sterilized: number
      vaccinated: number
      dewormed: number
      healthDesc: string
      personalityDesc: string
      adoptRequirements: string
      status: string
      lng: number
      lat: number
      coverUrl: string
      publishedTime: string
      createTime: string
      distanceKm: number
    }>
    total: number
    size: number
    current: number
    pages: number
  }
}

export const getPetList = (params: PetQueryParams): Promise<PetListResponse> => {
  return axiosInstance.get('/api/pets', { params })
}

// 使用示例
const fetchPetList = async () => {
  try {
    const params: PetQueryParams = {
      species: 'CAT',
      pageNo: 1,
      pageSize: 10
    }
    const response = await getPetList(params)
    console.log('宠物列表:', response.data)
  } catch (error) {
    console.error('获取宠物列表失败:', error)
  }
}
```

---

## 2. 获取宠物详情

### 接口信息
- **路径**: `GET /api/pets/{petId}`
- **功能**: 获取指定宠物的详细信息
- **权限**: 游客/用户通用，无需认证
- **描述**: 根据宠物ID获取宠物的详细信息

### 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| petId | Long | 是 | 宠物ID |

### 查询参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| lng | Double | 否 | - | 用户当前位置经度（用于计算距离） |
| lat | Double | 否 | - | 用户当前位置纬度（用于计算距离） |

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "orgName": "爱心救助站",
    "orgContactPhone": "13800138000",
    "name": "小白",
    "species": "CAT",
    "breed": "英短",
    "gender": "FEMALE",
    "ageMonth": 6,
    "size": "S",
    "color": "白色",
    "sterilized": 1,
    "vaccinated": 1,
    "dewormed": 1,
    "healthDesc": "身体健康",
    "personalityDesc": "温顺亲人",
    "adoptRequirements": "有爱心，能定期回访",
    "status": "PUBLISHED",
    "lng": 116.3972,
    "lat": 39.9075,
    "coverUrl": "https://example.com/images/pet1.jpg",
    "mediaUrls": [
      "https://example.com/images/pet1_1.jpg",
      "https://example.com/images/pet1_2.jpg"
    ],
    "tags": [
      {
        "id": 1,
        "name": "亲人",
        "type": "PERSONALITY"
      }
    ],
    "publishedTime": "2024-01-15 10:30:00",
    "createTime": "2024-01-10 09:15:00",
    "distanceKm": 2.5
  }
}
```

### Vue 前端调用示例
```typescript
import { axiosInstance } from '@/utils/request'

interface GetPetDetailParams {
  lng?: number
  lat?: number
}

interface PetDetailResponse {
  code: number
  message: string
  data: {
    id: number
    orgName: string
    orgContactPhone: string
    name: string
    species: string
    breed: string
    gender: string
    ageMonth: number
    size: string
    color: string
    sterilized: number
    vaccinated: number
    dewormed: number
    healthDesc: string
    personalityDesc: string
    adoptRequirements: string
    status: string
    lng: number
    lat: number
    coverUrl: string
    mediaUrls: string[]
    tags: Array<{
      id: number
      name: string
      type: string
    }>
    publishedTime: string
    createTime: string
    distanceKm: number
  }
}

export const getPetDetail = (petId: number, params?: GetPetDetailParams): Promise<PetDetailResponse> => {
  return axiosInstance.get(`/api/pets/${petId}`, { params })
}

// 使用示例
const fetchPetDetail = async (petId: number) => {
  try {
    const params: GetPetDetailParams = {
      lng: 116.3972,
      lat: 39.9075
    }
    const response = await getPetDetail(petId, params)
    console.log('宠物详情:', response.data)
  } catch (error) {
    console.error('获取宠物详情失败:', error)
  }
}
```

---

## 3. 机构创建宠物档案

### 接口信息
- **路径**: `POST /api/org/pets`
- **功能**: 机构创建新的宠物档案
- **权限**: 需要 `pet:create` 权限（机构用户）
- **描述**: 机构用户创建宠物档案，需要通过审核后才能发布

### 请求头
- **Authorization**: Bearer {token} (JWT Token)

### 请求体
```json
{
  "name": "小花",
  "species": "CAT",
  "breed": "田园猫",
  "gender": "FEMALE",
  "ageMonth": 6,
  "size": "S",
  "color": "三花",
  "sterilized": 0,
  "vaccinated": 1,
  "dewormed": 1,
  "healthDesc": "健康活泼",
  "personalityDesc": "活泼好动，喜欢与人互动",
  "adoptRequirements": "有养猫经验，能定期回访",
  "coverUrl": "https://example.com/images/pet_cover.jpg",
  "mediaUrls": [
    "https://example.com/images/pet1.jpg",
    "https://example.com/images/pet2.jpg"
  ],
  "tagIds": [1, 3, 5]
}
```

### 请求参数说明
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 否 | 宠物名字/昵称 |
| species | String | 是 | 物种（CAT/DOG/OTHER） |
| breed | String | 否 | 品种 |
| gender | String | 否 | 性别（MALE/FEMALE/UNKNOWN） |
| ageMonth | Integer | 否 | 年龄（月） |
| size | String | 否 | 体型（S/M/L） |
| color | String | 否 | 毛色/颜色 |
| sterilized | Integer | 否 | 是否绝育（0否1是） |
| vaccinated | Integer | 否 | 是否疫苗（0否1是） |
| dewormed | Integer | 否 | 是否驱虫（0否1是） |
| healthDesc | String | 否 | 健康描述 |
| personalityDesc | String | 否 | 性格描述 |
| adoptRequirements | String | 否 | 领养要求 |
| coverUrl | String | 是 | 封面图片URL |
| mediaUrls | Array | 否 | 媒体文件URL数组 |
| tagIds | Array | 否 | 标签ID数组 |

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "status": "DRAFT",
    "auditStatus": "NONE"
  }
}
```

### Vue 前端调用示例
```typescript
import { axiosInstance } from '@/utils/request'

interface PetCreateRequest {
  name?: string
  species: string
  breed?: string
  gender?: string
  ageMonth?: number
  size?: string
  color?: string
  sterilized?: number
  vaccinated?: number
  dewormed?: number
  healthDesc?: string
  personalityDesc?: string
  adoptRequirements?: string
  coverUrl: string
  mediaUrls?: string[]
  tagIds?: number[]
}

interface PetCreateResponse {
  code: number
  message: string
  data: {
    id: number
    status: string
    auditStatus: string
  }
}

export const createPet = (data: PetCreateRequest): Promise<PetCreateResponse> => {
  return axiosInstance.post('/api/org/pets', data)
}

// 使用示例
const createNewPet = async () => {
  try {
    const petData: PetCreateRequest = {
      name: "小花",
      species: "CAT",
      breed: "田园猫",
      gender: "FEMALE",
      ageMonth: 6,
      size: "S",
      color: "三花",
      sterilized: 0,
      vaccinated: 1,
      dewormed: 1,
      healthDesc: "健康活泼",
      personalityDesc: "活泼好动，喜欢与人互动",
      adoptRequirements: "有养猫经验，能定期回访",
      coverUrl: "https://example.com/images/pet_cover.jpg",
      mediaUrls: [
        "https://example.com/images/pet1.jpg",
        "https://example.com/images/pet2.jpg"
      ],
      tagIds: [1, 3, 5]
    }
    
    const response = await createPet(petData)
    console.log('创建成功:', response.data)
  } catch (error) {
    console.error('创建宠物档案失败:', error)
  }
}
```

---

## 通用响应格式说明

所有接口遵循统一的响应格式：

```typescript
interface ApiResponse<T> {
  code: number      // 状态码，200表示成功
  message: string   // 响应消息
  data: T           // 具体数据
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或Token失效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. 所有接口都需要在请求头中携带 Authorization: Bearer {token}（除了公开接口）
2. 分页参数从1开始计数
3. 距离计算需要提供用户当前位置的经纬度
4. 机构相关接口需要对应权限