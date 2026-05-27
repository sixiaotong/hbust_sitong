# SmartNote（湖科风光）

一款基于 Vue 3 + Spring Boot 的校园笔记分享应用，支持笔记的发表、浏览、评论、收藏、点赞以及用户关注，面向湖北科技学院在校师生。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 · Vite 5 · Vue Router 4 · Axios · Tailwind CSS |
| 后端 | Java 8 · Spring Boot 2.7 · Spring Security · MyBatis-Plus |
| 数据库 | MySQL 8 |
| 认证 | JWT (JSON Web Token) |
| 存储 | 阿里云 OSS（密钥已移除，需自行配置） |

## 项目结构

```
├── smart-note-frontend/   # Vue 3 前端
│   └── src/
│       ├── api/           # 接口封装 (axios)
│       ├── components/    # 公共组件
│       ├── router/        # 路由配置与登录守卫
│       └── views/         # 页面组件
├── smart-note-backend/    # Spring Boot 后端
│   └── src/main/java/com/smartnote/
│       ├── common/        # 全局异常处理、JWT 工具类、统一响应体
│       ├── config/        # 安全配置、跨域、MyBatis-Plus、OSS 等
│       ├── controller/    # 控制器
│       ├── dto/           # 数据传输对象
│       ├── entity/        # 实体类
│       ├── mapper/        # MyBatis-Plus Mapper
│       └── service/       # 业务逻辑
├── niginx_vscode_hukefenggaung/  # Nginx 静态资源部署
└── docs/                  # 需求分析与成果展示文档
```

## 功能模块

- **用户系统**：注册、登录、JWT 鉴权、个人主页
- **笔记管理**：发表图文笔记、编辑、删除、笔记详情
- **发现页**：浏览所有公开笔记
- **社交互动**：评论、收藏、点赞、关注用户
- **消息通知**：评论/点赞/关注等站内通知
- **文件上传**：支持图片上传至阿里云 OSS

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

### 1. 数据库

创建数据库并导入初始表结构：

```sql
CREATE DATABASE smart_note DEFAULT CHARACTER SET utf8mb4;
```

Spring Boot 启动时 MyBatis-Plus 会自动建表。如需手动初始化，可配置 `spring.sql.init.mode=always`（已默认开启）。

### 2. 后端启动

```bash
cd smart-note-backend

# 修改 application.yml 中的数据库连接、JWT secret、OSS 等配置
# src/main/resources/application.yml

mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`。

### 3. 前端启动

```bash
cd smart-note-frontend

npm install
npm run dev
```

前端默认运行在 `http://localhost:3000`，开发模式下通过 Vite proxy 将 `/api` 请求代理到后端。

### 4. 生产部署

```bash
# 构建前端
cd smart-note-frontend
npm run build

# 将 dist/ 目录部署到 Nginx 或其他静态服务器
# 参考 niginx_vscode_hukefenggaung/ 中的 Nginx 配置
```

## 配置说明

在 `application.yml` 中修改以下配置：

```yaml
# 数据库
spring.datasource.url: jdbc:mysql://<你的数据库地址>:3306/smart_note
spring.datasource.username: <用户名>
spring.datasource.password: <密码>

# JWT
jwt.secret: <自定义密钥>
jwt.expiration: 86400000

# 阿里云 OSS（如不使用文件上传功能可移除）
aliyun.oss.endpoint: <OSS Endpoint>
aliyun.oss.bucket-name: <Bucket 名称>
aliyun.oss.access-key-id: <AccessKey ID>
aliyun.oss.access-key-secret: <AccessKey Secret>
```

## 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/login` | 登录页 | 公开页面 |
| `/` | 首页 | 笔记流 |
| `/detail/:id` | 笔记详情 | 含评论区 |
| `/profile` | 个人主页 | 我的笔记、收藏 |
| `/create` | 创建笔记 | 富文本编辑 |
| `/discover` | 发现页 | 浏览所有公开笔记 |
| `/messages` | 消息通知 | 评论/点赞/关注通知 |
| `/user/:id` | 用户主页 | 查看他人主页 |
