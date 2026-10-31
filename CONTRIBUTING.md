# 贡献指南

感谢你对 Sz-Admin 的关注！欢迎以各种方式参与项目建设，无论是报告问题、提出建议、改进文档还是提交代码。

## 贡献方式

### 报告 Bug

如果你在使用过程中发现了问题，请通过 [Issue](https://github.com/feiyuchuixue/sz-boot-parent/issues) 反馈。提交时请尽量包含：

- 清晰的问题描述
- 复现步骤
- 预期行为与实际行为
- 运行环境（JDK 版本、数据库类型与版本、操作系统等）
- 相关日志或截图

### 提出功能建议

欢迎对新功能或改进方向提出建议。提交 Issue 时请说明：

- 功能的使用场景
- 期望的行为
- 该功能对项目的价值

### 改进文档

文档改进是非常有价值的贡献方式，包括但不限于：

- 修正错别字或表述不清的地方
- 补充缺失的使用说明
- 完善示例代码
- 更新过时的内容

### 提交代码 PR

如果你希望直接贡献代码，请遵循以下流程。

## 开发环境

### 环境要求

| 环境 | 要求 |
| --- | --- |
| JDK | 25 |
| Maven | 3.8+，推荐 3.9.x |
| 数据库 | MySQL 8.0.17+ 或 PostgreSQL 16+ |
| Redis | 7.x |
| Node.js | >= 20.19.0 |
| pnpm | 10.17.1 |

### 后端启动

```shell
git clone https://github.com/feiyuchuixue/sz-boot-parent.git
cd sz-boot-parent
```

1. 创建空数据库
2. 修改 `config/local/mysql.yml` 或 `config/local/postgresql.yml` 中的数据库连接
3. 修改 `config/local/redis.yml` 中的 Redis 连接
4. 启动 `sz-service/sz-service-admin` 模块中的 `com.sz.AdminApplication`

### 前端启动

```shell
git clone https://github.com/feiyuchuixue/sz-admin.git
cd sz-admin
corepack enable
corepack prepare pnpm@10.17.1 --activate
pnpm install
pnpm dev
```

## PR 提交流程

1. **Fork 仓库**：将本仓库 Fork 到你的 GitHub 账号下
2. **创建分支**：从 `main` 分支创建一个描述性的分支，例如 `feature/xxx`、`fix/xxx`、`docs/xxx`
3. **提交修改**：在你的分支上进行修改，确保每次提交聚焦于一个明确的改动
4. **本地验证**：确保代码可以正常编译和运行，相关测试通过
5. **提交 PR**：向 `feiyuchuixue/sz-boot-parent` 的 `main` 分支提交 Pull Request
6. **代码评审**：等待维护者评审，根据反馈进行修改

## 代码规范

- 遵循项目现有的代码风格和目录结构
- 后端代码遵循 Java 命名规范，使用有意义的变量和方法名
- 新增业务模块推荐放在独立的 `sz-module-*` 中，避免直接修改官方核心模块
- 前端代码遵循 TypeScript 和 Vue 3 的最佳实践
- 提交信息使用清晰的描述，建议格式：`类型: 简要描述`，例如 `fix: 修复用户列表分页异常`

## 注意事项

- 提交 PR 前请先同步上游仓库的最新代码，减少合并冲突
- 大型改动建议先通过 Issue 讨论，确认方向后再动手
- 请勿在 PR 中包含与本次改动无关的修改
- 数据库变更需要同步提供 Liquibase changelog

## 社区

- 官方文档：https://szadmin.cn
- 在线预览：https://preview.szadmin.cn
- 邮箱：feiyuchuixue@163.com

再次感谢你的贡献！
