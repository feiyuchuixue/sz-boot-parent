# Sz-Admin 项目知识库约定

## 0. 文档定位

本文档是开发者、AI 编程助手和团队协作时理解 Sz-Admin 体系的基础知识库。凡涉及 `sz-boot-parent`、`sz-admin`、`sz-admin-vitepress`、代码生成器、Liquibase、权限、字典、Excel、OSS、WebSocket、前后端模块化等工作，都应先读取本文档，再进入分析、设计或修改。

最后核验日期：2026-06-02。

核验依据：

1. 后端当前分支实际代码：本仓库 `sz-boot-parent`
2. 前端当前分支实际代码：配套仓库 `sz-admin`
3. 官网文档源码：配套文档仓库 `sz-admin-vitepress/docs/md/Help/doc`
4. 本仓库 `docs/` 下专题文档

知识来源优先级：

1. 当前分支源码、POM、配置、测试和模板。
2. 当前项目内 `docs/` 专题文档。
3. `sz-admin-vitepress` 文档。
4. 历史会话、经验、旧 README 或旧教程。

若本文档与当前源码冲突，以源码为准；若官网文档与当前源码冲突，以当前源码为准。旧文档只能作为迁移背景，不可替代当前事实。

使用建议：

- AI 编程助手或自动化工具应优先读取当前仓库的 `docs/project-knowledge-conventions.md`。
- 本文档只记录项目事实和工程约定，不绑定某个具体 AI 工具。
- 工具专用的加载规则、状态输出、安装方式和回退路径，应放在对应工具自己的说明中，例如 `docs/codex-skills/`，不要写入本项目知识库主体。
- 只确认文件存在不算加载完成，必须读取到内容后再引用本文档结论。

## 1. 项目定位

`sz-boot-parent` 是 Sz-Admin 后端多模块工程。它提供 RBAC、系统管理、审计、代码生成器、数据权限、字典、Excel、资源/OSS、WebSocket 等中后台基础能力。

`sz-admin` 是配套前端工程。它提供后台管理 UI、动态路由、权限指令、模块注册、代码生成器页面、审计页面和通用业务组件。

`sz-admin-vitepress` 是文档站源码。它用于补充说明当前设计，但不能覆盖当前分支源码事实。

二次开发推荐思路：

- 官方核心能力保留在 `sz-module-admin`。
- 有独立边界的业务域放到新的 `sz-module-*` 后端模块。
- 前端完整业务域放到 `src/modules/<domain>`，并通过 `register.ts` 与 edition 注册。
- 普通小型 CRUD 可放入既有 admin 模块和前端旧 `src/views` 结构，不要为了模块化而过度拆分。

## 2. 后端工程结构

当前后端根 POM 只聚合三类顶层模块：

```text
sz-boot-parent
├── sz-common
├── sz-module
└── sz-service
```

`sz-build` 是构建父 POM，通过根 POM 的 parent 引入，不是根聚合模块。

当前 `sz-common` 聚合模块：

```text
sz-common
├── sz-common-core
├── sz-common-db-core
├── sz-common-db-mysql
├── sz-common-db-postgresql
├── sz-common-db-mongodb
├── sz-common-db-redis
├── sz-common-excel
├── sz-common-log
├── sz-common-oss
├── sz-common-resource
├── sz-common-mq
├── sz-common-security
└── sz-common-wechat
```

当前 `sz-module` 聚合模块：

```text
sz-module
├── sz-module-common
├── sz-module-admin
├── sz-module-audit
└── sz-module-generator
```

当前 `sz-service` 聚合模块：

```text
sz-service
├── sz-service-admin
└── sz-service-websocket
```

职责边界：

- `sz-common-*`：框架基础能力、通用工具、数据库方言、Excel、日志、OSS、资源、安全、Redis 等。
- `sz-module-common`：跨业务模块共享的常量、轻量契约和平台级公共定义。
- `sz-module-admin`：官方后台核心，承载 RBAC、系统管理、认证授权、菜单、角色、字典、配置、资源、模板、示例业务等。
- `sz-module-audit`：审计日志模块，可选启用。
- `sz-module-generator`：代码生成器模块。不要再使用旧口径 `sz-common-generator`。
- `sz-service-admin`：主后台启动服务，装配 admin、audit、generator 等模块。
- `sz-service-websocket`：独立 WebSocket 服务，默认用于消息推送、字典同步等场景。

## 3. 后端技术栈

当前以后端 POM 为准：

| 项 | 当前事实 |
| --- | --- |
| Java | 21 |
| 项目版本 | `2.0.0-SNAPSHOT`，通过 `${revision}` 管理 |
| Spring Boot | 4.0.6 |
| Sa-Token | 1.45.0，使用 Spring Boot 4 starter |
| ORM | MyBatis-Flex 1.11.7 |
| 数据库迁移 | Liquibase XML changelog |
| API 文档 | Springdoc OpenAPI / Swagger UI |
| 数据库 | MySQL、PostgreSQL 双库支持 |
| 缓存 | Redis |
| 对象存储 | AWS S3 SDK 兼容适配，配合资源场景抽象 |
| Excel | FastExcel 与 `sz-common-excel` |
| 常用工具 | Lombok、ModelMapper、Hutool、Apache Commons |

当前不应再按 Spring Boot 3、Flyway、EasyExcel 或 `sz-common-generator` 的旧口径理解主线代码。

## 4. 配置与启动

`sz-service-admin` 的根配置位于：

```text
sz-service/sz-service-admin/src/main/resources/application.yml
```

环境配置位于：

```text
sz-service/sz-service-admin/src/main/resources/application-local.yml
sz-service/sz-service-admin/src/main/resources/application-dev.yml
sz-service/sz-service-admin/src/main/resources/application-preview.yml
sz-service/sz-service-admin/src/main/resources/application-prod.yml
```

功能拆分配置位于仓库根目录：

```text
config/{local,dev,preview,prod}/
├── audit-log.yml
├── knife4j.yml
├── mybatis-flex.yml
├── mysql.yml
├── oss.yml
├── page-helper.yml
├── postgresql.yml
├── redis.yml
└── sa-token.yml
```

`application.yml` 通过 `spring.config.import` 导入：

```yaml
file:config/${spring.profiles.active}/knife4j.yml
file:config/${spring.profiles.active}/audit-log.yml
file:config/${spring.profiles.active}/oss.yml
file:config/${spring.profiles.active}/mybatis-flex.yml
file:config/${spring.profiles.active}/${DB_TYPE}.yml
file:config/${spring.profiles.active}/page-helper.yml
file:config/${spring.profiles.active}/redis.yml
file:config/${spring.profiles.active}/sa-token.yml
```

服务默认上下文与端口：

- `server.servlet.context-path=/api`
- `server.port=9991`
- WebSocket 独立服务文档默认端口为 `9993`

数据库切换必须同时满足两件事：

1. `application.yml` 中 `DB_TYPE` 决定运行时加载 `mysql.yml` 还是 `postgresql.yml`。
2. `sz-service-admin/pom.xml` 中 Maven profile 决定构建时引入 `sz-common-db-mysql` 还是 `sz-common-db-postgresql`。

当前分支核验时，`application.yml` 中 `DB_TYPE` 为 `postgresql`。这属于当前工作树事实，开发时仍需按目标环境确认，不要默认所有环境都是 PostgreSQL。

数据库 profile：

- `mysql`：默认激活，引入 `sz-common-db-mysql`。
- `postgresql`：显式激活，引入 `sz-common-db-postgresql`。

构建 PostgreSQL 版本时应显式使用：

```bash
mvn -pl sz-service/sz-service-admin -am compile -DskipTests -Ppostgresql
```

不要同时启用 `mysql` 与 `postgresql`，否则可能出现驱动、方言或 TypeHandler 冲突。

`knife4j.yml` 是历史文件名。当前 Spring Boot 4 主线以 `springdoc-openapi-starter-webmvc-ui` 提供 Swagger UI / OpenAPI JSON；不要把 Knife4j UI 当作当前主线已适配能力。

## 5. API 前缀与模块接入

`sz.api-prefix` 不是 `server.servlet.context-path`。它由 `ApiPrefixConfiguration` 收集各模块提供的 `ApiPrefixRegister`，再按 Controller 包名追加模块业务前缀。

当前默认模块前缀：

| 模块 | 配置项 | 默认前缀 | 典型 Controller 包 |
| --- | --- | --- | --- |
| admin | `sz.api-prefix.modules.admin` | `/admin` | `com.sz.admin.*`、`com.sz.applet.*`、`com.sz.security.controller`、`com.sz.www` |
| audit | `sz.api-prefix.modules.audit` | `/audit` | `com.sz.audit.controller` |
| generator | `sz.api-prefix.modules.generator` | `/generator` | `com.sz.generator.controller` |

示例：

| Controller 路径 | 模块前缀 | context-path | 最终访问路径 |
| --- | --- | --- | --- |
| `/auth/login` | `/admin` | `/api` | `/api/admin/auth/login` |
| `/sys-user` | `/admin` | `/api` | `/api/admin/sys-user` |
| `/sys-operation-log` | `/audit` | `/api` | `/api/audit/sys-operation-log` |
| `/generator-table` | `/generator` | `/api` | `/api/generator/generator-table` |

新增独立后端模块时需要：

1. 在 `sz-module` 下创建 `sz-module-xxx`。
2. 在根 POM dependencyManagement 中声明 `sz-module-xxx`。
3. 在 `sz-module/pom.xml` 聚合 `<module>sz-module-xxx</module>`。
4. 在 `sz-service-admin/pom.xml` 引入模块依赖。
5. 模块提供 `ApiPrefixRegister`，`module()` 与 `sz.api-prefix.modules.xxx` 一致。
6. 模块提供独立 `@MapperScan` 配置，扫描自己的 mapper 包。
7. 若模块包含 Excel 导入模板，提供 `@EnableExcelTemplateScan` 配置。
8. 提供模块 Liquibase 入口并在服务 master changelog 中 include。
9. 前端同步 API base、模块目录、菜单 component、路由解析和代理配置。

`enabled: false` 只是不注册该模块 Controller 前缀，不等于完整卸载模块。完整移除模块还要同步移除服务 POM 依赖、Liquibase include、前端模块、菜单初始化数据、代理配置、资源 base-url、白名单等。

## 6. 后端业务代码规范

标准 CRUD 后端包结构以 `teacher_statistics` 示例为参考：

```text
com.sz.admin.teacher
├── controller
├── mapper
├── pojo
│   ├── dto
│   ├── po
│   └── vo
└── service
    └── impl
```

独立模块可使用自己的根包，例如 `com.sz.smart`、`com.sz.crm`，但仍应保持 controller、mapper、pojo、service 分层。

DTO、PO、VO 分工：

- DTO：入参对象，面向 create/update/list/import 等场景。
- PO：数据库实体，与表结构映射。
- VO：接口返回对象，可带 Excel、字典、OSS URL 回填等展示注解。

Controller 约定：

- 使用 REST 风格路径。
- 使用 `@Tag`、`@Operation`、`@Schema` 等 OpenAPI 注解。
- 需要鉴权的接口使用 `@SaCheckPermission`，除登录、公共资源等明确白名单外不要省略。
- 数据权限依赖权限识别，涉及数据权限的接口必须有明确权限标识。
- 返回统一使用 `ApiResult`、`ApiPageResult` 等框架响应结构。

Service 常见方法名：

```java
void create(XxxCreateDTO dto);
void update(XxxUpdateDTO dto);
PageResult<XxxVO> page(XxxListDTO dto);
List<XxxVO> list(XxxListDTO dto);
void remove(SelectIdsDTO dto);
XxxVO detail(Long id);
void importExcel(ImportExcelDTO dto);
void exportExcel(XxxListDTO dto, HttpServletResponse response);
```

依赖注入：

- 优先使用 `@RequiredArgsConstructor` + `final` 字段。
- 或使用 `@Resource`。
- 非特殊情况不要使用 `@Autowired`。

主键与 ID：

- 主键和关联 ID 通常使用 `bigint`，Java 映射为 `Long`。
- 当前主线使用雪花 ID：`SzIdGenerator`，配置位于 `mybatis-flex.yml` 下 `sz.id.worker-id`、`sz.id.datacenter-id`。
- 多节点部署必须使用不同 worker/datacenter 组合，避免 ID 冲突。

自动填充：

- 推荐字段：`create_id`、`create_time`、`update_id`、`update_time`。
- PO 通过 `@Table(value = "...", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)` 启用。
- 字段不存在时监听器会忽略，不应靠手写重复填充覆盖框架行为。

逻辑删除：

- 当前框架文档推荐新表使用跨库友好的字符字段，例如 `del_flag`，值为 `F/T`。
- PO 使用 `@Column(isLogicDelete = true)` 标识逻辑删除字段。
- 逻辑删除值由 `mybatis-flex.yml` 中 `deleted-value-of-logic-delete` 与 `normal-value-of-logic-delete` 配置。
- 如果需要删除人/删除时间自动填充，PO 使用 `@LogicDeleteFill`，由 `EntityLogicDeleteListener` 在逻辑删除 SQL 中补充。
- 外部或历史 DDL 若使用 `is_deleted=0/1`，不能直接套用 `del_flag F/T` 结论；需要核对生成器识别、MyBatis-Flex 全局配置和目标表字段，再决定是否转换或保留。

## 7. Liquibase 与数据库兼容

当前版本不再使用 Flyway，数据库版本控制以 Liquibase XML 为准。

服务总入口：

```text
sz-service/sz-service-admin/src/main/resources/db/changelog/changelog-master.xml
```

当前服务 master include：

```xml
<include file="db/changelog/module-admin-changelog.xml"/>
<include file="db/changelog/module-audit-changelog.xml" errorIfMissing="false"/>
<include file="db/changelog/module-generator-changelog.xml" errorIfMissing="false"/>
```

当前模块入口：

```text
sz-module-admin/src/main/resources/db/changelog/module-admin-changelog.xml
sz-module-audit/src/main/resources/db/changelog/module-audit-changelog.xml
sz-module-generator/src/main/resources/db/changelog/module-generator-changelog.xml
```

模块内继续按领域和版本拆分，例如：

- admin：`framework/changelog-master.xml`、`demo/changelog-master.xml`
- audit：`audit/2.0.0/001_audit_init.xml`
- generator：`generator/2.0.0/001_generator_init.xml`、`generator/2.0.1/001_generator_module_target_fields.xml`

编写规则：

- 使用显式 `<include file="...">`，避免无边界 `includeAll`。
- 发布后的 `changeSet id` 不要随意修改。
- 新增表、索引、字段、初始化数据尽量使用结构化标签。
- 可重复安全变更使用 `preConditions onFail="MARK_RAN"`。
- 框架核心、demo、generator、audit、自定义业务模块的 changelog 不要混写。
- 菜单、按钮、角色、字典、配置等初始化数据应随模块维护。

双库兼容：

- MySQL 最低按 8.0.17 理解；PostgreSQL 最低按 9.4、建议 16+ 理解。
- 普通类型优先使用 Liquibase property 变量统一，例如 `${datetime.type}`、`${bool.type}`、`${bigint.type}`、`${now.function}`。
- 无法统一表达的内容用 `dbms="mysql"` / `dbms="postgresql"` 拆分 changeSet。
- 不要把 MySQL 单方言 SQL 当作 PostgreSQL 兼容证明，也不要反过来。

高风险差异：

| 场景 | MySQL | PostgreSQL | 处理建议 |
| --- | --- | --- | --- |
| 逻辑标识 | `CHAR(1)` | `VARCHAR(1)` | 用 `${bool.type}` |
| 时间 | `DATETIME` | `TIMESTAMP` | 用 `${datetime.type}` |
| 大文本 | `MEDIUMTEXT` | `TEXT` | 必要时按 dbms 拆分 |
| 部门范围 `dept_scope` | `JSON` | `BIGINT[]` | 按 dbms 拆分并配 TypeHandler |
| 标识符包裹 | 反引号 | 双引号 | 交给方言层，不要手拼 |
| 聚合/冲突 SQL | `GROUP_CONCAT`、`INSERT IGNORE` 等 | PostgreSQL 语法不同 | 固定库可用，双库需隔离 |

新业务如果必须同时兼容 MySQL 和 PostgreSQL，应把表结构、索引、TypeHandler、Mapper XML、初始化数据和测试数据一起检查。

## 8. 数据权限

v2.0.0 的数据权限已经集成到系统角色授权体系，不再按独立“数据角色菜单”理解。

关键表/字段：

- `sys_menu.use_data_scope`
- `sys_role_menu.permission_type=scope`
- `sys_role_menu.data_scope_cd`
- `sys_data_role_relation`
- 业务表可用 `create_id` 或 `dept_scope` 支撑数据权限过滤。

配置：

```yaml
sz:
  data-scope:
    enabled: true
    logic-min-unit: user
    allow-admin-view: false
```

`logic-min-unit`：

- `user`：以 `create_id` 为最小过滤单位。
- `dept`：以 `dept_scope` 为部门范围过滤单位。

代码使用方式：

- Service 查询需要显式开启数据权限上下文，例如 `try (var ignored = new DataScopeSession(Xxx.class)) { ... }`。
- 仅配置全局开关不会让所有查询自动带数据权限。
- 当前示例 `TeacherStatisticsServiceImpl` 已使用 `DataScopeSession`。

数据库方言：

- MySQL 使用 `MysqlPermissionDialect`。
- PostgreSQL 使用 `PostgresqlPermissionDialect`。
- 方言根据数据源驱动自动激活。
- `dept_scope` Java 字段统一使用 `List<Long>` 并配置 `LongListTypeHandler`，但数据库类型和索引策略要分库处理。

不要绕过数据权限的常见坑：

- Controller 缺少 `@SaCheckPermission`。
- Mapper XML 写死单库 SQL。
- 查询没有开启 `DataScopeSession`。
- 表里没有 `create_id` 或 `dept_scope`，却开启了对应数据权限策略。
- 角色菜单没有配置 `use_data_scope` 或 scope 权限。

## 9. 数据字典

v2.0.0 字典模型由三层组成：

| 层级 | 表/实现 | 说明 |
| --- | --- | --- |
| 字典来源 | `sys_dict_source` | 区分 framework、custom、独立业务模块来源和 ID 号段 |
| 字典类型 | `sys_dict_type` | 管理 `type_code`、`type_name`，归属到 `source_code` |
| 字典项 | `sys_dict` | 管理 `code_name`、`alias`、展示样式、排序、状态 |

默认来源：

- `framework`：框架内置，默认号段 `1000-1999`。
- `custom`：业务自定义，默认号段 `2000-2999`。

独立业务模块建议新增自己的字典来源和号段，避免与框架升级或其他模块冲突。

后端字典约定：

- 业务代码不要硬编码字典项 ID 字符串。
- 框架字典常量位于 `sz-module-common` 的 `com.sz.platform.constant.dict` 包。
- 新增业务字典后建议维护对应常量类，命名如 `{业务名}Constant`。

静态字典接口：

- `GET /sys-dict/static`
- `GET /sys-dict/code?typeCode=xxx&typeCode=yyy`
- 当前没有 `GET /sys-dict/dict/{typeCode}`。

动态字典：

- 实现 `DynamicDictLoader`。
- 完整 typeCode 默认是 `dynamic_` + `getTypeCode()`。
- 当前内置动态字典包含用户、部门、角色、字典来源等选项。
- `DictLoaderFactory` 会精确注册动态字典，重复 typeCode 会导致启动异常。

前端字典使用：

- `useDict([...])` 主动加载。
- `useDictOptions(typeCode)` 获取响应式 options。
- `ProTable` 列可通过 `enum`、`fieldNames`、`tag` 使用字典。
- WebSocket 收到字典同步事件后，前端会标记相关字典过期并重新加载。

代码生成器与字典：

- 生成器支持字段关联静态/动态字典。
- 生成 CRUD 时要检查疑似字典字段，例如 `status`、`type`、`level`、`flag`。
- 生成初始化脚本时要考虑字典来源与 ID 号段，不要把业务字典塞进 framework 号段。

## 10. Excel 能力

当前主线已从 EasyExcel 切换为 FastExcel。

核心能力位于 `sz-common-excel`：

- `ExcelUtils.importExcel`
- `ExcelUtils.exportExcel`
- `@DictFormat`
- `@CellMerge`
- `@ExcelTemplate`
- `@ImportColumn`
- `@EnableExcelTemplateScan`
- `AbstractExcelImportTemplate<T>`

导入/导出约定：

- 导入 DTO 使用 `@ExcelProperty` 标记列。
- 字典字段可使用 `@DictFormat(dictType = "...")` 实现导入展示值到系统值、导出系统值到展示值。
- 导出 VO 可用 `@ExcelIgnore` 忽略字段。
- 导入模板可通过 `@ExcelTemplate(alias = "...")` 和 `@ImportColumn` 生成。
- 模块若有导入模板，要通过 `@EnableExcelTemplateScan(basePackages = "...")` 扫描。

模板查找优先级：

1. `classpath:/templates/{templateName}`
2. `sys_temp_file` 表中手动上传模板
3. `ExcelTemplateScanRegistry` 根据 DTO 注解动态生成

统一导入框架：

- 业务导入器继承 `AbstractExcelImportTemplate<T>`。
- 实现 `importDtoClass()`、`bizType()`、`bizName()`、`doImport()`、`convertExcelFailItems()`。
- 框架负责批次、分片、失败记录和状态收口。
- 前端通用导入弹窗是 `ImportExcel`。

## 11. OSS 与资源文件

当前文件体系分两层：

| 模块 | 职责 | 配置前缀 |
| --- | --- | --- |
| `sz-common-oss` | 厂商适配层，封装 S3 兼容对象存储 | `sz.oss.*` |
| `sz-common-resource` | 业务资源抽象层，按 sceneCode 管理上传、路径、命名、访问模式 | `sz.resource.*` |

`oss.yml` 中应同时维护：

- `sz.oss`：provider、endpoint、accessKey、secretKey、bucketName、domain、scheme。
- `sz.resource`：root、default-storage-type、security.allowed-exts、max-size、scenes。

上传约定：

- 统一通过资源上传接口上传。
- 前端必须传 `sceneCode`。
- 数据库存储 `objectKey`，不是完整 `accessUrl`。
- 上传返回的 `accessUrl` 只用于即时预览。
- 查询返回时由后端 `@OssUrlFill(sceneCode = "...")` 将 objectKey 转为可访问 URL。

前端组件：

- 单图：`Img`
- 多图：`Imgs`
- 多文件：`UploadFiles`
- 富文本：`JoditEditor`

旧版 `dir` 属性已废弃，统一使用 `sceneCode`。

如果 API 前缀、Nginx 或资源 base-url 变化，必须同步 `sz.resource.scenes[].base-url`，否则文件预览/下载会 404。

## 12. 审计、防抖与 WebSocket

审计日志：

- 模块：`sz-module-audit`。
- 配置：`config/{profile}/audit-log.yml`。
- 注解：`@OperationAudit`、`@OperationAuditIgnore`。
- 依赖 OpenAPI 的 `@Tag`、`@Operation` 获取业务语义。
- 可配置记录模式、SQL 审计、traceId 等。
- 审计是可选模块；不使用时要同步处理模块依赖、changelog、前端模块和菜单。

防抖：

```yaml
sz:
  debounce:
    enabled: true
    global-lock-time: 500
    ignore-get-method: true
```

相关注解：

- `@Debounce`
- `@DebounceIgnore`

WebSocket：

- 模块/服务：`sz-service-websocket`。
- 默认端口：`9993`。
- 默认 endpoint：`/socket`。
- 前端通过 `VITE_SOCKET_URL` 启用；为空或未配置时不启用。
- 认证 token 通过 `Sec-WebSocket-Protocol` 传递。
- 未授权关闭码使用 `4401`。
- 字典同步、消息推送等业务通过 WebSocket 与 Redis Pub/Sub 协作。

## 13. 代码生成器

代码生成器当前位于：

```text
sz-module/sz-module-generator
```

不要按旧的 `sz-common-generator` 查找。

前端代码生成器页面位于：

```text
sz-admin/src/modules/toolbox/views/generator
```

生成器后端典型能力：

- 数据源表导入。
- 表配置查询与保存。
- 字段推断与编辑。
- 生成预览。
- 生成磁盘检查。
- 实际写入代码。
- 下载 zip。
- 后端模块扫描。
- 新后端模块骨架生成。
- API prefix、MapperScan、ExcelTemplateScan 配置生成。
- 前端模块 `src/modules/<domain>` 结构生成。
- Liquibase/XML/SQL、菜单、字典、角色权限脚本输出。

实际生成 CRUD 时的流程：

1. 确认目标表、目标后端模块、目标前端模块、生成范围。
2. 如果用户提供 `CREATE TABLE` DDL，先解析表名、表注释、主键、字段注释、自动填充字段、逻辑删除字段、数据权限字段、字典候选、上传/图片/富文本/JSON 候选。
3. 除非当前生成器明确支持直接 DDL 文本导入，否则优先在确认的本地/开发数据库中建表，再走生成器导入。
4. 导入后复核生成配置：类名、业务名、包名、路径、API 前缀、权限、字典、Excel、上传、数据权限。
5. 先预览和磁盘检查，再实际生成。
6. 生成后检查两个仓库变更，并运行最小有效验证。

生成器目标模块判断：

- `sz-module-common` 与 `sz-module-generator` 不适合作普通 CRUD 生成目标。
- `sz-module-admin` 适合框架核心或普通 admin 小型 CRUD。
- 有独立边界的业务应新建 `sz-module-*`。
- 前端新业务域优先放到 `src/modules/<domain>`，并提供 `register.ts`。

生成产物清理规则：

- 表级产物可删除：Java package、Mapper XML、前端 api/types/views、表级 Liquibase、SQL 导出文件、同表名文档。
- 宿主文件只能删除生成插入片段：根/模块/服务 POM、服务 changelog include、`application.yml` API prefix 块、前端模块注册项、模块 changelog include。
- 已有模块清理时，保留模块壳、POM、配置类、`register.ts` 和非本次生成内容。
- 新增模块清理时，如果模块目录未跟踪且清理后只剩生成壳，可删除后端模块目录、前端模块目录、register、POM 挂载、API prefix、changelog 挂载。
- 判断模块是否新增要结合 Git 状态、目录内容、宿主挂载和用户范围，不能只看表名。
- 清理后用 `rg` 搜表名、类名、kebab 名、camel 名、模块 artifact、changelog include、API prefix、前端路径。

## 14. 前端工程结构与约定

当前前端以 `sz-admin/package.json` 为准：

| 项 | 当前事实 |
| --- | --- |
| Node | `>=20.19.0` |
| package manager | `pnpm@10.17.1` |
| Vue | `^3.5.35` |
| Vite | `7.3.3` |
| TypeScript | `~5.2.2` |
| Vue Router | `^5.0.7` |
| Pinia | `^3.0.4` |
| Element Plus | `^2.14.0` |
| Axios | `1.16.1` |
| Vue I18n | `^9.14.5` |

常用脚本：

```bash
pnpm run dev
pnpm run type-check
pnpm run lint
pnpm run build
```

API client 位于：

```text
src/api/client.ts
```

当前内置 client：

- `adminHttp`：默认 `/api/admin`
- `generatorHttp`：默认 `/api/generator`
- `auditHttp`：默认 `/api/audit`
- `createModuleHttp(moduleCode, apiPrefix?)`：动态模块 client，默认拼接 `VITE_API_CONTEXT_PATH` 与 `/<moduleCode>`

环境变量：

- `VITE_ADMIN_API_BASE`
- `VITE_GENERATOR_API_BASE`
- `VITE_AUDIT_API_BASE`
- `VITE_API_CONTEXT_PATH`
- `VITE_API_PROXY_TARGET`
- `VITE_SOCKET_URL`
- `VITE_ADMIN_BYPASS_PERMISSION`

`vite.config.mts` 会根据上述 API base 和 proxy target 生成代理规则。后端 API 前缀变化时，`.env*`、Vite proxy、Nginx location、Sa-Token 白名单、资源 base-url 都要同步。

前端模块体系：

- 核心定义：`src/core/module.ts`
- 默认 edition：`src/editions/admin.ts`
- 自动发现：`src/editions/module-discovery.ts`
- 模块目录：`src/modules/<domain>`
- 模块入口：`src/modules/<domain>/register.ts`

当前默认 edition：

- 设置本地账号密码登录适配器。
- 手动注册 `auditModule` 与 `toolboxModule`。
- 调用 `registerDiscoveredModules()` 自动加载 `src/modules/**/register.ts`。

菜单 component 解析顺序：

1. 已注册模块的 `components` 显式映射。
2. `src/modules/<domain>/views/<rest>.vue` 约定路径。
3. `src/views/<component>.vue` 旧目录兜底。

权限：

- 后端接口权限使用 `@SaCheckPermission`。
- 前端按钮权限使用 `v-auth`。
- `v-auth` 支持单权限字符串、AND 条件、OR 条件。
- `VITE_ADMIN_BYPASS_PERMISSION` 可控制 admin 前端按钮权限放行，生产通常不应随意开启。

普通页面可以继续使用旧结构：

```text
src/api/modules/<domain>
src/api/types/<domain>
src/views/<domain>/<page>
```

完整业务域或代码生成器模块推荐使用：

```text
src/modules/<domain>
├── api
├── types
├── views
└── register.ts
```

## 15. 常用验证

后端最小编译验证：

```bash
E:\opt\apache-maven-3.9.6-bin\apache-maven-3.9.6\bin\mvn.cmd -pl sz-service/sz-service-admin -am compile -DskipTests
```

PostgreSQL profile：

```bash
E:\opt\apache-maven-3.9.6-bin\apache-maven-3.9.6\bin\mvn.cmd -pl sz-service/sz-service-admin -am compile -DskipTests -Ppostgresql
```

MySQL profile：

```bash
E:\opt\apache-maven-3.9.6-bin\apache-maven-3.9.6\bin\mvn.cmd -pl sz-service/sz-service-admin -am compile -DskipTests -Pmysql
```

后端验证前应确认 `JAVA_HOME` 指向 JDK 21。Windows PowerShell 示例：

```powershell
$env:JAVA_HOME='<your-jdk-21-path>'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

前端验证：

```bash
pnpm run type-check
pnpm run lint
pnpm run build
```

生成或清理代码后必须至少检查：

- `git status --short`
- `rg` 搜表名、类名、模块名、API prefix、changelog include、菜单权限。
- 后端受影响模块是否能编译。
- 前端类型检查是否通过。
- 涉及 UI 布局或交互时打开本地页面检查。

Liquibase 变更至少检查：

- XML 是否结构完整。
- include 路径是否正确。
- changeSet ID 是否重复。
- MySQL 与 PostgreSQL 差异是否都处理。
- 初始化数据是否幂等。

## 16. 已更正的旧口径

以下旧说法不应再作为当前项目事实：

- 旧：根工程有 `sz-dependencies` 顶层聚合。  
  新：当前根聚合是 `sz-common`、`sz-module`、`sz-service`；`sz-build` 是构建父 POM。

- 旧：代码生成器在 `sz-common-generator`。  
  新：当前代码生成器在 `sz-module/sz-module-generator`。

- 旧：数据库迁移使用 Flyway。  
  新：当前主线使用 Liquibase XML changelog。

- 旧：只考虑 MySQL。  
  新：当前主线支持 MySQL 与 PostgreSQL，`DB_TYPE` 和 Maven profile 必须一致。

- 旧：数据库 enum 或 Java enum 可直接作为业务状态落库。  
  新：跨库场景优先使用稳定编码、字典或常量；不要新增 MySQL enum。

- 旧：Knife4j UI 是当前主线接口文档能力。  
  新：配置文件名仍可能叫 `knife4j.yml`，但 Spring Boot 4 当前主线以 Springdoc Swagger UI / OpenAPI JSON 为准。

- 旧：Excel 基于 EasyExcel。  
  新：当前主线使用 FastExcel，并有导入模板扫描、统一导入批次和失败记录能力。

- 旧：文件上传直接存完整 URL。  
  新：数据库存 `objectKey`，查询返回通过 `@OssUrlFill` 转完整 URL。

- 旧：前端 API 文件手动拼 `/admin`、`/audit`、`/generator`。  
  新：使用 `adminHttp`、`auditHttp`、`generatorHttp`、`createModuleHttp` 和 `.env*` 统一配置。

- 旧：前端 Vue Router 4、Pinia 2、Vite 6。  
  新：当前 `package.json` 为 Vue Router 5、Pinia 3、Vite 7。

- 旧：数据权限是独立数据角色菜单即可生效。  
  新：数据权限已集成到角色菜单授权体系，且查询代码必须显式开启 `DataScopeSession`。

## 17. AI 协作注意事项

进行 Sz 体系任务时，不要只读单个文件就下结论。至少按任务边界读取相关事实：

- 后端模块任务：POM、配置、Controller、Service、Mapper、PO/DTO/VO、Liquibase、测试。
- 前端模块任务：API client、`.env*`、route/module 注册、页面组件、权限指令、类型定义。
- 代码生成器任务：生成器 service/model/template、前端生成器页面、生成产物、清理路径。
- Liquibase 任务：service master、module changelog、版本 init、实际 changeSet、数据库 profile。
- 字典/菜单/权限任务：后端 changelog、系统表模型、前端权限指令、菜单 component 解析。

高风险不确定性必须停下来确认：

- 实际创建或删除数据库表。
- 批量生成代码写入真实仓库。
- 删除整个模块目录。
- 修改生产配置或密钥。
- 切换 Git 分支、提交、推送、reset、stash。
- 不能判断模块是已有模块还是新生成模块。

低风险细节可以声明假设后推进，但必须在最终汇报中列出验证结果和剩余风险。
