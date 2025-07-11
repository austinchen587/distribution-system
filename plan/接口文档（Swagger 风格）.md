# 🧩 分销系统 API 接口文档（Swagger 风格）

```json
// ==================== 用户注册与认证 ====================

POST /api/auth/register
说明：用户注册（手机号 + 验证码）

请求参数：
{
  "phone": "18812345678",
  "code": "123456",
  "role": "sales" // 可选值: sales / agent
}

响应：
{
  "code": 0,
  "message": "注册成功",
  "data": {
    "token": "xxxxx.jwt.token"
  }
}


POST /api/auth/login
说明：用户登录（手机号 + 密码）

请求参数：
{
  "phone": "18812345678",
  "password": "abc123456"
}

响应：
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "xxxxx.jwt.token"
  }
}


// ==================== 客资模块 ====================

POST /api/leads/submit
说明：提交客户信息（手机号必须唯一）

请求参数：
{
  "phone": "13666668888",
  "wechat": "wxid_abcd123",
  "qq": "123456789"
}

响应：
{
  "code": 0,
  "message": "提交成功",
  "data": {
    "lead_id": 10001
  }
}


GET /api/leads/mine
说明：获取当前用户提交的所有客资列表

响应：
{
  "code": 0,
  "data": [
    {
      "id": 1,
      "phone": "13666668888",
      "wechat": "wxid_abcd123",
      "status": "contacted",
      "created_at": "2025-07-08 10:00:00"
    }
  ]
}


// ==================== 成交记录模块 ====================

POST /api/deals/create
说明：销售录入成交记录（绑定客资 + 商品）

请求参数：
{
  "customer_id": 10001,
  "product_id": 3,
  "amount": 1980.00
}

响应：
{
  "code": 0,
  "message": "成交记录创建成功"
}


GET /api/deals/mine
说明：查询当前销售的成交记录

响应：
{
  "code": 0,
  "data": [
    {
      "id": 101,
      "customer_id": 10001,
      "product_id": 3,
      "amount": 1980,
      "status": "completed",
      "created_at": "2025-07-08 12:00:00"
    }
  ]
}


// ==================== 佣金查询模块 ====================

GET /api/commissions/mine
说明：查询当前用户佣金记录（含直接与间接）

响应：
{
  "code": 0,
  "data": [
    {
      "id": 201,
      "deal_id": 101,
      "level": "direct",
      "amount": 198,
      "rate": 0.10
    }
  ]
}


// ==================== 推广任务模块 ====================

POST /api/promotions/upload
说明：代理提交推广任务

请求参数：
{
  "platform": "douyin",
  "type": "real_person",
  "url": "https://douyin.com/video/abc123"
}

响应：
{
  "code": 0,
  "message": "提交成功，等待审核"
}


GET /api/promotions/mine
说明：获取当前用户上传的推广任务记录

响应：
{
  "code": 0,
  "data": [
    {
      "id": 301,
      "platform": "douyin",
      "type": "real_person",
      "status": "approved",
      "reward_amount": 10.00
    }
  ]
}


所有接口需携带 JWT token 放入请求头：
Authorization: Bearer {token}

接口统一返回结构如下：

{
  "code": 0,
  "message": "说明",
  "data": {}
}
