# 当前代码地图

## 后端

- 模块：`sz-module/sz-module-generator`
- Controller：`src/main/java/com/sz/generator/controller/GeneratorTableController.java`
- 启用条件：`config/ConditionalOnGeneratorEnabled.java`，由 `sz.api-prefix.modules.generator.enabled` 控制，默认启用。
- API prefix：`config/GeneratorApiPrefixConfiguration.java`，module 为 `generator`，prefix 为 `/generator`，base package 为 `com.sz.generator.controller`。
- 配置属性：`pojo/property/GeneratorProperties.java`，prefix 为 `sz.generator`。
- 主服务：`service/impl/GeneratorTableServiceImpl.java`
- 代码模型：`core/CodeModelBuilder.java`
- 模块扫描：`core/module/GeneratorBackendModuleScanner.java`
- 脚本渲染：`core/script/ScriptExportService.java`、`ScriptDialectResolver.java`、`ScriptDialect.java`、`SqlValueFormatter.java`
- 后端模块候选：`GeneratorBackendModuleOptionVO`、`GeneratorPathOptionsVO`

## 后端端点

`GeneratorTableController` 暴露：

- `POST import`
- `GET schema/list`
- `GET list`
- `GET /{tableName}`
- `PUT /`
- `POST generator/{tableName}`
- `DELETE /`
- `POST zip`
- `GET preview/{tableName}`
- `GET path/options`
- `GET check/{tableName}`

实际代码生成任务应使用这些端点或前端生成器 UI。直接生成前优先使用 preview/check，并确认是否允许直接写入磁盘。

## 模板

- Java API 模板：`src/main/resources/templates/api/*.ftl`
- Vue 模板：`src/main/resources/templates/vue/*.ftl`
- SQL 模板：`src/main/resources/templates/sql/*.ftl`
- Liquibase 模板：`src/main/resources/templates/liquibase/*.ftl`
- Admin 脚本模板：`sz-module-admin/src/main/resources/templates/admin-script/liquibase/*.ftl`

## 前端

- 项目：配套前端仓库 `sz-admin`
- 生成器模块 API：`src/modules/toolbox/api/generator.ts`
- 生成器类型：`src/modules/toolbox/types/generator.ts`
- 主页面：`src/modules/toolbox/views/generator/index.vue`
- 导入弹窗：`src/modules/toolbox/views/generator/components/Import.vue`
- 编辑弹窗：`src/modules/toolbox/views/generator/components/EditForm.vue`
- 字段步骤：`FieldConfigStep.vue`、`FieldDetailPanel.vue`
- 预览树：`Preview.vue`
- Toolbox 注册：`src/modules/toolbox/register.ts`

## 当前行为事实

- 前端使用来自 `@/api/client` 的 `generatorHttp`。
- 生成器编辑流程是三步：基本信息、字段信息、生成边界。
- 生成类型支持全栈、仅后端、仅数据库。
- 实际代码生成由现有生成器流程支持：导入表、编辑生成设置、预览、检查磁盘/目标边界、生成代码或下载 zip。
- 用户提供原始 `CREATE TABLE` SQL 时，把它视为生成请求输入和配置推断线索。除非当前生成器代码明确支持 DDL 文本导入，否则优先把表创建/导入到本地或开发数据库，再使用现有生成器流程。
- 后端目标支持已有模块和新增模块。
- 前端生成布局默认使用模块布局。
- 模型把生成的 Vue 输出映射到 `src/modules/<frontendModule>/api`、`types`、`views` 和模块布局的 `register.ts`。
- 内置 API 模块 `admin`、`audit`、`generator` 使用内置 client；其他模块使用动态模块 HTTP 输出。
- 脚本导出可渲染 Liquibase XML 和指定方言 SQL。

## DDL 驱动的生成输入

当用户提供 `CREATE TABLE` 并要求生成 CRUD 时：

- 提取表名、表注释、主键和字段注释。
- 根据字段名和注释推断候选生成配置：
  - `*_status`、`*_type`、`priority_level`：疑似字典/标签字段。
  - `*_flag`、`is_*`：疑似开关或类布尔字段。
  - `*_amount`、`*_rate`、`*_count`、`sort_order`：数字输入、金额、百分比或排序字段。
  - `*_date`、`*_time`、`pay_time`、`create_time`、`update_time`、`delete_time`：日期/时间范围或自动填充字段。
  - `*_file`、`*_image`、`avatar`、`attachment_urls`：上传、图片、头像或多文件字段。
  - `remark`、`rich_content`、`extra_config`：多行文本、富文本或 JSON 类字段。
  - `create_id`、`create_time`、`update_id`、`update_time`：自动填充字段。
  - `delete_id`、`delete_time`、`is_deleted`：逻辑删除字段。
  - `dept_scope`：数据权限字段；涉及迁移或脚本时配合 `sz-liquibase-db-compat` 检查数据库方言兼容。
- 确认目标后端模块、前端模块、包名/业务名、生成模式和是否允许直接写入。
- 如果需要执行 DDL，先询问目标数据库/profile，再运行。
- 表导入后，使用生成器详情/编辑页复核和修正推断配置，再生成。

## 生成产物清理地图

用户要求清理某张表或某个模块的生成代码时使用本节。

后端表级产物通常包括：

- `sz-module/sz-module-<module>/src/main/java/com/sz/<module>/<business>` 下的 Java package
- `sz-module/sz-module-<module>/src/main/resources/mapper/<business>` 下的 Mapper XML
- `sz-module/sz-module-<module>/src/main/resources/db/changelog/<module>/unreleased` 下的模块 changelog 文件
- `docs/test-sql` 或用户指定导出目录下的表 SQL 导出
- 提到相同表名、类名或业务名的生成文档

后端新增模块挂载可能包括：

- `sz-module/pom.xml` 模块项：`<module>sz-module-<module></module>`
- 根 `pom.xml` 中 `sz-module-<module>` 的 dependency management 项
- `sz-service/sz-service-admin/pom.xml` 中对 `sz-module-<module>` 的依赖
- `sz-service/sz-service-admin/src/main/resources/application.yml` 中 `sz.api-prefix.modules.<module>` 配置块
- `sz-service/sz-service-admin/src/main/resources/db/changelog/changelog-master.xml` 中 `module-<module>-changelog.xml` include
- 模块级 `pom.xml`、API-prefix 配置类、mapper 扫描配置类、Excel 模板扫描配置类和模块 changelog 文件

前端表级产物通常包括：

- `src/modules/<module>/api/<business>.ts`
- `src/modules/<module>/types/<business>.ts`
- `src/modules/<module>/views/<business>`
- `src/modules/<module>/register.ts` 中的生成组件注册项

仅当 `src/modules/<module>` 是未跟踪/新建目录，并且清理表后不含非生成文件时，新增模块清理才可以删除整个前端模块目录。如果模块原本存在，保留目录和 `register.ts`，只删除生成表文件和生成组件注册项。

清理验证应搜索表 snake 名、类名、camel 名、kebab 名、后端 artifactId、changelog include、API prefix 块和前端模块路径，然后分别在 `sz-boot-parent` 和 `sz-admin` 执行 `git status --short`。

## 验证命令

后端：

```powershell
$env:JAVA_HOME='<your-jdk-21-path>'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn -pl sz-service/sz-service-admin -am compile -DskipTests
```

前端：

```powershell
pnpm type-check
pnpm lint
pnpm build
```

有更窄测试时优先运行，例如 `sz-module/sz-module-generator/src/test/java` 下的生成器测试。
