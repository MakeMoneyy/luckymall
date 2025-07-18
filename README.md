# 智能客服系统

基于Spring Boot和通义千问API开发的智能客服系统，支持自动回复、上下文对话和缓存优化。

## 项目介绍

本项目是一个基于Spring Boot框架和阿里云通义千问API开发的智能客服系统，主要功能包括：

- 智能对话：通过通义千问API实现自然语言对话
- 上下文管理：支持多轮对话，保持对话连贯性
- 缓存优化：使用Redis缓存常见问题回答，提高响应速度
- 会话管理：支持创建、查询和管理用户会话

## 技术栈

- **后端框架**：Spring Boot 2.7.x
- **数据库**：MySQL 8.0
- **缓存**：Redis
- **AI接口**：阿里云通义千问API (DashScope SDK)
- **构建工具**：Maven

## 快速开始

### 前提条件

- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- 阿里云通义千问API Key

### 配置

1. 克隆项目到本地

```bash
git clone https://github.com/yourusername/smart-customer-service.git
cd smart-customer-service
```

2. 配置数据库

创建MySQL数据库：

```sql
CREATE DATABASE customer_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 配置应用

编辑 `src/main/resources/application.yml` 文件，修改数据库连接信息和API Key：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/customer_service?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
    username: your_username
    password: your_password

dashscope:
  api-key: your_api_key_here
```

或者通过环境变量设置API Key：

```bash
export DASHSCOPE_API_KEY=your_api_key_here
```

### 构建与运行

使用Maven构建项目：

```bash
mvn clean package
```

运行应用：

```bash
java -jar target/smart-customer-service-0.0.1-SNAPSHOT.jar
```

或者使用Maven直接运行：

```bash
mvn spring-boot:run
```

### 测试API连接

可以运行测试类来验证API连接是否正常：

```bash
mvn exec:java -Dexec.mainClass="com.example.smartcustomerservice.util.DashScopeApiExample"
```

## API接口

### 发送聊天消息

```
POST /api/customer-service/chat
```

请求体：

```json
{
  "userId": 12345,
  "sessionId": "session_abc123",
  "message": "你好，请问有什么可以帮助我的？"
}
```

响应：

```json
{
  "code": 200,
  "data": {
    "responseId": "resp_xyz789",
    "message": "您好！我是智能客服助手，很高兴为您服务。请问有什么可以帮助您的吗？",
    "cacheHit": false,
    "responseTimeMs": 1200
  }
}
```

### 创建新会话

```
POST /api/customer-service/session?userId=12345
```

响应：

```json
{
  "code": 200,
  "data": {
    "sessionId": "session_abc123"
  }
}
```

### 获取会话历史

```
GET /api/customer-service/session/{sessionId}
```

响应：

```json
{
  "code": 200,
  "data": {
    "responseId": "hist_xyz789",
    "message": "user: 你好\n\nassistant: 您好！请问有什么可以帮助您的？\n\n"
  }
}
```

## 注意事项

- 请确保API Key的安全性，不要将其硬编码在代码中或提交到版本控制系统
- Redis缓存默认过期时间为1小时，可在配置文件中调整
- 系统默认保留最近10轮对话作为上下文，可根据需要调整

## 许可证

MIT License 