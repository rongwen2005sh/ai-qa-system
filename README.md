# AI赋能智能问答系统

## 项目简介

本项目是一个基于 Spring Boot、Spring Cloud 及 Nacos 的微服务架构 AI 问答系统。
包含用户服务、问答服务以及统一的 API 网关，支持注册、登录、身份认证、JWT令牌、服务发现与路由，以及自动化 API 文档（Swagger/OpenAPI）。

---

## 目录结构

```
ai-qa-system/
│
├── backend-services/
│   ├── api-gateway/        # API 网关，统一入口和路由
│   ├── qa-service/         # 问答服务（AI问答相关）
│   ├── user-service/       # 用户服务（注册、登录、认证）
│   └── pom.xml             # 后端多模块父POM
├── .gitignore
```

---

## 微服务模块介绍

### 1. API 网关（api-gateway）

- 使用 Spring Cloud Gateway 进行统一路由和服务发现。
- 接入 Nacos 作为注册中心，实现负载均衡与动态路由。
- 主要路由配置见 `application.yml`：
    - `/api/user/**` → 路由至 user-service
    - `/api/qa/**`   → 路由至 qa-service

### 2. 用户服务（user-service）

- 提供用户注册、登录、密码修改、JWT鉴权等功能。
- 使用 Spring Security、JPA（MySQL）、自动化 API 文档（Swagger）。
- 主要接口：
    - `POST /api/users/login`         — 用户登录（获取JWT令牌）
    - `POST /api/users/register`      — 用户注册
    - `POST /api/users/change-password` — 修改密码

#### 重要文件：
- `UserController.java` / `UserControllerImpl.java` — 用户相关REST接口
- `UserService.java` / `UserServiceImpl.java`      — 业务逻辑
- `JwtUtil.java`, `JwtAuthenticationFilter.java`   — JWT令牌生成与校验
- `SecurityConfig.java`                            — 安全配置与拦截规则
- `GlobalExceptionHandler.java`                    — 全局统一异常处理
- `User.java`, `UserRepository.java`               — 用户实体与数据库访问
- `application.yml`                                — 服务配置（Nacos, DB, JWT, Swagger）
- `sql/init.sql`                                   — MySQL初始化脚本

### 3. 问答服务（qa-service）

- 用于实现 AI 问答相关逻辑（可扩展）。
- 已注册到 Nacos，网关中配置了路由。

---

## 技术栈

- **Java 17**
- **Spring Boot 2.7.x**
- **Spring Cloud (2021.x)**
- **Spring Cloud Alibaba Nacos (2021.x)**
- **Spring Security, JWT (jjwt)**
- **Spring Data JPA (MySQL)**
- **Swagger/OpenAPI (springdoc)**
- **Maven 多模块管理**

---

## 快速启动

### 依赖准备

- JDK 17+
- Maven 3.8+
- MySQL 8+
- [Nacos](https://nacos.io/) 注册中心，端口配置见各 `application.yml`

### 数据库初始化

使用脚本初始化数据库及表：

```bash
mysql -u root -p < backend-services/user-service/src/main/resources/sql/init.sql
```

### 编译与启动

编译所有微服务：

```bash
cd backend-services
mvn clean package
```

分别启动各服务（可用不同终端）：

```bash
cd user-service        && mvn spring-boot:run
cd qa-service          && mvn spring-boot:run
cd api-gateway         && mvn spring-boot:run
```

### 服务发现

确保 Nacos 注册中心已启动，地址与端口请参考各 `application.yml` 配置。

---

## API文档

- **用户服务 Swagger 文档：**  
  [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

---

## 认证与安全

- 登录成功后返回 JWT 令牌。
- 除登录和注册接口外，其余接口需在请求头中携带 `Authorization: Bearer <token>`。
- `/api/users/login` 和 `/api/users/register` 为公开接口。

---

## 开发建议

- JWT 密钥与有效期配置在 `application.yml`，生产环境建议通过环境变量注入，勿硬编码。
- 所有异常通过 `GlobalExceptionHandler` 统一响应。
- 用户密码采用 BCrypt 加密存储。

---

## 扩展方向

- 可在 `qa-service` 中实现具体 AI 问答逻辑。
- 支持更多微服务扩展（如：用户画像、问答历史、统计分析等）。
- 可集成真实的 AI/LLM 问答后端。

---

## 许可协议

本项目暂未设置开源协议，如需商用请联系作者。

---

如需进一步帮助或有建议，欢迎提交Issue！
