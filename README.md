# 招财商城 (Lucky Mall)

一个基于Spring Boot + React的电商系统原型

## 项目概述

本项目是一个简化版的电商系统，重点实现商品展示和搜索功能，其他模块提供基础展示功能。

## 技术栈

### 后端
- Java 11+
- Spring Boot 2.7+
- MyBatis
- MySQL 8.0
- Maven

### 前端
- React 18
- Ant Design
- Redux Toolkit
- React Router

## 项目结构

```
招财商城/
├── backend/          # Spring Boot后端项目
├── frontend/         # React前端项目
├── database/         # 数据库脚本
└── docs/            # 项目文档
```

## 核心功能

### ✅ 完整实现（核心功能）
- 商品列表展示和分页
- 商品详情页面
- 商品搜索功能
- 商品筛选和排序

### 📱 基础展示（界面功能）
- 用户注册/登录界面
- 购物车界面展示
- 订单管理界面展示

## 快速开始

### 1. 数据库准备
```sql
# 创建数据库
CREATE DATABASE lucky_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行数据库脚本
source database/init.sql
```

### 2. 后端启动
```bash
cd backend
mvn spring-boot:run
```

### 3. 前端启动
```bash
cd frontend
npm install
npm start
```

访问 http://localhost:3000 查看应用

## API文档

后端API运行在 http://localhost:8080

主要接口：
- `GET /api/products` - 获取商品列表
- `GET /api/products/{id}` - 获取商品详情
- `GET /api/products/search` - 搜索商品
- `GET /api/categories` - 获取商品分类

## 开发说明

本项目按照阶段一需求开发，重点实现商品展示和搜索功能，为后续AI功能集成奠定基础。 