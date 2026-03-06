# AI聊天功能前后端对接文档

> 本文档基于宠物在线领养系统,详细说明AI聊天、会话管理及问题记录的完整前后端对接方案

## 📋 文档概述

本文档定义了AI聊天系统的核心功能,包括智能问答、会话管理、历史记录查询等功能的前后端对接规范。

## 🎯 功能特性

- ✅ 基于DeepSeek的AI智能问答(流式响应)
- ✅ 本地问题模板优先匹配
- ✅ 会话管理(创建、删除、查询)
- ✅ 按天分组的会话列表
- ✅ 详细的问答记录追溯
- ✅ 完整的RESTful API设计

## 🏗️ 系统架构

### 数据库表结构

#### AI-用户提问记录表 (ai_question_record)
| 字段 | 类型 | 描述 |
|------|------|------|
| id | BIGINT | 主键ID |
| user_id | BIGINT | 用户ID |
| type | TINYINT | 类型,1问题 2回复 |
| session_id | BIGINT | 用户会话id |
| question_id | BIGINT | 用户问题id(系统) |
| response_id | VARCHAR(64) | deepseek回复id(外部) |
| req_text | TEXT | 请求原文 |
| resp_text | TEXT | 回复文本-原文 |
| tokens_used | INT | tokens使用量 |
| status | TINYINT | 状态,0失败 1成功 2中断 |
| req_param | MEDIUMTEXT | 请求体 |
| resp_result | MEDIUMTEXT | 响应体 |
| if_delete | TINYINT(1) | 是否删除(0否 1是) |
| create_time | DATETIME | 创建时间 |
| create_by | BIGINT | 创建人 |
| modify_time | DATETIME | 修改时间 |
| modify_by | BIGINT | 修改人 |

#### AI-系统问题模板表 (ai_question_template)
| 字段 | 类型 | 描述 |
|------|------|------|
| id | BIGINT | 主键ID |
| question | TEXT | 问题内容 |
| answer | TEXT | 答案内容 |
| sort_no | INT | 排序号 |
| type | TINYINT | 类型,1-问题模板,2-免责声明 |
| if_delete | TINYINT(1) | 是否删除(0否 1是) |
| create_time | DATETIME | 创建时间 |
| create_by | BIGINT | 创建人 |
| modify_time | DATETIME | 修改时间 |
| modify_by | BIGINT | 修改人 |

#### AI-用户会话表 (ai_session)
| 字段 | 类型 | 描述 |
|------|------|------|
| session_id | BIGINT | 主键ID |
| title | VARCHAR(255) | 会话标题 |
| if_delete | TINYINT(1) | 是否删除(0否 1是) |
| create_time | DATETIME | 创建时间 |
| create_by | BIGINT | 创建人 |
| modify_time | DATETIME | 修改时间 |
| modify_by | BIGINT | 修改人 |

## 🔄 业务流程图

```
用户提问 → 创建会话 → 查询本地模板库
                          ↓
                  ┌───命中模板───→ 直接返回模板答案
                  │
                  └─未命中模板───→ 调用DeepSeek AI接口
                                        ↓
                                  流式返回AI答案
                                        ↓
                                  保存问答记录
```

## 🚀 API接口文档

### 1. DeepSeek AI聊天接口

#### 1.1 AI聊天问答-流式响应
**接口地址**: `GET /ai/deepseek/chatCompletions`

**功能描述**: 与AI进行智能对话,支持流式响应,优先匹配本地问题模板

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| content | String | 是 | 用户提问内容 |

**请求示例**:
```
GET /ai/deepseek/chatCompletions?content=你好,请介绍一下宠物领养流程
```

**响应格式**: `text/event-stream;charset=utf-8` (Server-Sent Events)

**响应示例**:
```
event: message
data: 您好!宠物领养流程一般包括以下几个步骤:

event: message
data: 1. 选择心仪的宠物

event: message
data: 2. 提交领养申请

event: message
data: 3. 等待审核

event: message
data: 4. 审核通过后签订领养协议

event: message
data: 5. 完成领养
```

**业务逻辑说明**:
1. 接收用户提问后,自动创建会话记录(标题取前10个字符)
2. 优先查询本地问题模板库,如有匹配则直接返回模板答案
3. 未命中模板则调用DeepSeek AI接口,返回流式响应
4. 所有问答记录都会保存到数据库

#### 1.2 获取账户余额
**接口地址**: `GET /ai/deepseek/account/balance`

**功能描述**: 获取DeepSeek账户余额信息

**请求参数**: 无

**响应示例**:
```json
{
  "code": 10000,
  "message": "success",
  "data": {
    "available": true,
    "totalBalance": 100.50,
    "grantedBalance": 50.00,
    "toppedUpBalance": 50.50
  }
}
```

**响应字段说明**:
| 字段 | 类型 | 描述 |
|------|------|------|
| available | Boolean | 账户是否可用 |
| totalBalance | BigDecimal | 总余额 |
| grantedBalance | BigDecimal | 赠送余额 |
| toppedUpBalance | BigDecimal | 充值余额 |

---

### 2. 会话管理接口

#### 2.1 按天分组获取会话列表
**接口地址**: `GET /ai/session/listGroupByDay`

**功能描述**: 获取最近N天的会话列表,按天分组展示

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| days | Integer | 否 | 7 | 查询最近天数(最大30天) |

**请求示例**:
```
GET /ai/session/listGroupByDay?days=7
```

**响应示例**:
```json
{
  "code": 10000,
  "message": "success",
  "data": [
    {
      "day": "2025-12-11",
      "sessions": [
        {
          "sessionId": 1001,
          "title": "抓到出轨证据就稳赢?",
          "ifDelete": false,
          "createTime": "2025-12-11 10:30:00",
          "createBy": 123,
          "modifyTime": "2025-12-11 10:30:00",
          "modifyBy": 123
        }
      ]
    },
    {
      "day": "2025-12-10",
      "sessions": [
        {
          "sessionId": 1002,
          "title": "他是谁",
          "ifDelete": false,
          "createTime": "2025-12-10 14:20:00",
          "createBy": 123,
          "modifyTime": "2025-12-10 14:20:00",
          "modifyBy": 123
        }
      ]
    }
  ]
}
```

**响应字段说明**:
| 字段 | 类型 | 描述 |
|------|------|------|
| day | String | 日期(yyyy-MM-dd) |
| sessions | Array | 当天会话列表 |
| sessions[].sessionId | Long | 会话ID |
| sessions[].title | String | 会话标题 |
| sessions[].ifDelete | Boolean | 是否已删除 |
| sessions[].createTime | String | 创建时间 |
| sessions[].createBy | Long | 创建人ID |
| sessions[].modifyTime | String | 修改时间 |
| sessions[].modifyBy | Long | 修改人ID |

#### 2.2 删除会话
**接口地址**: `POST /ai/session/delete`

**功能描述**: 删除指定会话(逻辑删除)

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| sessionId | Long | 是 | 会话ID |

**请求示例**:
```
POST /ai/session/delete?sessionId=1001
```

**响应示例**:
```json
{
  "code": 10000,
  "message": "success",
  "data": null
}
```

#### 2.3 删除某天所有会话
**接口地址**: `POST /ai/session/delete/day`

**功能描述**: 删除指定日期的所有会话(逻辑删除)

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| date | Date | 是 | 日期 |

**请求示例**:
```
POST /ai/session/delete/day?date=2025-12-11
```

**响应示例**:
```json
{
  "code": 10000,
  "message": "success",
  "data": null
}
```

#### 2.4 获取会话详情
**接口地址**: `POST /ai/session/detail`

**功能描述**: 获取指定会话的详细问答记录

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| sessionId | Long | 是 | 会话ID |

**请求示例**:
```
POST /ai/session/detail?sessionId=1001
```

**响应示例**:
```json
{
  "code": 10000,
  "message": "success",
  "data": [
    {
      "id": 2001,
      "userId": 123,
      "type": 1,
      "sessionId": 1001,
      "questionId": null,
      "reqText": "宠物领养需要什么条件?",
      "respText": null,
      "createBy": 123,
      "createTime": "2025-12-11 10:30:00"
    },
    {
      "id": 2002,
      "userId": 123,
      "type": 2,
      "sessionId": 1001,
      "questionId": 2001,
      "reqText": null,
      "respText": "您好!宠物领养一般需要满足以下条件:\n1. 年满18周岁\n2. 有稳定的住所\n3. 有经济能力\n4. 家庭成员同意",
      "createBy": 123,
      "createTime": "2025-12-11 10:30:05"
    }
  ]
}
```

**响应字段说明**:
| 字段 | 类型 | 描述 |
|------|------|------|
| id | Long | 记录ID |
| userId | Long | 用户ID |
| type | Integer | 类型(1-问题,2-回复) |
| sessionId | Long | 会话ID |
| questionId | Long | 问题ID(回复记录中关联的问题ID) |
| reqText | String | 请求文本(问题内容) |
| respText | String | 回复文本(AI回答) |
| createBy | Long | 创建人ID |
| createTime | String | 创建时间 |

#### 2.5 查询当天所有会话内容详情
**接口地址**: `GET /ai/session/chat/history/day`

**功能描述**: 查询指定日期的所有会话问答记录

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| day | String | 是 | 日期(格式: yyyy-MM-dd) |

**请求示例**:
```
GET /ai/session/chat/history/day?day=2025-12-11
```

**响应示例**:
```json
{
  "code": 10000,
  "message": "success",
  "data": [
    {
      "id": 2001,
      "userId": 123,
      "type": 1,
      "sessionId": 1001,
      "questionId": null,
      "reqText": "宠物领养需要什么条件?",
      "respText": null,
      "createBy": 123,
      "createTime": "2025-12-11 10:30:00"
    },
    {
      "id": 2002,
      "userId": 123,
      "type": 2,
      "sessionId": 1001,
      "questionId": 2001,
      "reqText": null,
      "respText": "您好!宠物领养一般需要满足以下条件...",
      "createBy": 123,
      "createTime": "2025-12-11 10:30:05"
    }
  ]
}
```

---

## 🎨 前端实现示例

### 4.1 AI聊天组件示例

```vue
<template>
  <div class="ai-chat-container">
    <!-- 消息列表 -->
    <div class="message-list" ref="messageList">
      <div
        v-for="msg in messages"
        :key="msg.id"
        :class="['message', msg.type === 1 ? 'user' : 'ai']"
      >
        <div class="message-content">{{ msg.text }}</div>
      </div>
    </div>

    <!-- 输入框 -->
    <div class="input-area">
      <el-input
        v-model="inputContent"
        placeholder="请输入您的问题..."
        @keyup.enter="sendMessage"
      />
      <el-button type="primary" @click="sendMessage">发送</el-button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      inputContent: '',
      messages: [],
      currentSessionId: null
    };
  },
  methods: {
    async sendMessage() {
      if (!this.inputContent.trim()) return;

      const userMessage = this.inputContent;

      // 添加用户消息到列表
      this.messages.push({
        id: Date.now(),
        type: 1,
        text: userMessage
      });

      this.inputContent = '';

      // 调用AI接口
      await this.callAI(userMessage);
    },

    async callAI(content) {
      try {
        const response = await fetch(
          `/ai/deepseek/chatCompletions?content=${encodeURIComponent(content)}`,
          {
            method: 'GET',
            headers: {
              'Accept': 'text/event-stream'
            }
          }
        );

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let aiMessage = {
          id: Date.now() + 1,
          type: 2,
          text: ''
        };

        // 添加AI消息占位
        this.messages.push(aiMessage);

        // 读取流式响应
        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          const chunk = decoder.decode(value);
          const lines = chunk.split('\n');

          for (const line of lines) {
            if (line.startsWith('data:')) {
              const data = line.substring(5).trim();
              if (data) {
                aiMessage.text += data;
                // 触发视图更新
                this.$forceUpdate();
                // 滚动到底部
                this.scrollToBottom();
              }
            }
          }
        }
      } catch (error) {
        console.error('AI调用失败:', error);
        this.$message.error('AI调用失败,请稍后重试');
      }
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messageList;
        container.scrollTop = container.scrollHeight;
      });
    }
  }
};
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 600px;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message {
  margin-bottom: 16px;
}

.message.user {
  text-align: right;
}

.message.user .message-content {
  background: #409eff;
  color: white;
}

.message.ai .message-content {
  background: #f5f5f5;
  color: #333;
}

.message-content {
  display: inline-block;
  padding: 12px 16px;
  border-radius: 8px;
  max-width: 70%;
  word-wrap: break-word;
}

.input-area {
  display: flex;
  padding: 20px;
  border-top: 1px solid #eee;
}

.input-area .el-input {
  flex: 1;
  margin-right: 10px;
}
</style>
```

### 4.2 会话列表组件示例

```vue
<template>
  <div class="session-list-container">
    <el-button @click="loadSessions" type="primary" size="small" style="margin-bottom: 16px;">
      刷新会话
    </el-button>

    <!-- 按天分组的会话列表 -->
    <div v-for="dayGroup in sessionList" :key="dayGroup.day" class="day-group">
      <div class="day-header">
        <span>{{ dayGroup.day }}</span>
        <el-button
          type="text"
          size="small"
          @click="deleteDaySessions(dayGroup.day)"
        >
          删除当天会话
        </el-button>
      </div>

      <div
        v-for="session in dayGroup.sessions"
        :key="session.sessionId"
        class="session-item"
        @click="selectSession(session)"
      >
        <div class="session-title">{{ session.title }}</div>
        <div class="session-time">{{ session.createTime }}</div>
        <el-button
          type="text"
          size="small"
          @click.stop="deleteSession(session.sessionId)"
        >
          删除
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api';

export default {
  data() {
    return {
      sessionList: []
    };
  },
  mounted() {
    this.loadSessions();
  },
  methods: {
    async loadSessions() {
      try {
        const response = await api.get('/ai/session/listGroupByDay', {
          params: { days: 7 }
        });
        this.sessionList = response.data.data;
      } catch (error) {
        console.error('加载会话列表失败:', error);
        this.$message.error('加载会话列表失败');
      }
    },

    async deleteSession(sessionId) {
      try {
        await this.$confirm('确定要删除这个会话吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        await api.post('/ai/session/delete', null, {
          params: { sessionId }
        });

        this.$message.success('删除成功');
        this.loadSessions();
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除会话失败:', error);
          this.$message.error('删除失败');
        }
      }
    },

    async deleteDaySessions(day) {
      try {
        await this.$confirm(`确定要删除${day}的所有会话吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        await api.post('/ai/session/delete/day', null, {
          params: { date: day }
        });

        this.$message.success('删除成功');
        this.loadSessions();
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除会话失败:', error);
          this.$message.error('删除失败');
        }
      }
    },

    async selectSession(session) {
      try {
        const response = await api.post('/ai/session/detail', null, {
          params: { sessionId: session.sessionId }
        });

        // 触发事件,传递会话详情
        this.$emit('session-selected', {
          session: session,
          messages: response.data.data
        });
      } catch (error) {
        console.error('获取会话详情失败:', error);
        this.$message.error('获取会话详情失败');
      }
    }
  }
};
</script>

<style scoped>
.session-list-container {
  padding: 20px;
}

.day-group {
  margin-bottom: 24px;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 12px;
  font-weight: bold;
  color: #666;
}

.session-item {
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.session-item:hover {
  background: #f5f5f5;
}

.session-title {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.session-time {
  font-size: 12px;
  color: #999;
  margin-right: 12px;
}
</style>
```

### 4.3 API调用封装示例

```javascript
// src/api/ai.js
import request from '@/utils/request';

export default {
  /**
   * AI聊天问答(流式响应)
   * @param {string} content - 用户提问内容
   * @returns {Promise<Response>} - 返回流式响应
   */
  chatCompletions(content) {
    return fetch(
      `${process.env.VUE_APP_BASE_URL}/ai/deepseek/chatCompletions?content=${encodeURIComponent(content)}`,
      {
        method: 'GET',
        headers: {
          'Accept': 'text/event-stream',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );
  },

  /**
   * 获取账户余额
   */
  getBalance() {
    return request({
      url: '/ai/deepseek/account/balance',
      method: 'get'
    });
  },

  /**
   * 按天分组获取会话列表
   * @param {number} days - 查询最近天数(默认7天,最大30天)
   */
  getSessionListGroupByDay(days = 7) {
    return request({
      url: '/ai/session/listGroupByDay',
      method: 'get',
      params: { days }
    });
  },

  /**
   * 删除会话
   * @param {number} sessionId - 会话ID
   */
  deleteSession(sessionId) {
    return request({
      url: '/ai/session/delete',
      method: 'post',
      params: { sessionId }
    });
  },

  /**
   * 删除某天所有会话
   * @param {string} date - 日期(yyyy-MM-dd)
   */
  deleteDaySessions(date) {
    return request({
      url: '/ai/session/delete/day',
      method: 'post',
      params: { date }
    });
  },

  /**
   * 获取会话详情
   * @param {number} sessionId - 会话ID
   */
  getSessionDetail(sessionId) {
    return request({
      url: '/ai/session/detail',
      method: 'post',
      params: { sessionId }
    });
  },

  /**
   * 查询当天所有会话内容详情
   * @param {string} day - 日期(yyyy-MM-dd)
   */
  getDayChatHistory(day) {
    return request({
      url: '/ai/session/chat/history/day',
      method: 'get',
      params: { day }
    });
  }
};
```

---

## 📊 状态说明

### 问答记录类型(type)

| 值 | 描述 |
|----|------|
| 1 | 问题 |
| 2 | 回复 |

### 问答记录状态(status)

| 值 | 描述 |
|----|------|
| 0 | 失败 |
| 1 | 成功 |
| 2 | 中断 |

### 问题模板类型(type)

| 值 | 描述 |
|----|------|
| 1 | 问题模板 |
| 2 | 免责声明 |

---

## 📈 错误处理

### 错误码定义

| 错误码 | 描述 | 处理方式 |
|--------|------|----------|
| 10000 | 成功 | 正常处理 |
| 40001 | 参数错误 | 检查请求参数 |
| 40002 | 会话不存在 | 提示用户会话已删除 |
| 40003 | AI服务异常 | 提示稍后重试 |
| 40004 | 余额不足 | 提示联系管理员充值 |
| 50000 | 系统异常 | 提示稍后重试 |

### 前端错误处理示例

```javascript
// 统一错误处理拦截器
request.interceptors.response.use(
  response => {
    const res = response.data;

    if (res.code !== 10000) {
      switch (res.code) {
        case 40003:
          Message.error('AI服务暂时不可用,请稍后重试');
          break;
        case 40004:
          Message.error('账户余额不足,请联系管理员');
          break;
        default:
          Message.error(res.message || '请求失败');
      }

      return Promise.reject(new Error(res.message || '请求失败'));
    }

    return res;
  },
  error => {
    Message.error('网络异常,请稍后重试');
    return Promise.reject(error);
  }
);
```

---

## 🚀 部署注意事项

### 1. DeepSeek配置

确保application.yml中配置了正确的DeepSeek API密钥:

```yaml
deepseek:
  api:
    key: your-api-key-here
    base-url: https://api.deepseek.com
```

### 2. 跨域配置

确保后端配置了正确的CORS跨域设置:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/ai/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### 3. 流式响应支持

前端需要使用原生fetch API或EventSource来处理流式响应,不能使用axios等普通HTTP库。

---

## 📋 测试用例

### 7.1 AI聊天测试

```javascript
// 测试AI聊天功能
const testAIChat = async () => {
  const content = '你好,请介绍一下宠物领养流程';

  try {
    const response = await fetch(
      `/ai/deepseek/chatCompletions?content=${encodeURIComponent(content)}`,
      {
        method: 'GET',
        headers: {
          'Accept': 'text/event-stream',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let result = '';

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      const chunk = decoder.decode(value);
      result += chunk;
    }

    console.log('AI回复:', result);
    console.assert(result.length > 0, 'AI回复不能为空');
  } catch (error) {
    console.error('测试失败:', error);
  }
};
```

### 7.2 会话管理测试

```javascript
// 测试会话列表查询
const testSessionList = async () => {
  try {
    const response = await api.get('/ai/session/listGroupByDay', {
      params: { days: 7 }
    });

    console.assert(response.data.code === 10000, '请求失败');
    console.assert(Array.isArray(response.data.data), '返回格式错误');

    console.log('会话列表:', response.data.data);
  } catch (error) {
    console.error('测试失败:', error);
  }
};

// 测试会话详情查询
const testSessionDetail = async (sessionId) => {
  try {
    const response = await api.post('/ai/session/detail', null, {
      params: { sessionId }
    });

    console.assert(response.data.code === 10000, '请求失败');
    console.assert(Array.isArray(response.data.data), '返回格式错误');

    console.log('会话详情:', response.data.data);
  } catch (error) {
    console.error('测试失败:', error);
  }
};
```

---

## 📋 总结

本对接文档提供了完整的AI聊天系统前后端对接方案,包括:

1. **AI聊天功能**: 支持流式响应,优先匹配本地模板
2. **会话管理**: 创建、删除、查询会话
3. **历史记录**: 按天分组查询,详情追溯
4. **前端实现**: 完整的Vue组件示例和API封装
5. **错误处理**: 统一的错误码和处理机制
6. **测试用例**: 完整的功能测试示例

所有接口都遵循RESTful规范,支持前后端分离开发,可以直接用于生产环境。前端开发人员可以根据本文档快速完成功能对接和开发。