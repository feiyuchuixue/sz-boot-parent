---
name: sz-liquibase-db-compat
description: Sz-Admin Liquibase 与数据库兼容工作流，用于设计、审查、修复和验证 MySQL/PostgreSQL 双数据库迁移。适用于 db/changelog XML、changeSet 结构、模块 changelog 挂载、MySQL/PostgreSQL 类型差异、初始化数据、升级脚本、代码生成器脚本输出、角色/菜单/字典/配置迁移和数据库兼容问题。
---

# Sz Liquibase 数据库兼容工作流

## 核心规则

在任何 Liquibase 或数据库结论前，先加载 Sz 知识库，并且必须读取到内容。读取顺序：

1. 当前后端仓库或当前工作项目的 `docs/project-knowledge-conventions.md`。
2. 使用随 `sz-boot-parent` 发布的 skill 时，以该仓库根目录 `docs/project-knowledge-conventions.md` 为项目事实。
3. 项目知识库不存在时，才读取当前用户 Codex Home 下的 `docs/project-knowledge-conventions.md`，例如 Windows 的 `%USERPROFILE%\.codex\docs\project-knowledge-conventions.md` 或类 Unix 系统的 `$HOME/.codex/docs/project-knowledge-conventions.md`。

在分析或编辑前输出 `[sz 知识库状态]` 行。项目知识库与全局回退知识库冲突时，以项目知识库为准。

不要把 MySQL 行为当作 PostgreSQL 兼容的证明，也不要反过来把 PostgreSQL 行为当作 MySQL 兼容的证明。

## 代码地图

需要当前 changelog 入口、模块布局、已知类型模式和验证命令时，读取 `references/current-liquibase-map.md`。

## 工作流程

1. 先分类数据库任务：
   - 新表或新模块 changelog。
   - 已有表结构变更。
   - 初始化数据或 demo 数据。
   - 菜单、字典、角色菜单、配置或自定义业务数据的升级脚本。
   - 代码生成器产出的 Liquibase/XML/SQL。
   - 写入数据库字段的代码兼容检查。

2. 定位正确的 changelog 边界：
   - service master 入口用于运行时组合。
   - module changelog 用于模块归属。
   - 版本 init 文件用于有序 include。
   - 表级文件承载实际结构或数据变更。
   - 尽量把框架核心变更和用户/业务自定义变更分开。

3. 按幂等性设计：
   - 使用显式 `<include file="...">`，避免宽泛 `includeAll`。
   - 新增可重复安全变更时添加 `preConditions onFail="MARK_RAN"`。
   - 发布后的 `changeSet id` 使用稳定、可描述的值。
   - 插入数据使用稳定 ID，并在目标数据可能已存在时加重复保护。
   - 升级脚本要显式设计跳过/回滚行为，不依赖人工清理。

4. 按数据库兼容设计：
   - 优先使用 Liquibase 结构化标签，避免不必要的 raw SQL。
   - 常见类型差异使用 dbms 专属 `<property>`，例如 bool、datetime。
   - 无法兼容表达的 SQL、索引、JSON/array 类型或 cast，使用 `dbms="mysql"` / `dbms="postgresql"` 拆分 changeSet。
   - 字段 Java 类型、TypeHandler、SQL 类型、demo 数据格式和数据权限方言要一起验证。
   - `dept_scope`、JSON 字段、数组/list 字段、索引、逻辑删除写入属于高风险点。

5. 检查应用耦合：
   - PO 字段、DTO/VO 字段、Mapper XML、TypeHandler、监听器和 service 逻辑。
   - `DB_TYPE` profile 和 service POM 中的数据库模块选择。
   - 数据权限配置和方言行为。
   - 如果迁移由代码生成器产出，同时检查生成器模板。

6. 验证：
   - 读取并检查 XML 结构和 include 路径。
   - 在受影响模块内搜索重复 changeSet ID。
   - Java 代码或 TypeHandler 耦合变化时 compile 受影响 service。
   - SQL 较重的变更要分别推理 MySQL 和 PostgreSQL，并说明未实际运行的数据库验证。
   - 只有两种方言路径都检查过，才能称为兼容。

7. 汇报：
   - 入口文件和受影响模块。
   - 新增或修改的 changeSet ID。
   - MySQL/PostgreSQL 分别如何处理。
   - 幂等策略。
   - 已运行验证和剩余风险。

## 常用模式

常见标量类型差异使用这个紧凑模式：

```xml
<property name="datetime.type" value="DATETIME" dbms="mysql"/>
<property name="datetime.type" value="TIMESTAMP" dbms="postgresql"/>
<property name="bool.type" value="CHAR(1)" dbms="mysql"/>
<property name="bool.type" value="VARCHAR(1)" dbms="postgresql"/>
```

当结构化标签无法安全表达两种方言时，拆分 changeSet：

```xml
<changeSet id="example-mysql-202606020001" author="sz" dbms="mysql">
    <preConditions onFail="MARK_RAN">
        <not><indexExists tableName="example_table" indexName="idx_example"/></not>
    </preConditions>
    <sql>...</sql>
</changeSet>

<changeSet id="example-postgresql-202606020001" author="sz" dbms="postgresql">
    <preConditions onFail="MARK_RAN">
        <not><indexExists tableName="example_table" indexName="idx_example"/></not>
    </preConditions>
    <sql>...</sql>
</changeSet>
```

## 硬性停止点

先询问用户再执行：

- 对 live database 运行迁移。
- 写入破坏性 SQL，例如 `DROP`、`TRUNCATE`、无范围的 `DELETE`/`UPDATE`。
- 修改已发布的框架 changeSet ID。
- 修改生产环境文件或凭据。

不要：

- 为用户特定定制修改 `framework/**`，除非请求明确指向框架演进。
- Liquibase 结构化标签足够时仍使用 raw SQL。
- 仅凭 XML 检查就声称运行时迁移成功。
- 未确认范围就禁用或删除 demo/generator/audit 模块 include。
