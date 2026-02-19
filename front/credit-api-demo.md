# 信用相关接口JSON Demo文档

## 1. 获取个人信用账户摘要

### 接口信息
- **路径**: `GET /api/user/credit/summary`
- **方法**: GET
- **权限**: 需要登录
- **用途**: 获取用户信用账户的摘要信息，用于展示信用看板

### 请求示例
```http
GET /api/user/credit/summary
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 响应示例
```json
{
  "code": 10000,
  "message": "操作成功",
  "data": {
    "score": 850,
    "levelName": "资深领养人",
    "levelIcon": "shield-check",
    "levelColor": "#FF8C42",
    "totalCheckins": 45,
    "consecutiveCheckins": 7,
    "nextLevelName": "金牌领养人",
    "nextLevelThreshold": 1000,
    "nextLevelProgress": 85,
    "ranking": "Top 5%",
    "totalUsers": 1250,
    "rank": 63,
    "recentChange": 15,
    "recentChangeReason": "连续7天打卡",
    "lastCheckinDate": "2026-02-17",
    "createdAt": "2024-01-15",
    "updatedAt": "2026-02-17"
  },
  "timestamp": "2026-02-18T09:15:30"
}
```

### 字段说明
| 字段名 | 类型 | 说明 |
|--------|------|------|
| score | number | 当前信用分数 |
| levelName | string | 当前等级名称 |
| levelIcon | string | 等级图标标识 |
| levelColor | string | 等级颜色代码 |
| totalCheckins | number | 总打卡天数 |
| consecutiveCheckins | number | 连续打卡天数 |
| nextLevelName | string | 下一等级名称 |
| nextLevelThreshold | number | 下一等级所需分数 |
| nextLevelProgress | number | 到下一等级的进度百分比 |
| ranking | string | 排名百分比 |
| totalUsers | number | 总用户数 |
| rank | number | 当前排名 |
| recentChange | number | 最近变动分数 |
| recentChangeReason | string | 最近变动原因 |
| lastCheckinDate | string | 最后打卡日期 |
| createdAt | string | 账户创建时间 |
| updatedAt | string | 最后更新时间 |

---

## 2. 获取信用积分变更流水

### 接口信息
- **路径**: `GET /api/user/credit/logs`
- **方法**: GET
- **权限**: 需要登录
- **用途**: 获取用户信用积分的变更记录，用于展示积分明细

### 请求示例
```http
GET /api/user/credit/logs?pageNo=1&pageSize=10
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 响应示例
```json
{
  "code": 10000,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 12345,
        "userId": 789,
        "delta": 10,
        "reason": "每日打卡奖励",
        "type": "CHECKIN",
        "relatedId": 456,
        "relatedType": "CHECKIN_POST",
        "createTime": "2026-02-17T20:30:00",
        "balance": 850,
        "metadata": {
          "petName": "小橘",
          "checkinId": 456,
          "imageCount": 3
        }
      },
      {
        "id": 12344,
        "userId": 789,
        "delta": 5,
        "reason": "连续打卡奖励",
        "type": "STREAK",
        "relatedId": null,
        "relatedType": null,
        "createTime": "2026-02-17T20:30:00",
        "balance": 840,
        "metadata": {
          "streakDays": 7
        }
      },
      {
        "id": 12343,
        "userId": 789,
        "delta": -5,
        "reason": "逾期未打卡",
        "type": "OVERDUE",
        "relatedId": null,
        "relatedType": null,
        "createTime": "2026-02-15T10:00:00",
        "balance": 835,
        "metadata": {
          "overdueDays": 2
        }
      },
      {
        "id": 12342,
        "userId": 789,
        "delta": 15,
        "reason": "疫苗接种打卡",
        "type": "VACCINE",
        "relatedId": 455,
        "relatedType": "CHECKIN_POST",
        "createTime": "2026-02-14T15:20:00",
        "balance": 840,
        "metadata": {
          "petName": "小橘",
          "checkinId": 455,
          "vaccineType": "三联疫苗"
        }
      },
      {
        "id": 12341,
        "userId": 789,
        "delta": 10,
        "reason": "每日打卡奖励",
        "type": "CHECKIN",
        "relatedId": 454,
        "relatedType": "CHECKIN_POST",
        "createTime": "2026-02-13T09:15:00",
        "balance": 825,
        "metadata": {
          "petName": "小橘",
          "checkinId": 454,
          "imageCount": 2
        }
      }
    ],
    "pageNo": 1,
    "pageSize": 10,
    "total": 45,
    "totalPages": 5,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2026-02-18T09:15:30"
}
```

### 字段说明

#### 响应数据结构
| 字段名 | 类型 | 说明 |
|--------|------|------|
| list | array | 信用记录列表 |
| pageNo | number | 当前页码 |
| pageSize | number | 每页条数 |
| total | number | 总记录数 |
| totalPages | number | 总页数 |
| hasNext | boolean | 是否有下一页 |
| hasPrevious | boolean | 是否有上一页 |

#### 信用记录对象
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | number | 记录ID |
| userId | number | 用户ID |
| delta | number | 积分变动值（正数为增加，负数为减少） |
| reason | string | 变动原因描述 |
| type | string | 变动类型（CHECKIN/STREAK/OVERDUE/VACCINE等） |
| relatedId | number/null | 关联记录ID |
| relatedType | string/null | 关联记录类型 |
| createTime | string | 记录创建时间 |
| balance | number | 变动后的积分余额 |
| metadata | object | 额外元数据，根据type不同而变化 |

### 变动类型说明
| 类型 | 说明 | 示例原因 |
|------|------|----------|
| CHECKIN | 日常打卡 | "每日打卡奖励" |
| STREAK | 连续打卡奖励 | "连续打卡奖励" |
| OVERDUE | 逾期未打卡 | "逾期未打卡" |
| VACCINE | 疫苗接种打卡 | "疫苗接种打卡" |
| ADOPTION | 成功领养 | "成功领养奖励" |
| REPORT | 举报奖励 | "举报违规内容" |
| SYSTEM | 系统调整 | "系统积分调整" |

### 请求参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| pageNo | number | 否 | 1 | 页码，从1开始 |
| pageSize | number | 否 | 10 | 每页条数，最大50 |
| type | string | 否 | - | 筛选特定类型的记录 |
| startDate | string | 否 | - | 开始日期，格式：YYYY-MM-DD |
| endDate | string | 否 | - | 结束日期，格式：YYYY-MM-DD |

### 错误响应示例
```json
{
  "code": 1001,
  "message": "用户未登录",
  "data": null,
  "timestamp": "2026-02-18T09:15:30"
}
```

### 前端调用示例

#### 获取信用摘要
```javascript
import { getCreditSummary } from '@/api/modules/checkin.js'

const fetchCreditSummary = async () => {
  try {
    const res = await getCreditSummary()
    console.log('信用摘要:', res.data)
    // 使用 res.data 更新信用看板
  } catch (error) {
    console.error('获取信用摘要失败:', error)
  }
}
```

#### 获取信用流水
```javascript
import { getCreditLogs } from '@/api/modules/checkin.js'

const fetchCreditLogs = async (page = 1) => {
  try {
    const res = await getCreditLogs({
      pageNo: page,
      pageSize: 10
    })
    console.log('信用流水:', res.data)
    // 使用 res.data.list 更新积分明细列表
  } catch (error) {
    console.error('获取信用流水失败:', error)
  }
}
```

### 注意事项
1. 所有时间字段均为ISO 8601格式
2. 积分变动类型可以根据业务需求扩展
3. metadata字段为可选，根据具体变动类型提供额外信息
4. 分页参数遵循统一规范，最大pageSize为50
5. 接口需要有效的JWT token进行身份验证