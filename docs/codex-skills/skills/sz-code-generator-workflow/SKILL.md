---
name: sz-code-generator-workflow
description: Sz-Admin 代码生成器工作流，用于基于已有数据库表或用户提供的 CREATE TABLE DDL 生成、检查、清理和回滚 CRUD/module 代码。适用于导入表、配置生成选项、预览、磁盘检查、生成或下载代码、检查生成产物、清理生成表/模块、局部还原宿主文件插入片段、修改 sz-module-generator、生成模板、生成器前端页面、后端/前端模块目标、API prefix、菜单/字典/角色脚本导出和生成器文档等场景。
---

# Sz 代码生成器工作流

## 核心规则

在任何分析或修改前，先加载 Sz 知识库，并且必须读取到内容。读取顺序：

1. 当前后端仓库或当前工作项目的 `docs/project-knowledge-conventions.md`。
2. 使用随 `sz-boot-parent` 发布的 skill 时，以该仓库根目录 `docs/project-knowledge-conventions.md` 为项目事实。
3. 项目知识库不存在时，才读取当前用户 Codex Home 下的 `docs/project-knowledge-conventions.md`，例如 Windows 的 `%USERPROFILE%\.codex\docs\project-knowledge-conventions.md` 或类 Unix 系统的 `$HOME/.codex/docs/project-knowledge-conventions.md`。

在输出结论或执行代码工作前，先输出 `[sz 知识库状态]` 行。项目知识库与全局回退知识库冲突时，以项目知识库为准。

把代码生成器视为跨边界功能处理。后端生成器逻辑、前端生成器页面、生成代码产物、Liquibase/SQL 脚本输出和文档影响要一起检查。

## 代码地图

需要当前路径、类、模板或验证命令时，读取 `references/current-code-map.md`。

## 工作流程

1. 先判断任务类型：
   - 实际代码生成：使用已有表或用户提供的 `CREATE TABLE` DDL，导入表，配置生成选项，预览/检查，生成代码，下载 zip，或验证生成产物。
   - 生成器后端行为：导入、详情、保存、预览、检查、生成、zip。
   - 生成目标模型：已有/新增后端模块、API prefix、前端模块、前端布局。
   - 模板输出：Java、Vue、TypeScript、SQL、Liquibase XML。
   - 生成器前端 UI：导入弹窗、编辑三步流程、字段详情面板、预览树。
   - 生成产物质量：CRUD 分层、权限、Excel、字典、上传/资源字段、数据权限。
   - 生成产物清理/回滚：删除某张生成表、删除生成文件、局部还原宿主文件挂载片段、清理误生成模块。
   - 文档同步：VitePress、README、变更说明、升级说明、截图。

2. 实际生成代码前先做预检：
   - 确认目标表、目标后端模块、目标前端模块/布局，以及生成范围是全栈、仅后端还是仅数据库。
   - 如果用户提供 `CREATE TABLE` SQL，先解析表名、表注释、主键、字段注释、疑似字典字段、上传/图片/富文本字段、逻辑删除字段、自动填充字段和数据权限字段，用于生成配置复核。
   - 不要把原始 DDL 文本当成现有生成器流程的替代品。除非当前生成器代码明确支持 DDL 文本导入，否则优先把表创建/导入到本地或开发数据库，再走生成器的导入、详情、编辑、预览、检查、生成流程。
   - 执行 DDL 前，必须确认目标数据库连接/profile、数据库方言，以及是否允许在该环境创建表。
   - 确认是否允许直接写入 `sz-boot-parent` / `sz-admin`，还是只允许 zip/预览。
   - 检查导入后的表配置：类名、模块名、业务名、包名、路径、API prefix 模块/前缀、权限、Excel 导入导出、字典、上传/资源字段、数据权限字段。
   - DDL 驱动生成时，先给出简洁计划：建表/导入、复核推断配置、预览、磁盘检查、生成、检查产物、验证前后端。
   - 可用时先预览和磁盘检查，再生成。
   - 生成后检查变更文件，并运行最小有效的后端/前端验证。

3. 修改生成器内部前先定位：
   - 后端：`sz-module/sz-module-generator`。
   - 前端：`src/modules/toolbox`。
   - 如果问题涉及输出形态，参考现有生成样例，例如 smart/audit 模块。
   - 关联服务挂载：API prefix、模块 POM、服务 POM、服务 changelog、`application.yml`。

4. 保持现有架构：
   - 生成器代码放在 `sz-module-generator`，不要放到 `sz-module-admin`。
   - 生成的 Admin/system 实现模式要符合 Sz 知识库。
   - 前端生成模块默认放在 `src/modules/<module>`，除非用户明确要求旧布局。
   - `admin`、`audit`、`generator` 内置 API 模块使用内置 client；其他模块使用动态 `createModuleHttp` 输出。
   - 未经用户确认，不新增依赖。

5. 外科手术式修改：
   - 后端行为改 service/controller/model/builder 和相关测试。
   - 生成文件形态改 FreeMarker 模板和喂给模板的代码模型。
   - UI 行为改对应的生成器 Vue 组件或类型文件。
   - 涉及 Liquibase 或 SQL 迁移兼容时，配合 `sz-liquibase-db-compat` 使用。

6. 清理生成代码前先分类：
   - 直接表级产物：Java package、Mapper XML、Liquibase unreleased changeSet、前端 api/types/views、菜单或 SQL 导出文件、同表名/类名/业务名生成文档。匹配确认后可删除。
   - 宿主文件插入片段：根/模块/服务 POM、服务 changelog include、`application.yml` API-prefix 模块块、前端模块注册项、changelog-master include。只删除生成片段，不整文件回退。
   - 已有模块清理：如果后端或前端模块原本存在，保留模块 POM、配置类、register、模块目录，只删除生成表文件和 register/changelog 中的生成项。
   - 新增模块清理：如果模块本身是新生成、未跟踪，或清完表后没有非生成文件，则可删除后端模块目录、前端模块目录及 `register.ts`、模块 POM/配置类、根/模块/服务 POM 挂载、API-prefix 块、服务/模块 changelog 挂载。
   - 空目录：只删除生成范围内的空目录。不要删除仍含无关文件的父级 docs 或模块目录。

7. 用证据判断模块是新增还是已有：
   - Git 状态：未跟踪的模块目录通常表示新生成模块；已跟踪文件或无关既有内容表示已有模块。
   - 目录内容：如果 `src/modules/<module>` 只包含生成的 `api`、`types`、`views` 和 `register.ts`，新增模块清理时可以删除。
   - 宿主挂载：`<module>sz-module-xxx</module>`、`sz-module-xxx` 依赖、`module-xxx-changelog.xml` include、`sz.api-prefix.modules.xxx` 块，只在确认是生成模块引入时移除。
   - 用户范围：如果用户只要求清理已有模块中的一张表，保留模块壳，除非用户明确要求连模块一起删除。

8. 按风险验证：
   - 后端生成器逻辑：有相关测试则运行；否则 compile `sz-service/sz-service-admin`。
   - 模板/模型变更：检查生成预览路径和预期模型值；相关测试存在时运行。
   - 前端生成器 UI：运行 `pnpm type-check`；布局/交互变更要打开页面检查。
   - 生成 CRUD 产物：检查后端包路径、Mapper XML 路径、前端模块注册、API client、权限、菜单/脚本输出。
   - 生成产物清理：用 `rg` 搜表名、类名、kebab 名、camel 名、模块 artifact、changelog include、API prefix、前端模块路径；再分别检查两个仓库的 `git status --short`。
   - 文档同步：确认真实代码行为后再更新文档。

9. 汇报时说明：
   - 改了哪条代码路径或模板。
   - 预期生成产物会如何变化。
   - 运行了哪些验证。
   - 未验证路径，尤其是生成代码运行效果或浏览器 UI 检查。

## 硬性停止点

先询问用户再执行：

- 目标范围不清时，运行会大量写入 live repo 的实际生成。
- 无法判断模块是否原本存在时，删除整个生成模块。
- 超出用户请求修改数据库 schema 或迁移策略。
- 安装生成模块、切换 Git 分支、提交、推送、修改生产环境文件。

不要：

- 当前代码可查时，凭旧文档推断生成器行为。
- 只改已经生成的样例来修复输出，而不改真正的模板/模型源头。
- 不说明耦合关系就大范围同时修改生成器行为和生成模板。
- 清理生成代码时使用整文件 Git revert，而实际只需要删除一个生成块。
- 仅因为删除了一张生成表，就删除模块壳、`register.ts`、docs 目录、changelog 目录或父级模块目录。
