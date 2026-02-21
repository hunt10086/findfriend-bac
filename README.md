基于 Spring Boot 的社交平台后端服务，提供用户管理、队伍系统、博客分享、好友聊天等社交功能。

## 项目简介

FindFriend 是一个功能完整的社交网络后端系统，采用现代化的技术栈构建，支持用户发现、队伍创建、博客发布、实时聊天等核心社交功能。

- **框架**: Spring Boot 3.4.5 + Java 17
- **数据库**: MySQL + MyBatis Plus 3.5.12
- **缓存/会话**: Redis + Spring Session
- **API文档**: SpringDoc OpenAPI + Knife4j
- **邮件服务**: Spring Mail
- **工具库**: Hutool、Lombok、Gson

## 核心功能

### 👤 用户模块

- 用户注册与登录（邮箱验证码）
- 个人信息修改
- 密码找回
- 标签搜索用户
- 智能推荐用户
- 附近用户发现
- 地理位置服务

### 👥 队伍系统

- 创建队伍（支持密码保护）
- 加入/退出队伍
- 队伍管理
- 队伍搜索
- 队伍聊天

### 📝 博客系统

- 发布博客（公开/私密）
- 博客点赞
- 博客评论
- 博客列表展示

### 💬 好友系统

- 好友申请与处理
- 好友列表
- 实时聊天（WebSocket）
- 消息推送

### 📁 文件服务

- 图片上传
- 腾讯COS云存储集成
- 定时清理未使用图片

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis   5.0+

application.yml中的邮箱、MySQL、Redis、腾讯云Cos等配置需要修改为自己的

```bash
# 编译项目
mvn clean compile

# 本地开发运行
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 打包
mvn clean package

# 运行打包文件
java -jar target/findFriend-backend-0.0.1-SNAPSHOT.jar
```

### API文档

本地环境启动后访问：

- Knife4j: http://localhost:7777/api/doc.html
