# 当前 Liquibase 地图

## 入口文件

- Service master：`sz-service/sz-service-admin/src/main/resources/db/changelog/changelog-master.xml`
- Admin 模块：`sz-module/sz-module-admin/src/main/resources/db/changelog/module-admin-changelog.xml`
- Generator 模块：`sz-module/sz-module-generator/src/main/resources/db/changelog/module-generator-changelog.xml`
- Audit 模块：`sz-module/sz-module-audit/src/main/resources/db/changelog/module-audit-changelog.xml`
- Smart 模块示例：`sz-module/sz-module-smart/src/main/resources/db/changelog/module-smart-changelog.xml`

service master 当前通过 `errorIfMissing="false"` include 可选模块 changelog，例如 audit、generator、smart。

## Admin 结构

- `framework/changelog-master.xml`
- `framework/2.0.0/001_framework_init.xml`
- `framework/2.0.1/001_sys_change.xml`
- `demo/changelog-master.xml`
- `demo/2.0.0/001_demo_init.xml`

框架核心文件不适合放用户特定自定义。自定义新增内容优先放到模块或业务 changelog 文件。

## Generator 结构

- `module-generator-changelog.xml`
- `generator/2.0.0/001_generator_init.xml`
- `generator/2.0.0/tables/001_generator_table.xml`
- `generator/2.0.0/tables/002_generator_table_column.xml`
- `generator/2.0.1/001_generator_module_target_fields.xml`

生成器脚本输出模板：

- `sz-module-generator/src/main/resources/templates/liquibase/menuInit.xml.ftl`
- `sz-module-generator/src/main/resources/templates/liquibase/menuImport.xml.ftl`
- `sz-module-generator/src/main/resources/templates/liquibase/dictImport.xml.ftl`
- `sz-module-generator/src/main/resources/templates/sql/menuInit.sql.ftl`
- `sz-module-generator/src/main/resources/templates/sql/menuImport.sql.ftl`
- `sz-module-generator/src/main/resources/templates/sql/dictImport.sql.ftl`

Admin 脚本模板：

- `sz-module-admin/src/main/resources/templates/admin-script/liquibase/menuImport.xml.ftl`
- `sz-module-admin/src/main/resources/templates/admin-script/liquibase/dictImport.xml.ftl`
- `sz-module-admin/src/main/resources/templates/admin-script/liquibase/roleMenuImport.xml.ftl`

## 当前兼容模式

- framework/audit changelog 文件中已有常见类型 property：
  - `datetime.type`：MySQL 使用 `DATETIME`，PostgreSQL 使用 `TIMESTAMP`。
  - `bool.type`：MySQL 使用 `CHAR(1)`，PostgreSQL 使用 `VARCHAR(1)`。
- 新 changeSet 通常使用 `preConditions onFail="MARK_RAN"`。
- include 通常是显式 include；模块自有文件中常用 `relativeToChangelogFile="true"`。
- `dept_scope` 对方言敏感：
  - MySQL 数据权限方言使用 JSON 相关行为。
  - PostgreSQL 数据权限方言使用数组交集行为。
  - 该字段的索引或 cast 经常需要按 `dbms` 拆分 changeSet。
- 逻辑删除值是项目约定：`F` 表示有效，`T` 表示删除。

## 审查清单

- 确认目标模块拥有该数据或表。
- 确认 include 路径能从 service master 到 module，再到 version file，最终到达新增文件。
- 在受影响 changelog 树中搜索重复 changeSet ID。
- 检查每个 raw SQL 块是否包含数据库特定语法。
- 检查所有插入 ID、权限编码、字典类型、配置 key、菜单组件是否与前后端代码一致。
- 检查 Java PO 字段和 TypeHandler 是否匹配 JSON/list/array 字段。
- 如果涉及代码生成器模板，检查生成的 SQL/XML 输出。

## 验证命令

相关修改后按需使用：

```powershell
$env:JAVA_HOME='<your-jdk-21-path>'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn -pl sz-service/sz-service-admin -am compile -DskipTests
```

Windows 下检查 XML 时使用 UTF-8 读取：

```powershell
Get-Content -LiteralPath <path> -Encoding UTF8
```
