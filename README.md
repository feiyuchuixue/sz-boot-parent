<p align="center">
  <img src="https://szadmin.cn/brand/sz-admin-banner.png" alt="Sz-Admin" width="560">
</p>

<h1 align="center">Sz-Admin</h1>
<h4 align="center">基于 Spring Boot 4.x、JDK 21、Vue 3、Vite 7 的轻量级中后台脚手架</h4>

<p align="center">
  <a href="https://github.com/feiyuchuixue/sz-boot-parent/stargazers"><img src="https://img.shields.io/github/stars/feiyuchuixue/sz-boot-parent?style=flat-square&logo=GitHub" alt="GitHub Stars"></a>
  <a href="https://github.com/feiyuchuixue/sz-boot-parent/network/members"><img src="https://img.shields.io/github/forks/feiyuchuixue/sz-boot-parent?style=flat-square&logo=GitHub" alt="GitHub Forks"></a>
  <a href="https://gitee.com/feiyuchuixue/sz-boot-parent/stargazers"><img src="https://gitee.com/feiyuchuixue/sz-boot-parent/badge/star.svg?theme=dark" alt="Gitee Stars"></a>
  <a href="https://gitee.com/feiyuchuixue/sz-boot-parent/members"><img src="https://gitee.com/feiyuchuixue/sz-boot-parent/badge/fork.svg?theme=dark" alt="Gitee Forks"></a>
  <a href="https://github.com/feiyuchuixue/sz-boot-parent/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-Apache_2.0-blue.svg" alt="License"></a>
  <a href="https://deepwiki.com/feiyuchuixue/sz-boot-parent"><img src="https://deepwiki.com/badge.svg" alt="Ask DeepWiki"></a>
</p>

<p align="center">
  <a href="https://szadmin.cn">官网</a> |
  <a href="https://preview.szadmin.cn">在线预览</a> |
  <a href="https://szadmin.cn/md/Help/doc/info/start.html">快速开始</a> |
  <a href="https://szadmin.cn/md/Help/doc/other/change-log.html">更新日志</a> |
  <a href="https://szadmin.cn/md/Help/doc/other/upgrade.html#v2-0-0">v2.0.0 升级指南</a>
</p>

## 项目介绍

Sz-Admin 是一套面向企业中后台、管理端系统和二次开发项目的开源脚手架。它由后端 `sz-boot-parent`、前端 `sz-admin` 和官网文档共同组成，目标不是堆功能，而是提供一套边界清晰、便于升级、适合扩展的基础工程。

v2.0.0 是一次面向兼容性和扩展性的结构性重构：后端形成 `sz-common`、`sz-module`、`sz-service` 三层边界，前端引入 `src/core`、`src/editions`、`src/modules` 等模块化入口，让数据库、业务模块、登录适配、页面组合和派生项目接入都具备更清晰的扩展空间。对于二次开发项目来说，推荐把自有业务放进独立模块，从而减少与官方核心代码的耦合，也更利于后续升级。

Sz-Admin 也可以理解为“升职 Admin”，寓意升职加薪、节节高。它希望把常见后台能力整理成一套轻量、直观、可维护的工程底座，让开发者把更多精力放回业务本身。

## 为什么选择 Sz-Admin

| 亮点 | 说明 |
| --- | --- |
| 现代技术栈 | Spring Boot 4.x、JDK 21、Vue 3.5、Vite 7.3、TypeScript 5.2 |
| 贴近真实开发 | 来自实际中后台开发场景的持续实践，注重细节和开发体验，尽量降低使用者的心智负担 |
| 模块边界清晰 | 后端拆分 `sz-common`、`sz-module`、`sz-service`，官方能力和二开业务更容易隔离 |
| 升级友好 | 推荐自有业务独立成 `sz-module-*`，减少直接改官方核心模块带来的升级冲突 |
| 权限能力完整 | 用户、角色、菜单、按钮权限、动态路由、系统角色数据权限形成完整闭环 |
| 数据库适配明确 | MySQL / PostgreSQL 二选一启用，数据权限方言、生成器和 Liquibase 脚本同步适配 |
| 开发效率更高 | 内置代码生成器、脚本预览、Excel、OSS、字典、WebSocket、接口防抖等常用能力 |
| 易维护、易协作 | 结构清晰、职责稳定，适合长期二开维护，也为后续结合 AI 辅助编码、vibe coding 等方式提供更容易理解和接手的工程基础 |

## 在线体验

| 入口 | 地址 |
| --- | --- |
| 官网 | [https://szadmin.cn](https://szadmin.cn) |
| 在线预览 | [https://preview.szadmin.cn](https://preview.szadmin.cn) |
| 快速开始 | [https://szadmin.cn/md/Help/doc/info/start.html](https://szadmin.cn/md/Help/doc/info/start.html) |
| 更新日志 | [https://szadmin.cn/md/Help/doc/other/change-log.html](https://szadmin.cn/md/Help/doc/other/change-log.html) |
| v2.0.0 升级指南 | [https://szadmin.cn/md/Help/doc/other/upgrade.html#v2-0-0](https://szadmin.cn/md/Help/doc/other/upgrade.html#v2-0-0) |

## 代码仓库

| 仓库 | GitHub | Gitee |
| --- | --- | --- |
| 后端 `sz-boot-parent` | [feiyuchuixue/sz-boot-parent](https://github.com/feiyuchuixue/sz-boot-parent) | [feiyuchuixue/sz-boot-parent](https://gitee.com/feiyuchuixue/sz-boot-parent) |
| 前端 `sz-admin` | [feiyuchuixue/sz-admin](https://github.com/feiyuchuixue/sz-admin) | [feiyuchuixue/sz-admin](https://gitee.com/feiyuchuixue/sz-admin) |
| 部署脚本 `sz-deploy` | [feiyuchuixue/sz-deploy](https://github.com/feiyuchuixue/sz-deploy) | [feiyuchuixue/sz-deploy](https://gitee.com/feiyuchuixue/sz-deploy) |

## 适合场景

- **企业后台管理系统**：账户、角色、菜单、部门、参数、字典、日志、资源等基础能力可直接使用。
- **SaaS 管理端**：可基于系统角色、菜单权限、数据权限和模块注册能力继续扩展业务域。
- **二次开发脚手架**：官方模块与业务模块边界清晰，适合长期跟随框架升级。
- **代码生成与快速原型**：内置代码生成器，可生成常见 CRUD、导入导出、菜单和初始化脚本。
- **数据库适配项目**：当前主线对 MySQL 与 PostgreSQL 做了适配优化，部署时二选一启用对应数据库模块。

## v2.0.0 重构亮点

| 方向 | 变化 |
| --- | --- |
| 后端模块 | 新增 `sz-module` 层，`sz-module-admin` 承载后台业务，`sz-service-admin` 只负责启动和装配 |
| 数据库版本 | 主线切换为 Liquibase，按模块维护 changelog，便于多模块演进 |
| 数据库兼容 | 数据权限、代码生成器、初始化脚本同步适配 MySQL / PostgreSQL；运行时按数据库模块二选一启用 |
| 数据权限 | 配置入口合并到系统角色授权，菜单维度保存功能权限和数据范围 |
| 字典体系 | 新增字典来源 `sys_dict_source`，区分框架内置和业务自定义字典 |
| 代码生成器 | 后端迁移到 `sz-module-generator`，前端迁移到 `src/modules/toolbox` |
| 前端架构 | 新增 edition、模块注册、登录适配器和多 HTTP 实例，支持派生项目组合 |
| 会话处理 | 前端统一处理 HTTP 401、业务码 `C105`、blob 下载错误和 WebSocket `4401` |

## 核心能力

- **权限体系**：内置用户、角色、菜单、按钮权限和动态路由，支持超级管理员兜底策略。
- **数据权限**：支持按用户、部门、自定义范围等维度控制数据访问，当前主流程集成在系统角色授权中。
- **数据字典**：支持框架内置字典、业务自定义字典、静态字典预热和动态字典按需加载。
- **代码生成器**：支持读取表结构、配置字段、预览代码、导出菜单和初始化脚本。
- **数据库迁移**：使用 Liquibase 管理框架、生成器、演示业务等模块级 changelog。
- **文件与 OSS**：基于资源场景 `sceneCode` 管理上传路径、访问方式和资源引用。
- **Excel 导入导出**：封装导入模板、失败记录、字典格式化、导出字段配置等常见能力。
- **WebSocket**：独立服务承载实时消息，支持多节点转发、心跳、鉴权失效处理。
- **接口防抖**：内置重复请求防护能力，可按全局或接口维度配置。

## 技术栈

### 后端

| 技术 | 说明 |
| --- | --- |
| Spring Boot 4.x | 核心框架，主线已从历史 Spring Boot 3.x 升级到 4.x |
| JDK 21 | 当前主线运行时，后续会在生态稳定后持续评估新的 Java LTS |
| Sa-Token | 轻量级 Java 权限认证框架 |
| MyBatis-Flex | MyBatis 增强框架，配合项目内数据库能力使用 |
| Liquibase | 数据库版本控制工具，替代历史 Flyway 主线 |
| MySQL / PostgreSQL | 数据库二选一启用，对应 `sz-common-db-mysql` 或 `sz-common-db-postgresql` |
| Redis | 登录态、缓存、字典和部分同步能力 |
| Springdoc OpenAPI | 当前接口文档主线，使用 Swagger UI / OpenAPI JSON |
| AWS S3 SDK | 兼容 S3 协议的对象存储接入，支持 MinIO、阿里云 OSS、腾讯云 OSS 等 |
| FastExcel | Excel 导入导出能力 |

### 前端

| 技术 | 说明 |
| --- | --- |
| Vue 3.5.x | 前端核心框架 |
| Vite 7.3.3 | 前端开发与构建工具 |
| TypeScript 5.2.x | 类型约束和工程可维护性 |
| Element Plus 2.14.x | UI 组件库 |
| Pinia 3.x | 状态管理 |
| Vue Router 5.x | 路由管理 |
| Axios 1.16.x | HTTP 客户端，v2.0.0 使用 `adminHttp` / `auditHttp` / `generatorHttp` 区分接口域 |

## 项目结构

### 后端结构

```text
sz-boot-parent/
├── sz-build               # Spring Boot 父版本、第三方依赖版本与 Maven 插件管理
├── sz-common              # 通用能力模块集合，与业务无关
│   ├── sz-common-core
│   ├── sz-common-db-core
│   ├── sz-common-db-mysql
│   ├── sz-common-db-postgresql
│   ├── sz-common-excel
│   ├── sz-common-log
│   ├── sz-common-oss
│   ├── sz-common-resource
│   └── sz-common-security
├── sz-module              # 业务模块集合，不直接启动
│   ├── sz-module-common
│   ├── sz-module-admin
│   └── sz-module-generator
└── sz-service             # 可独立部署的服务
    ├── sz-service-admin
    └── sz-service-websocket
```

扩展业务时，推荐新增独立 `sz-module-*` 模块，例如 `sz-module-shop`、`sz-module-crm`，再由 `sz-service-admin` 或新的服务引入。这样可以让官方 RBAC、字典、资源、生成器等模块继续跟随版本升级，自有业务也能独立维护。

### 前端结构

```text
sz-admin/
├── src
│   ├── api                # API 请求与接口类型，按业务域组织
│   ├── core               # 登录适配、模块注册、菜单组件解析等底座能力
│   ├── editions           # 产品版本组合入口，决定启用哪些模块能力
│   ├── modules            # 可组合业务模块，如 toolbox / generator
│   ├── views              # 传统页面目录，适合普通 CRUD 和动态路由兜底
│   ├── components         # 全局组件
│   ├── hooks              # 组合式复用逻辑
│   ├── stores             # Pinia 状态
│   ├── router             # 路由与守卫
│   └── config             # 前端常量与配置
├── package.json
└── vite.config.mts
```

前端 v2.0.0 保留 `src/views` 的低成本开发路径，普通系统管理页面、简单 CRUD 和一次性业务可以继续放在 `src/views`；具备独立领域、可选启用、派生项目复用或团队并行开发诉求的业务，建议放入 `src/modules/<domain>`，再通过 `src/core` 与 `src/editions` 进行注册和组合。

动态菜单组件解析会按“模块显式注册 -> `src/modules` 约定路径 -> `src/views` 兜底”的顺序匹配。接口调用通过 `adminHttp` / `auditHttp` / `generatorHttp` 区分管理端、审计与生成器接口，并与 `VITE_ADMIN_API_BASE`、`VITE_AUDIT_API_BASE`、`VITE_GENERATOR_API_BASE` 保持一致。

## 环境要求

| 环境 | 要求 |
| --- | --- |
| JDK | 21 |
| Maven | 3.8+，推荐 3.9.x |
| 数据库 | MySQL 8.0.17+ 或 PostgreSQL 16+，二选一启用 |
| Redis | 7.x |
| Node.js | >= 20.19.0，推荐 20.19.5 |
| pnpm | 10.17.1 |

## 快速启动

> 完整步骤请查看 [快速开始文档](https://szadmin.cn/md/Help/doc/info/start.html)。README 只保留最短启动路径。

### 后端

```shell
git clone https://github.com/feiyuchuixue/sz-boot-parent.git
cd sz-boot-parent
```

1. 创建空数据库，例如 `sz_admin_preview`。
2. 修改 `config/local/mysql.yml` 或 `config/local/postgresql.yml`。
3. 默认使用 MySQL；如切换 PostgreSQL，需要同时修改 `application.yml` 中的 `DB_TYPE=postgresql`，并在 `sz-service/sz-service-admin/pom.xml` 中启用 `sz-common-db-postgresql`、注释 `sz-common-db-mysql`。
4. 修改 `config/local/redis.yml`。
5. 启动 `sz-service/sz-service-admin` 模块中的 `com.sz.AdminApplication`。
6. 如需 WebSocket 实时消息能力，另行启动 `sz-service/sz-service-websocket`。

启动后常用地址：

| 能力 | 地址 |
| --- | --- |
| 后端服务 | `http://127.0.0.1:9991/api` |
| 管理端 API | `http://127.0.0.1:9991/api/admin/**` |
| 审计 API | `http://127.0.0.1:9991/api/audit/**` |
| 生成器 API | `http://127.0.0.1:9991/api/generator/**` |
| Swagger UI | `http://127.0.0.1:9991/api/swagger-ui.html` |
| 健康检查 | `http://127.0.0.1:9991/api/actuator/health` |
| WebSocket | `ws://127.0.0.1:9993/socket` |

> v2.0.0 使用 Liquibase 管理数据库结构和初始化数据，首次启动会自动创建表结构。存量旧库升级不要直接套用快速开始流程，请先阅读 [v2.0.0 升级指南](https://szadmin.cn/md/Help/doc/other/upgrade.html#v2-0-0)。

### 前端

```shell
git clone https://github.com/feiyuchuixue/sz-admin.git
cd sz-admin
corepack enable
corepack prepare pnpm@10.17.1 --activate
pnpm install
pnpm dev
```

本地开发常用 `.env.development.local`：

```properties
VITE_USER_NODE_ENV=development
VITE_PUBLIC_PATH=/
VITE_ADMIN_API_BASE=/api/admin
VITE_AUDIT_API_BASE=/api/audit
VITE_GENERATOR_API_BASE=/api/generator
VITE_API_PROXY_TARGET=http://127.0.0.1:9991
VITE_APP_CLIENT_ID=195da9fcce574852b850068771cde034
VITE_ADMIN_BYPASS_PERMISSION=true
# VITE_SOCKET_URL=ws://127.0.0.1:9993/socket
```

前端启动后访问：

```text
http://localhost:9848/
```

默认账号：

```text
admin / sz123456
```

## 系统截图

<table>
  <tr>
    <td><img alt="登录页" src="https://minioapi.szadmin.cn/public/img/login.webp"/></td>
    <td><img alt="home页" src="https://minioapi.szadmin.cn/public/img/home.webp"/></td>
  </tr>
  <tr>
    <td><img alt="账户管理" src="https://minioapi.szadmin.cn/public/img/account.webp"/></td>
    <td><img alt="角色管理" src="https://minioapi.szadmin.cn/public/img/role.webp"/></td>
  </tr>
  <tr>
    <td><img alt="菜单管理" src="https://minioapi.szadmin.cn/public/img/menu.webp"/></td>
    <td><img alt="字典管理" src="https://minioapi.szadmin.cn/public/img/dict.webp"/></td>
  </tr>
  <tr>
    <td><img alt="配置管理" src="https://minioapi.szadmin.cn/public/img/config.webp"/></td>
    <td><img alt="客户端管理" src="https://minioapi.szadmin.cn/public/img/client.webp"/></td>
  </tr>
  <tr>
    <td><img alt="部门管理" src="https://minioapi.szadmin.cn/public/img/dept.webp"/></td>
    <td><img alt="代码预览" src="https://minioapi.szadmin.cn/public/img/gen-preview.webp"/></td>
  </tr>
  <tr>
    <td><img alt="代码生成配置1" src="https://minioapi.szadmin.cn/public/img/gen-editor.webp"/></td>
    <td><img alt="代码生成配置2" src="https://minioapi.szadmin.cn/public/img/gen-editor2.webp"/></td>
  </tr>
</table>

> 部分截图仍沿用旧版页面，用于展示系统基础形态。v2.0.0 新增的字典来源、角色数据权限、脚本预览、模块化代码生成器等页面，后续会继续补充新版截图。

## 文档导航

- [快速开始](https://szadmin.cn/md/Help/doc/info/start.html)
- [技术选型](https://szadmin.cn/md/Help/doc/info/stack.html)
- [目录结构](https://szadmin.cn/md/Help/doc/base/tree.html)
- [配置说明](https://szadmin.cn/md/Help/doc/base/config.html)
- [数据库支持](https://szadmin.cn/md/Help/doc/base/database.html)
- [Liquibase 数据库版本控制](https://szadmin.cn/md/Help/doc/base/liquibase.html)
- [数据权限](https://szadmin.cn/md/Help/doc/core/data-scope.html)
- [数据字典](https://szadmin.cn/md/Help/doc/core/dict.html)
- [代码生成器](https://szadmin.cn/md/Help/doc/generator/generator-tools.html)
- [v2.0.0 升级指南](https://szadmin.cn/md/Help/doc/other/upgrade.html#v2-0-0)

## 参与讨论

<img alt="加入群聊" src="https://minioapi.szadmin.cn/public/img/wechat.webp" width="260"/>

## 捐赠支持

Sz-Admin 基于 [Apache License 2.0](https://github.com/feiyuchuixue/sz-boot-parent/blob/main/LICENSE) 开源发布。维护项目需要持续投入时间、精力和资源，如果 Sz-Admin 对你的工作或项目有帮助，欢迎通过 Star、Fork、Issue、PR 或捐赠表达支持。

![请 Sz-Admin 作者喝咖啡](https://szadmin.cn/assets/pick.D4fNWjno.webp)

## 版权与品牌

源码遵循 Apache License 2.0。Sz-Admin 名称、Logo、横版品牌图、favicon 等品牌标识与源码授权相互独立；公开使用品牌资产时，请阅读 [版权声明与使用说明](https://szadmin.cn/md/Help/doc/info/copyright.html)。

## 联系方式

- 微信：`xxmmly010`（非诚勿扰）
- 邮箱：`feiyuchuixue@163.com`
