# Sz Codex Skills 使用说明

## 一、背景说明

本目录收录 Sz-Admin / `sz-boot-parent` 项目配套的 Codex skills。它们用于把项目内已经沉淀稳定的开发流程交给 Codex 复用，减少重复解释项目结构、代码生成器流程、Liquibase 兼容规则和生成产物清理边界。

这些 skill 面向 Codex 执行，不是运行时代码，不参与 Maven 构建，也不改变项目启动方式。团队成员可以把 `skills/` 下的 skill 复制到自己的 Codex skill 目录后使用。

本仓库同时发布项目知识库：

```text
docs/project-knowledge-conventions.md
```

两个 skill 都会在执行前优先读取该知识库。它是当前 `sz-boot-parent` / `sz-admin` 分支的事实源，包含后端模块结构、前端模块结构、Liquibase、数据库兼容、代码生成器、字典、Excel、OSS、数据权限等约定。当前项目不存在知识库时，才读取当前用户 Codex Home 下的全局回退知识库，例如 Windows 的 `%USERPROFILE%\.codex\docs\project-knowledge-conventions.md` 或类 Unix 系统的 `$HOME/.codex/docs/project-knowledge-conventions.md`；全局回退知识库不能覆盖项目内知识库。

## 二、目标定义

目标是让 Codex 在处理 Sz-Admin 项目任务时具备稳定的项目级工作流：

- 使用代码生成器生成、检查、清理 CRUD/module 代码。
- 分析和修改 `sz-module-generator`、生成模板、前端生成器页面。
- 设计、审查、修复 MySQL / PostgreSQL 兼容的 Liquibase 迁移。
- 在清理生成代码时区分“新增模块”和“已有模块”，避免误删已有模块壳。

成功标准：

- Codex 能根据用户请求自动触发对应 skill。
- Codex 能加载 skill 内的参考路径和校验命令。
- 清理、迁移、生成器改造任务都有明确边界、验证方式和停止条件。

## 三、包含的 Skill

### 1. sz-code-generator-workflow

用途：

- 使用现有代码生成器导入表、配置生成选项、预览、检查、生成或下载代码。
- 分析和修改 `sz-module-generator` 后端逻辑。
- 修改生成器前端 UI，例如导入弹窗、编辑三步流程、字段配置、预览树。
- 修改 FreeMarker 模板、代码模型、API prefix、前后端模块目标。
- 检查生成后的 CRUD 代码质量。
- 清理错误生成的表代码或模块代码。

适合场景：

- “使用代码生成器为 `sys_demo_order` 生成 CRUD。”
- “检查生成器为什么前端 API prefix 不对。”
- “修改生成器模板，让导出的 Vue 页面增加某个字段行为。”
- “清理 `test_gen_smart_order` 表相关生成代码。”
- “我生成了一个新模块，现在想完整撤回这个模块。”

清理规则重点：

- 已有模块：只删除表级产物和注册项，保留模块目录、POM、配置类、`register.ts`。
- 新增模块：如果模块目录是新生成且没有非生成内容，可以删除模块目录、POM 挂载、服务依赖、API prefix、changelog include、前端模块目录和 `register.ts`。
- 宿主文件只做片段级还原，不使用整文件 Git revert。

### 2. sz-liquibase-db-compat

用途：

- 设计、审查、修复 Liquibase XML。
- 检查 MySQL / PostgreSQL 类型兼容。
- 处理 module changelog、service master changelog、初始化数据、升级脚本。
- 审查菜单、角色、字典、配置、demo 数据迁移。
- 检查代码生成器输出的 Liquibase / SQL 脚本是否兼容双数据库。

适合场景：

- “检查这个 changelog 是否兼容 MySQL 和 PostgreSQL。”
- “帮我给某个表新增字段，并写兼容迁移。”
- “菜单 SQL 需要迁移到 Liquibase XML。”
- “这个 changeSet 在 PostgreSQL 下失败，帮我分析。”
- “代码生成器导出的 Liquibase 脚本需要支持双数据库。”

迁移规则重点：

- 优先使用 Liquibase 结构化标签，避免不必要的 raw SQL。
- MySQL / PostgreSQL 行为不能互相证明兼容。
- 对类型、索引、JSON、数组、`dept_scope` 等高风险点进行方言拆分或属性映射。
- 已发布的 changeSet ID 不随意修改。
- 运行真实数据库迁移、破坏性 SQL、生产配置修改前必须确认。

## 四、安装方式

将本目录下的 skill 复制到 Codex skills 目录：

```powershell
Copy-Item -Recurse -Force .\skills\sz-code-generator-workflow "$env:USERPROFILE\.codex\skills\sz-code-generator-workflow"
Copy-Item -Recurse -Force .\skills\sz-liquibase-db-compat "$env:USERPROFILE\.codex\skills\sz-liquibase-db-compat"
```

安装后重新打开或刷新 Codex 会话，使 skill 列表重新加载。

如果只复制 skill 到其他仓库，必须同时确认目标仓库已有自己的 `docs/project-knowledge-conventions.md`，或把本仓库知识库复制过去并按目标代码修正。不要让面向 `sz-boot-parent` 当前分支的全局知识库误当成其他项目事实。

## 五、使用方式

可以直接在 Codex 对话中点名 skill：

```text
使用 $sz-code-generator-workflow 帮我清理 test_gen_smart_order 表相关代码
```

```text
使用 $sz-code-generator-workflow 检查代码生成器预览结果为什么缺少前端 register.ts
```

```text
我有一张表：CREATE TABLE `test_gen_smart_order` (...);
请使用 $sz-code-generator-workflow 在 smart 模块生成完整 CRUD 代码。
请先判断是否需要落库导入；如果需要，先说明目标数据库和执行步骤，确认后再生成。
生成后请检查后端、前端、Liquibase、菜单权限脚本，并运行必要验证。
```

```text
使用 $sz-liquibase-db-compat 检查 module-admin-changelog.xml 新增 changeSet 是否兼容 MySQL 和 PostgreSQL
```

也可以描述任务，让 Codex 根据 skill 描述自动触发：

```text
我刚生成了 smart 模块，现在想撤回所有生成产物，但保留我已有的 PostgreSQL 配置
```

```text
这个 Liquibase 脚本在 PostgreSQL 下执行报错，帮我定位并修复
```

### CREATE TABLE SQL 生成 CRUD 的推荐话术

当只有建表 SQL，还没有在数据库中导入该表时，建议这样描述：

```text
使用 $sz-code-generator-workflow 处理下面这张表。
目标：在 smart 模块生成完整 CRUD。
表 SQL 如下：

CREATE TABLE `test_gen_smart_order` (
  ...
) COMMENT='代码生成器智能推断测试订单表';

请先解析字段语义并给出生成配置建议。
如果当前生成器需要从数据库导入表元数据，请先说明需要把表创建到哪个本地/开发数据库，并在我确认后执行。
生成前请执行预览和磁盘检查；生成后请检查后端、前端、Liquibase、菜单权限脚本，并运行后端 compile 和前端 type-check。
```

Codex 使用该 skill 时应按以下步骤处理：

- 从 SQL 中提取表名、表注释、主键、字段名、字段类型和字段注释。
- 推断疑似字典字段、开关字段、金额/小数、日期时间、上传图片、富文本、JSON、自动填充、逻辑删除和数据权限字段。
- 确认目标后端模块、前端模块、包名、业务名、生成模式和是否允许直接写入项目。
- 如果生成器不支持直接从 DDL 文本导入，则先把表创建到本地/开发数据库，再走“导入表 -> 编辑配置 -> 预览 -> 检查 -> 生成”的现有生成器流程。
- 涉及 Liquibase 或跨 MySQL/PostgreSQL 脚本时，同时使用 `sz-liquibase-db-compat` 检查兼容性。

## 六、推荐验收方式

代码生成器相关任务完成后，建议 Codex 汇报：

- 修改或清理了哪些后端路径。
- 修改或清理了哪些前端路径。
- 是否涉及新增模块还是已有模块。
- 是否清理了 POM、API prefix、changelog include、前端 `register.ts`。
- 残留搜索结果。
- `git status --short`。
- 后端 Maven compile 和前端 type-check 结果。

Liquibase 相关任务完成后，建议 Codex 汇报：

- 入口 changelog 和模块归属。
- 新增或修改的 changeSet ID。
- MySQL / PostgreSQL 分别如何处理。
- 幂等策略和 preConditions。
- 是否存在 raw SQL、类型差异、索引差异或数据初始化风险。
- 已运行和未运行的验证。

## 七、注意事项

- Skill 是 Codex 的执行说明，不是业务代码。
- 不要把团队说明文档放进单个 skill 目录内，避免污染 skill 的上下文设计。
- 修改 skill 后，应同步更新本目录的发布副本和个人 Codex skill 目录。
- 涉及数据库真实迁移、破坏性 SQL、生产配置或 Git 写操作时，仍需按项目规则确认。
- 清理生成代码时必须先判断模块来源：新增模块可以整体清理，已有模块只能清理表级产物。
