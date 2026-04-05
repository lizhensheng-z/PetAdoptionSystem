# 宠物领养申请流程API请求示例

本文档提供了完整的宠物领养申请流程JSON请求示例，涵盖从用户提交申请到机构完成审核的全过程。

## 1. 用户提交领养申请

### 请求示例
```http
POST /api/adoption/applications
Content-Type: application/json

{
  "petId": 12345,
  "questionnaire": {
    "experience": "有养猫经验，曾养过2只猫",
    "housingType": "自有住房",
    "familyMembers": 3,
    "otherPets": "无其他宠物",
    "dailyTime": "每天2-3小时",
    "reason": "希望给猫咪一个温暖的家",
    "contactPhone": "13800138000",
    "contactWechat": "catlover2026"
  }
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "applicationId": 67890,
    "petId": 12345,
    "userId": 10001,
    "status": "SUBMITTED",
    "submitTime": "2026-02-16T09:00:00",
    "estimatedReviewTime": "3-7天内"
  }
}
```

## 2. 用户查看我的申请列表

### 请求示例
```http
GET /api/adoption/applications/me?pageNo=1&pageSize=10&status=SUBMITTED&sortBy=create_time&order=desc
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 67890,
        "petName": "橘猫小橘",
        "petCoverUrl": "https://example.com/cat1.jpg",
        "status": "SUBMITTED",
        "statusDesc": "已提交",
        "submitTime": "2026-02-16T09:00:00",
        "canCancel": true,
        "canModify": true
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

## 3. 用户查看申请详情

### 请求示例
```http
GET /api/adoption/applications/67890
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 67890,
    "petId": 12345,
    "petName": "橘猫小橘",
    "petCoverUrl": "https://example.com/cat1.jpg",
    "userId": 10001,
    "userName": "张三",
    "userAvatar": "https://example.com/avatar.jpg",
    "userPhone": "13800138000",
    "userEmail": "zhangsan@example.com",
    "status": "SUBMITTED",
    "statusDesc": "已提交",
    "submitTime": "2026-02-16T09:00:00",
    "questionnaire": {
      "experience": "有养猫经验，曾养过2只猫",
      "housingType": "自有住房",
      "familyMembers": 3,
      "otherPets": "无其他宠物",
      "dailyTime": "每天2-3小时",
      "reason": "希望给猫咪一个温暖的家",
      "contactPhone": "13800138000",
      "contactWechat": "catlover2026"
    },
    "canCancel": true,
    "canModify": true
  }
}
```

## 4. 用户取消申请

### 请求示例
```http
POST /api/adoption/applications/67890/cancel
Content-Type: application/json

{
  "reason": "个人原因，暂时无法领养"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 5. 机构查看申请列表

### 请求示例
```http
GET /api/org/adoption/applications?petId=12345&status=SUBMITTED&keyword=张三&pageNo=1&pageSize=10&sortBy=create_time&order=desc
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 67890,
        "petName": "橘猫小橘",
        "petCoverUrl": "https://example.com/cat1.jpg",
        "applicantName": "张三",
        "applicantPhone": "13800138000",
        "status": "SUBMITTED",
        "statusDesc": "已提交",
        "submitTime": "2026-02-16T09:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

## 6. 机构查看申请详情

### 请求示例
```http
GET /api/org/adoption/applications/67890
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 67890,
    "petId": 12345,
    "petName": "橘猫小橘",
    "petCoverUrl": "https://example.com/cat1.jpg",
    "userId": 10001,
    "userName": "张三",
    "userAvatar": "https://example.com/avatar.jpg",
    "userPhone": "13800138000",
    "userEmail": "zhangsan@example.com",
    "status": "SUBMITTED",
    "statusDesc": "已提交",
    "submitTime": "2026-02-16T09:00:00",
    "questionnaire": {
      "experience": "有养猫经验，曾养过2只猫",
      "housingType": "自有住房",
      "familyMembers": 3,
      "otherPets": "无其他宠物",
      "dailyTime": "每天2-3小时",
      "reason": "希望给猫咪一个温暖的家",
      "contactPhone": "13800138000",
      "contactWechat": "catlover2026"
    },
    "canCancel": false,
    "canModify": true
  }
}
```

## 7. 机构更新申请状态 - 开始审核

### 请求示例
```http
POST /api/org/adoption/applications/67890/status
Content-Type: application/json

{
  "toStatus": "UNDER_REVIEW",
  "remark": "开始审核申请材料，用户养猫经验丰富"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "applicationId": 67890,
    "fromStatus": "SUBMITTED",
    "toStatus": "UNDER_REVIEW",
    "updateTime": "2026-02-16T10:30:00"
  }
}
```

## 8. 机构更新申请状态 - 安排面谈

### 请求示例
```http
POST /api/org/adoption/applications/67890/status
Content-Type: application/json

{
  "toStatus": "INTERVIEW",
  "interviewTime": "2026-02-20T14:00:00",
  "interviewLocation": "宠物之家领养中心",
  "remark": "材料审核通过，安排面谈了解详细情况"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "applicationId": 67890,
    "fromStatus": "UNDER_REVIEW",
    "toStatus": "INTERVIEW",
    "updateTime": "2026-02-16T11:00:00"
  }
}
```

## 9. 机构更新申请状态 - 安排家访

### 请求示例
```http
POST /api/org/adoption/applications/67890/status
Content-Type: application/json

{
  "toStatus": "HOME_VISIT",
  "remark": "面谈通过，安排家访考察居住环境"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "applicationId": 67890,
    "fromStatus": "INTERVIEW",
    "toStatus": "HOME_VISIT",
    "updateTime": "2026-02-16T15:00:00"
  }
}
```

## 10. 机构更新申请状态 - 审核通过

### 请求示例
```http
POST /api/org/adoption/applications/67890/status
Content-Type: application/json

{
  "toStatus": "APPROVED",
  "remark": "家访合格，同意领养申请"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "applicationId": 67890,
    "fromStatus": "HOME_VISIT",
    "toStatus": "APPROVED",
    "updateTime": "2026-02-16T16:30:00"
  }
}
```

## 11. 机构更新申请状态 - 审核拒绝

### 请求示例
```http
POST /api/org/adoption/applications/67890/status
Content-Type: application/json

{
  "toStatus": "REJECTED",
  "rejectReason": "居住环境不适合养猫，阳台无防护网存在安全隐患",
  "remark": "建议改善居住环境后再次申请"
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "applicationId": 67890,
    "fromStatus": "UNDER_REVIEW",
    "toStatus": "REJECTED",
    "updateTime": "2026-02-16T10:45:00"
  }
}
```

## 状态流转图

```
用户提交申请 → SUBMITTED(已提交)
                ↓
        机构开始审核 → UNDER_REVIEW(审核中)
                ↓
        安排面谈 → INTERVIEW(已约面谈)
                ↓
        家访考察 → HOME_VISIT(家访中)
                ↓
        最终审核 → APPROVED(已通过)
                
任意阶段可拒绝 → REJECTED(已拒绝)
用户可主动取消 → CANCELLED(已取消)
```

## 注意事项

1. **状态转换规则**：
   - SUBMITTED → UNDER_REVIEW/REJECTED/CANCELLED
   - UNDER_REVIEW → INTERVIEW/REJECTED/CANCELLED
   - INTERVIEW → HOME_VISIT/APPROVED/REJECTED/CANCELLED
   - HOME_VISIT → APPROVED/REJECTED/CANCELLED
   - APPROVED/REJECTED/CANCELLED 为终止状态，不可再转换

2. **权限控制**：
   - 用户只能操作自己的申请
   - 机构只能操作自己发布宠物的申请
   - 用户只能在SUBMITTED和UNDER_REVIEW状态取消申请

3. **特殊处理**：
   - 申请通过后，该宠物的其他进行中的申请会被自动拒绝
   - 所有状态变更都会记录操作日志