# sz-common-resource 使用手册

> 本手册面向 **业务开发者** 与 **框架扩展开发者** 两类读者，分两部分组织：
> - **第一部分 · 基础使用**：30 分钟快速上手，覆盖配置、API、典型案例、旧代码迁移、排障
> - **第二部分 · 进阶原理**：架构分层、安全模型细节、SPI 扩展点
>
> §2 快速开始采用 `sz-service-admin` 的真实场景 `admin.user.logo`（用户头像）作为示例；其他章节的案例集、API 参考等部分仍使用虚构场景 `demo.avatar` / `demo.document` / `demo.archive` 作为通用说明。

---

## 目录

**第一部分 · 基础使用**

- [1. 模块定位](#1-模块定位)
- [2. 快速开始](#2-快速开始)
- [3. 核心概念](#3-核心概念)
- [4. 配置参考](#4-配置参考)
- [5. 使用案例集](#5-使用案例集)
- [6. ResourceService API 参考](#6-resourceservice-api-参考)
- [6.7 @OssUrlFill 注解（VO 自动回填 URL）](#67-ossurlfill-注解vo-自动回填-url)
- [7. 从 OssClient / UploadResult 迁移](#7-从-ossclient--uploadresult-迁移)
- [8. 常见问题排查](#8-常见问题排查)

**第二部分 · 进阶原理**

- [9. 架构原理](#9-架构原理)
- [10. 安全模型（深入）](#10-安全模型深入)
- [11. SPI 扩展](#11-spi-扩展)
- [12. 相关文件索引](#12-相关文件索引)

**附录**

- [附录 A · ResourceService API 速查表](#附录-a--resourceservice-api-速查表)
- [附录 B · yml 示例](#附录-b--yml-示例)
- [附录 C · 与 sz-common-oss 的配合](#附录-c--与-sz-common-oss-的配合)

---

# 第一部分 · 基础使用

## 1. 模块定位

### 1.1 模块解决什么问题

`sz-common-resource` 是 **统一资源管理模块**，为业务层提供一套与存储介质无关的文件上传、读取、URL 生成接口。

- 业务代码只调用 `ResourceService`，不再直接依赖 `OssClient` 或文件系统 API。
- 存储位置（本地磁盘 / OSS）、访问方式（明文 URL / 代理 token / 签名 URL）、路径规则、命名规则、安全白名单，全部通过 **yml 配置（scene）** 描述，代码零改动即可切换。

### 1.2 模块不做什么

| 不负责的事 | 由谁负责 |
|------------|---------|
| OSS 厂商接入与 bucket 生命周期管理 | `sz-common-oss` |
| TOKEN 模式的实际 URL 签发与代理转发 | 上层业务（本模块返回 `null`，由上层 Controller/Service 根据业务策略生成） |
| 资源元数据落库（如 `sys_resource` 表） | 上层业务（本模块只返回 `ResourceUploadResult`，不写库） |
| 资源鉴权、RBAC、限流 | 上层业务 |

### 1.3 与 sz-common-oss 的关系

- `sz-common-oss`：厂商适配层，封装 S3 / MinIO / 阿里云 OSS 等底层 SDK。
- `sz-common-resource`：业务抽象层，定义 **场景（scene）** 概念，按场景组装存储/命名/访问策略。
- 依赖方向：`resource → oss`（单向）。未引入 `sz-common-oss` 时，本模块仍可单机 LOCAL 模式工作。

---

## 2. 快速开始

以 **用户头像（`admin.user.logo`）** 为例，演示从配置到上传到查询回填的完整链路。

> 本节示例均取自 `sz-service-admin` 的真实代码，可直接对照查阅。

### Step 1 引入依赖

```xml
<dependency>
    <groupId>com.sz.resource</groupId>
    <artifactId>sz-common-resource</artifactId>
</dependency>
```

> 仅使用 LOCAL 存储时，**无需** 引入 `sz-common-oss`。OSS 模式需同时引入 `sz-common-oss`。

### Step 2 yml 定义场景

三种常见部署方式如下，按需取消注释其中一种，其余保持注释状态。

```yaml
sz:
  resource:
    root: ./data          # LOCAL 模式的本地存储根目录
    scenes:

      # ========= 方式一：本地磁盘 + Java 服务代理（开发/内网，当前激活） =========
      - code: admin.user.logo
        type: LOCAL
        serve-mode: DIRECT
        path: logo                                                   # 相对于 root 的子目录
        base-url: http://127.0.0.1:19991/api/admin/resource/file/logo  # Java 代理访问地址
        naming: ORIGINAL                                             # 保留原始文件名
        path-strategy: BIZ_DATE                                      # 路径结构：{userId}/{yyyyMMdd}/
        exts: [svg, png, jpg, jpeg, webp, gif]
        max-size: 3                                                  # MB

      # ========= 方式二：OSS 私有桶 + 预签名 URL（生产推荐） =========
      # - code: admin.user.logo
      #   type: OSS
      #   serve-mode: PRESIGNED          # 访问时生成临时签名 URL，私有桶安全访问
      #   bucket: client-logos
      #   base-url: http://192.168.100.176:9000/client-logos
      #   naming: ORIGINAL
      #   path-strategy: BIZ_DATE
      #   exts: [svg, png, jpg, jpeg, webp, gif]
      #   max-size: 3

      # ========= 方式三：OSS 公有桶 + 直链（CDN / 公开资源） =========
      # - code: admin.user.logo
      #   type: OSS
      #   serve-mode: DIRECT             # 公有桶，直接返回 base-url + objectKey 拼接的明文链接
      #   bucket: client-logos-public
      #   base-url: http://192.168.100.176:9000/client-logos-public
      #   naming: UUID                   # 公有桶建议 UUID 命名，避免原始文件名泄露
      #   path-strategy: BIZ_DATE
      #   exts: [svg, png, jpg, jpeg, webp, gif]
      #   max-size: 3
```

**三种方式对比：**

| | 方式一：LOCAL DIRECT | 方式二：OSS PRESIGNED | 方式三：OSS DIRECT |
|---|---|---|---|
| 存储位置 | 本地磁盘 | OSS 私有桶 | OSS 公有桶 |
| accessUrl 形式 | `base-url/objectKey` 明文 | 带 `X-Amz-Signature` 的临时链接 | `base-url/objectKey` 明文 |
| 文件访问 | Java 服务代理流式转发 | OSS 直接下发，Java 不参与 | OSS 直接下发，Java 不参与 |
| 适用场景 | 开发、内网、无 OSS 环境 | 生产、需要访问控制 | 公开静态资源、CDN |
| 依赖 | 无额外依赖 | `sz-common-oss` + MinIO/S3 | `sz-common-oss` + MinIO/S3 |

### Step 3 调用上传接口

`sz-service-admin` 提供了统一的文件上传入口，场景由前端传入 `sceneCode` 决定：

```
POST /resource/upload
Content-Type: multipart/form-data

sceneCode    = admin.user.logo         （必填，对应 yml 中的 scene code）
pathSegments = {userId}                （可选，BIZ_DATE 策略的业务路径分段，多段逗号分隔）
bizKey       =                         （可选，BIZ_KEY 命名规则时必填；ORIGINAL/UUID 可不传）
file         = <头像文件>
```

对应的 Controller 实现（`SysResourceController.java:45`）：

```java
@DebounceIgnore
@PostMapping(value = "/upload", consumes = "multipart/form-data")
public ApiResult<ResourceUploadResult> upload(
        @RequestParam("sceneCode") String sceneCode,
        @RequestParam(value = "bizKey", required = false) String namingKey,
        @RequestParam(value = "pathSegments", required = false) String pathSegments,
        @RequestPart("file") MultipartFile file) throws IOException {
    String[] segments = (pathSegments != null && !pathSegments.isBlank())
            ? pathSegments.split(",") : new String[0];
    ResourceUploadResult result = sysResourceService.upload(sceneCode, namingKey, file, segments);
    return ApiResult.success(result);
}
```

`SysResourceServiceImpl` 在委托底层 `ResourceService` 完成存储后，还会写入 `sys_resource` 审计记录并将 `resourceId` 回填到结果（`SysResourceServiceImpl.java:57-76`）。

### Step 4 前端使用 accessUrl

上传成功后返回 `ResourceUploadResult`，前端将 `accessUrl` 用于即时预览，将 `objectKey` 存入业务表（如 `sys_user.logo` 字段）。

**方式一（LOCAL DIRECT）返回示例：**

```json
{
  "objectKey":   "logo/1001/20260424/avatar.png",
  "originName":  "avatar.png",
  "size":        98304,
  "contentType": "image/png",
  "accessUrl":   "http://127.0.0.1:19991/api/admin/resource/file/logo/1001/20260424/avatar.png",
  "resourceId":  10086
}
```

**方式二（OSS PRESIGNED）返回示例：**

```json
{
  "objectKey":   "logo/1001/20260424/avatar.png",
  "originName":  "avatar.png",
  "size":        98304,
  "contentType": "image/png",
  "accessUrl":   "http://192.168.100.176:9000/client-logos/logo/1001/20260424/avatar.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Expires=3600&X-Amz-Signature=...",
  "resourceId":  10086
}
```

> `accessUrl` 仅用于上传后即时预览，不要长期存储。入库值始终存 `objectKey`，查询时由框架自动回填 URL（见 Step 5）。

### Step 5 查询时自动回填 URL

数据库存的是 `objectKey`，查询接口返回 VO 时需要把它转为完整访问 URL。  
在 VO 字段上标注 `@OssUrlFill`，框架在 Controller 返回前自动完成替换，无需手动调用 `resolveUrl`。

`SysUserVO.java` 的实际写法（`SysUserVO.java:35`）：

```java
import static com.sz.platform.constant.AdminSceneCodeConstant.ADMIN_USER_LOGO_SCENE_CODE;
// ADMIN_USER_LOGO_SCENE_CODE = "admin.user.logo"

@OssUrlFill(sceneCode = ADMIN_USER_LOGO_SCENE_CODE)
@Schema(description = "LOGO")
private String logo;   // 数据库存 objectKey，Controller 返回时自动替换为完整访问 URL
```

Controller 无需任何额外处理：

```java
// 以查询用户列表为例，框架拦截 ApiResult 返回值，自动将 SysUserVO.logo 中的
// objectKey（如 "logo/1001/20260424/avatar.png"）替换为可访问的完整 URL
@GetMapping("/list")
public ApiResult<PageResult<SysUserVO>> list(SysUserListDTO dto) {
    return ApiResult.success(sysUserService.list(dto));
}
```

> 自定义解析器、非 Controller 场景手动触发、注意事项，见 [§6.7](#67-ossurlfill-注解vo-自动回填-url)。

### 可选：curl 验证

```bash
# 上传头像，pathSegments 传用户 id（决定文件存放的子目录层级）
curl -X POST http://127.0.0.1:19991/api/admin/resource/upload \
  -H "Authorization: Bearer <token>" \
  -F "sceneCode=admin.user.logo" \
  -F "pathSegments=1001" \
  -F "file=@./avatar.png"
```

### 前端对接（Vue 3 + sz-admin）

> 以下代码取自 `sz-admin` 项目，可直接对照 `src/api/modules/system/upload.ts`、`src/components/Upload/` 目录查阅。

#### 1. API 函数与类型

新增 `uploadResource()` 函数，统一对接 `POST /resource/upload`：

```typescript
// src/api/modules/system/upload.ts

/** 上传请求参数 */
export type ResourceUploadFile = {
  file: UploadRawFile;
  sceneCode: string;       // 必填，对应 yml scene.code，如 "admin.user.logo"
  bizKey?: string;         // 可选，BIZ_KEY 命名规则时必填（ORIGINAL/UUID 可不传）
  pathSegments?: string;   // 可选，路径分段，逗号分割；BIZ_DATE 策略时生效，如 "1001" 或 "user,logo"
};

/** 上传响应结果 */
export type ResourceUploadResult = {
  objectKey: string;    // 存储路径，入库值，如 "logo/user/logo/20260424/avatar.png"
  originName: string;   // 原始文件名，如 "avatar.png"
  accessUrl: string;    // 可访问 URL（LOCAL: Java 代理地址；OSS PRESIGNED: 带签名临时链接；OSS DIRECT: 明文链接）
  contentType: string;  // MIME 类型，如 "image/png"
  size: number;         // 文件大小（字节）
  eTag: string;         // ETag（OSS 场景有值，LOCAL 为空）
  resourceId: number;   // sys_resource 审计记录 ID
};

/** 统一资源上传接口 */
export const uploadResource = (
  params: ResourceUploadFile,
  config?: CustomAxiosRequestConfig<any>
) => {
  return http.upload<ResourceUploadResult>(ADMIN_MODULE + `/resource/upload`, params, config);
};
```

> 旧的 `uploadFile()`（对接 `/sys-file/upload`）已标记 `@deprecated`，新业务一律使用 `uploadResource()`。

---

#### 2. 上传组件 Props 变化

四个上传组件（`Upload/Img.vue`、`Upload/Imgs.vue`、`Upload/UploadFiles.vue`、`SimplifyUpload/index.vue`）均已从旧 `dir` prop 迁移到以下三个新 prop：

| 旧 prop | 新 prop | 类型 | 必填 | 说明 |
|---------|---------|------|------|------|
| `dir` | `sceneCode` | `string` | 建议填写 | 对应 yml `scene.code`，决定存储驱动、命名、路径等所有规则 |
| —      | `bizKey` | `string` | 否 | `naming: BIZ_KEY` 时作为文件名；`ORIGINAL`/`UUID` 可不传 |
| —      | `pathSegments` | `string` | 否 | 路径分段，逗号分隔；`path-strategy: BIZ_DATE` 时每段对应一级子目录 |

**`pathSegments` 场景举例：**

| 传值示例 | 生成路径结构 | 适用场景 |
|---------|-------------|---------|
| `"1001"` | `logo/1001/20260424/avatar.png` | 按用户 ID 隔离 |
| `"user,logo"` | `logo/user/logo/20260424/avatar.png` | 多级业务分类 |
| 不传 | `logo/20260424/avatar.png` | 不需要业务子目录 |

**响应字段变化（旧 → 新）：**

| 旧字段（`UploadResult`）| 新字段（`ResourceUploadResult`）| 说明 |
|------------------------|--------------------------------|------|
| `url` | `accessUrl` | 可访问 URL，仅用于上传后即时预览，不入库 |
| `filename` | `originName` | 原始文件名 |
| `fileId` | `resourceId` | 审计记录 ID |
| `dirTag` | `sceneCode`（请求参数） | 场景编码，响应不再回传 |
| —      | `objectKey` | **新增**，入库值，数据库存这个字段 |
| `objectName` | `objectKey` | 字段重命名 |

> **核心原则**：`objectKey` 存库，`accessUrl` 仅用于上传后的即时预览。查询列表时由后端 `@OssUrlFill` 自动回填 URL（见 Step 5）。

---

#### 3. 用户头像接入示例（`admin.user.logo`）

取自 `UserAdd.vue` / `UserEdit.vue` 实际代码：

```vue
<UploadImg
  v-model:image-url="paramsProps.row.logo"
  scene-code="admin.user.logo"
  path-segments="user,logo"
  width="135px"
  height="135px"
  border-radius="50%"
  @change="fileChange"
>
  <template #empty>
    <el-icon><Avatar /></el-icon>
  </template>
</UploadImg>
```

```typescript
import type { IResourceUploadResult } from '@/api/types/system/upload';

// v-model:image-url 双向绑定 objectKey（入库字段）
// 上传成功后组件内部自动用 accessUrl 本地预览，无需额外处理

const fileChange = (data: IResourceUploadResult) => {
  // data.objectKey  → 入库值（已通过 v-model 同步到 row.logo）
  // data.accessUrl  → 即时预览地址（组件内部已处理，此处按需使用）
  // data.resourceId → sys_resource 审计 ID（按需关联）
  console.log(data);
};
```

**`Upload/Img.vue` 的 `previewUrl` prop 说明：**

`Img.vue` 新增了 `previewUrl` prop，用于**编辑场景**下回显已有头像：

```vue
<!-- 编辑用户时：logo 是 objectKey（从接口拿到的已回填 URL 或原始 objectKey 均可） -->
<UploadImg
  v-model:image-url="row.logo"
  :preview-url="row.logoUrl"
  scene-code="admin.user.logo"
  path-segments="user,logo"
/>
```

| prop | 绑定值 | 说明 |
|------|--------|------|
| `image-url`（v-model） | `objectKey` | 入库字段，双向绑定 |
| `preview-url` | `accessUrl` 或后端回填的完整 URL | 仅用于展示，不入库 |

> 若后端已通过 `@OssUrlFill` 将 `logo` 字段直接回填为完整 URL（如 §2 Step 5 所示），则 `image-url` 本身已是可访问地址，`preview-url` 可不传；`@OssUrlFill` 未生效时，需要额外绑定 `preview-url` 来正确回显。

---

#### 4. 文件上传场景对照（UploadFiles）

`UploadFiles.vue` 默认 `sceneCode` 为 `system.template`（对应临时文件场景）：

```vue
<!-- 临时文件上传，使用默认 sceneCode，无需显式传参 -->
<UploadFiles v-model="fileUrls" />

<!-- 指定其他场景，如合同文档 -->
<UploadFiles
  v-model="fileUrls"
  scene-code="biz.contract"
  path-segments="deptId,year"
  :limit="5"
  :file-size="10"
  accept=".pdf,.doc,.docx"
  @all-success="handleAllSuccess"
/>
```

```typescript
import type { IResourceUploadResult } from '@/api/types/system/upload';

const fileUrls = ref<IResourceUploadResult[]>([]);

function handleAllSuccess(list: IResourceUploadResult[]) {
  // list[i].objectKey  → 入库
  // list[i].originName → 显示文件名（原 filename）
  // list[i].accessUrl  → 下载/预览地址（后端已处理签名，前端直接使用）
  console.log('全部上传成功，共', list.length, '条：', list);
}
```

> 下载逻辑已简化：`UploadFiles.vue` 直接使用后端返回的 `accessUrl` 发起下载，不再经过前端 `getOssPreviewUrl()` 转换。私有桶（OSS PRESIGNED）的签名 URL 由后端在上传时生成，前端透传即可。


---

## 3. 核心概念

### 3.1 Scene（场景）

一个 scene 描述 **一类资源** 的全部存取策略：存在哪（LOCAL / OSS）、叫什么名（naming）、放哪个目录（path-strategy）、怎么访问（serve-mode）、允许哪些扩展名和大小。

业务代码只需 `sceneCode`（如 `demo.avatar`），无需感知底层细节。

### 3.2 objectKey（存储键）

- **定义**：相对于全局 `root` 的完整子路径，**不包含** `root` 前缀。
- **数据库存储值**：业务应只存 `objectKey`，**不应** 存完整 URL（URL 随环境变化，objectKey 稳定）。
- **示例**：`avatars/20260424/5f7c...9a.png`

### 3.3 accessUrl（访问 URL）

根据 `serve-mode` 三分：

| serveMode | accessUrl 生成方式 | 适用场景 |
|-----------|---------------------|---------|
| DIRECT | `base-url` + objectKey 去 basePath 前缀后的相对部分 | 公开资源（头像、Logo、静态文档） |
| PRESIGNED | 由 OSS 驱动签发临时签名 URL，有效期 = `scene.expire` 秒 | 私有 bucket 上的图片预览等 |
| TOKEN | **本模块返回 `null`**，URL 由上层业务按策略生成（token + 代理接口） | 合同、敏感文件，需隐藏真实地址 |

> TOKEN 模式下实际 URL 的签发 **不在本模块职责内**，由上层业务决定端点与令牌机制。

### 3.4 配置优先级

文件大小：`min(全局 security.maxSize, 场景 maxSize)`

扩展名白名单：**两者同时生效**（先过全局白名单，再过场景白名单；场景是进一步收窄）

MIME 白名单：**场景优先**（`scene.mimes` 非空时不再查全局）

---

## 4. 配置参考

### 4.1 顶级字段（前缀 `sz.resource`）

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `root` | String | `./data` | 本地存储根目录（LOCAL 有效） |
| `default-storage-type` | Enum | `LOCAL` | 默认存储类型（目前仅作为语义保留，实际类型以 scene.type 为准） |
| `security` | Object | 见下表 | 全局安全策略 |
| `scenes` | List | `[]` | 场景列表 |

### 4.2 `security` 三字段

| 字段 | 类型 | 未配时默认值 | 说明 |
|------|------|--------------|------|
| `allowed-exts` | Set<String> | `ResourceSecurityDefaults.DEFAULT_ALLOWED_EXTS` | 全局扩展名白名单（小写无点） |
| `allowed-mime-types` | Set<String> | `ResourceSecurityDefaults.DEFAULT_ALLOWED_MIMES` | 全局 MIME 白名单 |
| `max-size` | DataSize | `50MB` | 全局最大文件大小，支持 `50MB` / `10KB` / `1GB` |

默认扩展名白名单覆盖：图片、文档、压缩包、音视频（详见 §10.3）。

### 4.3 `scene` 字段全表

| 字段 | 类型 | 默认值 | 必填条件 | 说明 |
|------|------|--------|----------|------|
| `code` | String | — | 必填 | 场景唯一编码，如 `demo.avatar` |
| `type` | Enum | `LOCAL` | — | `LOCAL` / `OSS` |
| `serve-mode` | Enum | `DIRECT` | — | `DIRECT` / `TOKEN` / `PRESIGNED` |
| `expire` | Long（秒） | `3600` | PRESIGNED 必须 >0 | 临时 URL 有效期 |
| `path` | String | — | type=LOCAL 必填 | 相对 `root` 的子目录，如 `avatars/` |
| `base-url` | String | — | serve-mode=DIRECT 必填 | 访问根 URL（不含末尾斜杠） |
| `bucket` | String | — | type=OSS 必填 | OSS 桶名；同时作为 objectKey 一级前缀 |
| `naming` | Enum | `UUID` | — | `BIZ_KEY` / `UUID` / `ORIGINAL` / `TIMESTAMP` |
| `path-strategy` | Enum | `FLAT` | — | `FLAT` / `DATE` / `BIZ` / `BIZ_DATE` |
| `exts` | Set<String> | null | — | 场景扩展名白名单；为空则只查全局 |
| `mimes` | Set<String> | null | — | 场景 MIME 白名单；非空时忽略全局 MIME |
| `max-size` | long（MB） | `0` | — | 场景最大文件大小，0=不限 |

### 4.4 启动期强校验（`ResourceProperties.validate()` 共 5 条）

以下任一失败，应用启动时抛 `IllegalStateException` 终止：

| # | 规则 | 报错信息片段 |
|---|------|-------------|
| 1 | `type=LOCAL` 时 `path` 必填且不含路径穿越字符 | `type=LOCAL 但未配置 path` |
| 2 | `type=OSS` 时 `bucket` 必填且不含路径穿越字符 | `type=OSS 但未配置 bucket` |
| 3 | `serve-mode=DIRECT` 时 `base-url` 必填 | `serveMode=DIRECT 但未配置 base-url` |
| 4 | `serve-mode=PRESIGNED` + `type=LOCAL` 组合禁用 | `PRESIGNED 不能与 type=LOCAL 组合` |
| 5 | `serve-mode=PRESIGNED` 时 `expire > 0` | `serveMode=PRESIGNED 但 expire 无效` |

另有两条附加规则：
- `code` 不能为空
- `code` 不能重复

若 `scenes` 列表为空，只打印 WARN（不抛异常），但资源模块将不可用。

---

## 5. 使用案例集

> 下面 10 个案例覆盖绝大多数真实需求，全部使用虚构场景 `demo.avatar` / `demo.document` / `demo.archive` 演示。

### 案例 A：LOCAL + DIRECT + Java 直接返回

适合：内网小文件、Demo 环境。

```yaml
sz:
  resource:
    root: ./data
    scenes:
      - code: demo.avatar
        type: LOCAL
        serve-mode: DIRECT
        path: avatars/
        base-url: http://127.0.0.1:8080/api/resource/file/avatars
        naming: UUID
```

```java
ResourceUploadResult r = resourceService.upload("demo.avatar", null, file);
// r.getObjectKey()  → avatars/5f7c...9a.png
// r.getAccessUrl()  → http://127.0.0.1:8080/api/resource/file/avatars/5f7c...9a.png
```

> `base-url` 指向的 Java 接口由 **上层业务** 提供（读取 `objectKey` 并回流字节）；本模块不内置此 Controller。

### 案例 B：LOCAL + DIRECT + Nginx 前置

适合：生产静态资源、高并发读。

```yaml
sz:
  resource:
    root: /var/www/static
    scenes:
      - code: demo.avatar
        type: LOCAL
        serve-mode: DIRECT
        path: avatars/
        base-url: https://cdn.example.com/static/avatars   # ← 指向 Nginx
        naming: UUID
```

Nginx 配置片段（示意）：

```nginx
location /static/ {
    alias /var/www/static/;
}
```

> DIRECT 模式的 **Java/Nginx 切换由 `base-url` 隐式决定**，代码无需改动。

### 案例 C：OSS + DIRECT（公开读 bucket）

适合：已配置公开读 ACL 的 bucket。

```yaml
sz:
  resource:
    scenes:
      - code: demo.avatar
        type: OSS
        serve-mode: DIRECT
        bucket: my-public-avatars
        base-url: https://my-public-avatars.oss-cn-hangzhou.aliyuncs.com
        naming: UUID
        path-strategy: DATE
```

> 须同时引入 `sz-common-oss` 并配置厂商（详见 `sz-common-oss/docs/oss-provider-guide.md`）。

### 案例 D：OSS + PRESIGNED（私有 bucket）

适合：私有 bucket 上的图片预览类场景。

```yaml
sz:
  resource:
    scenes:
      - code: demo.document
        type: OSS
        serve-mode: PRESIGNED
        bucket: my-private-docs
        expire: 900                  # 15 分钟
        naming: UUID
        path-strategy: DATE
        exts: [pdf, docx, xlsx]
```

```java
ResourceUploadResult r = resourceService.upload("demo.document", null, file);
// r.getAccessUrl() 是带 Signature/Expires 的临时 URL，15 分钟内可直接访问
```

调用方 **无需感知** PRESIGNED，与 DIRECT 模式 API 完全一致。

### 案例 E：TOKEN 模式

适合：合同、身份证照片等敏感资源，需隐藏真实存储地址。

```yaml
sz:
  resource:
    scenes:
      - code: demo.document
        type: LOCAL
        serve-mode: TOKEN
        path: contracts/
        naming: UUID
```

```java
ResourceUploadResult r = resourceService.upload("demo.document", null, file);
// r.getAccessUrl() == null  ← 本模块不生成 URL
// r.getObjectKey() == "contracts/xxx.pdf"
```

> **模块边界**：TOKEN 模式的实际访问 URL 由上层业务生成（一般做法是签发短时 token，前端带 token 调代理接口，接口内部再用 `resourceService.readStream(sceneCode, objectKey)` 读文件回流）。具体端点与 token 机制 **不属于本模块职责**。

### 案例 F：命名规则选型（4 种）

| naming | 典型用途 | 文件名示例 | 是否覆盖同名 |
|--------|---------|------------|--------------|
| `BIZ_KEY` | Logo 按业务 id 覆盖 | `github.svg`（`namingKey="github"`） | 是（相同 key 重传覆盖） |
| `UUID`（默认） | 用户上传普通文件 | `5f7c3b2e9a14d8f0b6e1a2c3d4e5f678.png` | 否 |
| `ORIGINAL` | 保留原始文件名 | `report.pdf` / `report_1745742241123.pdf`（同名冲突时） | 否（冲突时自动追加毫秒时间戳） |
| `TIMESTAMP` | 导出文件、日志归档 | `20260424143025123.csv` | 毫秒级，理论可冲突 |

`BIZ_KEY` 示例：

```java
resourceService.upload("demo.avatar", "user_123", file);
// objectKey = avatars/user_123.png
```

> `namingKey` 仅在 `naming=BIZ_KEY` 时生效；其他 naming 传 `null`。`namingKey` 会被清洗（仅保留 `[a-zA-Z0-9_-]`，其他字符替换为 `_`）。

### 案例 G：路径策略选型（4 种）

假设 `path=avatars/`、`pathSegments=["tenant1", "group2"]`、当日 `20260424`、文件名 `abc.png`：

| path-strategy | objectKey |
|---------------|-----------|
| `FLAT`（默认） | `avatars/abc.png` |
| `DATE` | `avatars/20260424/abc.png` |
| `BIZ` | `avatars/tenant1/group2/abc.png` |
| `BIZ_DATE` | `avatars/tenant1/group2/20260424/abc.png` |

```java
// BIZ / BIZ_DATE 时 pathSegments 可变参数传入业务层级
resourceService.upload("demo.avatar", null, file, "tenant1", "group2");
```

> `BIZ` 指的是 **pathSegments 参数**，不是 `namingKey`；二者互相独立。

### 案例 H：场景白名单与 MIME 收窄

```yaml
sz:
  resource:
    security:
      allowed-exts: [jpg, jpeg, png, pdf, docx]   # 全局放行 5 种
    scenes:
      - code: demo.avatar
        type: LOCAL
        serve-mode: DIRECT
        path: avatars/
        base-url: http://127.0.0.1:8080/api/resource/file/avatars
        naming: UUID
        exts: [jpg, jpeg, png]                    # 场景仅允许图片 3 种
        mimes: [image/jpeg, image/png]            # 严格 MIME 限制
        max-size: 2                               # 进一步压到 2MB
```

最终 `demo.avatar` 仅接受 jpg/jpeg/png 且 ≤2MB 的图片；pdf 会被 **场景白名单** 阻断。

### 案例 I：大文件走 stream 而非 bytes

```java
// ❌ 30 GB 视频用 readBytes 直接 OOM
byte[] data = resourceService.readBytes("demo.archive", objectKey);

// ✅ 用 readStream，调用方负责关闭
try (InputStream in = resourceService.readStream("demo.archive", objectKey)) {
    IOUtils.copy(in, response.getOutputStream());
}
```

### 案例 J：前端回传完整 URL 的防御

某些遗留前端会把完整 `accessUrl` 作为表单字段回传。直接入库会污染数据库（URL 随环境变化）。用 `normalizeObjectKey` 还原：

```java
String raw = form.getAvatar();
// raw 可能是 "http://host/api/resource/file/avatars/abc.png"
// 也可能已经是 "avatars/abc.png"
String objectKey = resourceService.normalizeObjectKey("demo.avatar", raw);
user.setAvatar(objectKey);
```

- 若 `raw` 不是以 `http://` / `https://` 开头：原样返回
- 若 `raw` 以场景 `base-url` 开头：剥离 base-url，拼回 `path` 前缀得到完整 objectKey，并走 `PathSanitizer` 校验
- 若 `raw` 是完整 URL 但不以 `base-url` 开头：抛 `IllegalArgumentException`

---

## 6. ResourceService API 参考

### 6.1 API 总览

```java
public class ResourceService {
    ResourceUploadResult upload(String sceneCode, String namingKey, MultipartFile file, String... pathSegments);
    String               resolveUrl(String sceneCode, String objectKey);
    String               normalizeObjectKey(String sceneCode, String rawValue);
    byte[]               readBytes(String sceneCode, String objectKey);
    InputStream          readStream(String sceneCode, String objectKey);
}
```

### 6.2 `upload`

| 参数 | 必填 | 说明 |
|------|------|------|
| `sceneCode` | 是 | 场景编码 |
| `namingKey` | `naming=BIZ_KEY` 时必填，其他传 `null` | 业务标识，清洗后作为文件名 |
| `file` | 是 | Spring `MultipartFile` |
| `pathSegments` | `path-strategy=BIZ` / `BIZ_DATE` 时必填 | 变长业务层级 |

**异常**：
- `IllegalArgumentException`：文件为空、扩展名被黑名单拒绝、双重扩展名攻击、命中白名单外、MIME 不符、大小超限、`namingKey` 非法
- `IllegalStateException`：OSS 场景但未引入 `sz-common-oss`（`OssClient` 未注册）
- `IOException`：底层存储写入失败

### 6.3 `resolveUrl`

| serveMode | 返回值 |
|-----------|--------|
| DIRECT | `base-url` + objectKey 去前缀后的相对部分 |
| PRESIGNED | 临时签名 URL，有效期 `scene.expire` 秒 |
| TOKEN | **`null`**（由上层业务按策略生成 URL） |

`objectKey` 为 `null` / 空时返回 `null`。

### 6.4 `normalizeObjectKey`

见 [案例 J](#案例-j前端回传完整-url-的防御)。仅服务于防御性数据清洗，**不应** 把完整 URL 入库当成常规操作。

### 6.5 `readBytes` / `readStream`

- `readBytes`：全量加载进内存，**仅用于小文件**
- `readStream`：真流式，**大文件必用**，调用方负责关闭返回的 `InputStream`

### 6.6 `ResourceUploadResult` 字段

| 字段 | 由谁填充 | 说明 |
|------|---------|------|
| `objectKey` | 本模块 | **应入库** |
| `originName` | 本模块 | 上传时的原始文件名 |
| `size` | 本模块 | 字节 |
| `contentType` | 本模块 | 上传时的 MIME |
| `accessUrl` | 本模块 | DIRECT / PRESIGNED 有值；TOKEN 为 `null` |
| `eTag` | **驱动层**（OSS 有值，LOCAL 为 `null`） | 存储端 ETag |
| `resourceId` | **上层业务**（本模块 **不填**） | `sys_resource` 表主键，如需落库由业务层 set |

---

## 6.7 @OssUrlFill 注解（VO 自动回填 URL）

### 6.7.1 解决什么问题

数据库存的是 `objectKey`，前端展示需要完整 URL。传统做法是在 Service 层逐字段调 `resolveUrl`——字段多时代码冗长。

`@OssUrlFill` 注解让框架在 **Controller 返回前自动完成替换**，业务代码无需手动转换。

### 6.7.2 基本用法

在 VO 的 `String` 类型 objectKey 字段上标注：

```java
@Data
public class UserVO {
    private Long userId;

    @OssUrlFill(sceneCode = "demo.avatar")
    private String avatar;          // 存 objectKey，返回前自动替换为完整 URL
}
```

Controller 照常返回，无需额外处理：

```java
@GetMapping("/user/{id}")
public ApiResult<UserVO> getUser(@PathVariable Long id) {
    UserVO vo = userService.getById(id);
    // vo.getAvatar() 此时仍是 "avatars/20260424/abc.png"
    return ApiResult.success(vo);
    // 框架在返回前自动将 avatar 替换为完整 URL
}
```

### 6.7.3 自定义解析器

当默认的 `ResourceService.resolveUrl` 不够用时（例如：OAuth 头像需走第三方 URL，本地头像才走 OSS），可实现 `OssUrlResolver<T>`：

```java
@Component
@RequiredArgsConstructor
public class AvatarUrlResolver implements OssUrlResolver<UserVO> {

    private final ResourceService resourceService;

    @Override
    public String resolve(String sceneCode, UserVO vo, String fieldValue) {
        // OAuth 来源直接用第三方 URL
        if ("OAUTH".equals(vo.getAvatarSource())) {
            return vo.getOauthAvatarUrl();
        }
        return resourceService.resolveUrl(sceneCode, fieldValue);
    }
}
```

注解上引用（推荐按类型引用，类型安全）：

```java
@OssUrlFill(sceneCode = "demo.avatar", resolverClass = AvatarUrlResolver.class)
private String avatar;
```

也可按 Bean 名称引用（兼容方式，`resolverClass` 已设置时本属性被忽略）：

```java
@OssUrlFill(sceneCode = "demo.avatar", customResolver = "avatarUrlResolver")
private String avatar;
```

**解析器优先级**：`resolverClass` > `customResolver` > 默认 `ResourceService.resolveUrl`

### 6.7.4 非 Controller 场景

AOP 切面只拦截 `@RestController` 方法返回值。消息 handler、定时任务等非 Controller 场景需手动调用：

```java
@RequiredArgsConstructor
public class SomeMessageHandler {

    private final OssUrlFillProcessor ossUrlFillProcessor;

    public void handle(List<UserVO> users) {
        ossUrlFillProcessor.process(users);   // 支持单对象、Collection、PageResult
        // 处理后 users 中每个 VO 的 @OssUrlFill 字段已替换为完整 URL
    }
}
```

### 6.7.5 注意事项

| 事项 | 说明 |
|------|------|
| 字段类型限制 | 仅支持 `String` 类型字段；非 `String` 字段标注后会打印 WARN 并跳过 |
| 已是 URL 的字段 | 字段值以 `http://` 或 `https://` 开头时自动跳过，兼容历史存了完整 URL 的数据 |
| 字段为空时 | `null` 或空白值直接跳过，不会报错 |
| TOKEN 场景 | `resolveUrl` 返回 `null` 时框架跳过覆写，字段保持原 objectKey |
| 反射缓存 | 同一 VO 类的字段反射结果缓存在 `ConcurrentHashMap` 中，同类型只扫描一次，不影响性能 |

---

## 7. 从 OssClient / UploadResult 迁移

> 本节覆盖 **业务代码层** 的迁移（Service 层从 `OssClient` 直调改为 `ResourceService`）。
> 数据库表结构迁移（如 `sys_file` → `sys_resource`）不在本节范围。

### 7.1 为什么迁移

| 维度 | 旧：OssClient 直调 | 新：ResourceService |
|------|---------------------|---------------------|
| 存储类型 | 硬编码 OSS | LOCAL / OSS 配置切换 |
| 路径规则 | 代码里拼字符串 | 配置化（naming + path-strategy） |
| 安全白名单 | 业务自己写 if 判断 | 三层模型自动校验 |
| 签名 URL | 业务手动调 SDK | PRESIGNED 配置后自动生成 |
| 访问模式 | 要么公开要么业务自己签 token | DIRECT / TOKEN / PRESIGNED 三选一 |

### 7.2 API 对照表

| 旧 API | 新 API |
|--------|-------|
| `ossClient.upload(inputStream, bucket, objectKey, contentType)` | `resourceService.upload(sceneCode, namingKey, file, ...segments)` |
| `ossClient.upload(file, bucket, path)` | `resourceService.upload(sceneCode, null, file)` |
| `ossClient.getPresignedUrl(bucket, key, expire)` | 配置 `serve-mode: PRESIGNED` + `expire`，调 `resolveUrl` 自动生成 |
| `ossClient.getPublicUrl(bucket, key)` | 配置 `serve-mode: DIRECT` + `base-url`，调 `resolveUrl` |
| `ossClient.download(bucket, key)` | `resourceService.readBytes(sceneCode, objectKey)` |
| `ossClient.downloadStream(bucket, key)` | `resourceService.readStream(sceneCode, objectKey)` |
| `ossClient.delete(bucket, key)` | 暂无 `ResourceService` 对应方法；如需删除，注入对应 `ResourceStorageDriver` 调 `delete` |

### 7.3 模型字段映射

| 旧 `UploadResult` 字段 | 新 `ResourceUploadResult` 字段 | 备注 |
|------------------------|------------------------------|------|
| `fileName` | `originName` | 原始文件名 |
| `filePath` / `key` | `objectKey` | 入库值 |
| `url` | `accessUrl` | TOKEN 模式下为 `null` |
| `fileSize` | `size` | 字节 |
| `fileType` / `contentType` | `contentType` | MIME |
| `eTag` | `eTag` | 保持一致 |
| — | `resourceId` | 新增，供上层业务回填 |

### 7.4 迁移步骤（业务代码层）

**Step 1** 在 yml 为该业务定义一个 scene：

```yaml
sz:
  resource:
    scenes:
      - code: demo.archive           # 给旧业务起个 sceneCode
        type: OSS                    # 与旧代码一致的存储
        serve-mode: PRESIGNED
        bucket: archive-bucket       # 搬旧硬编码 bucket
        expire: 3600
        naming: UUID
        path-strategy: DATE
```

**Step 2** 替换注入：

```diff
- private final OssClient ossClient;
+ private final ResourceService resourceService;
```

**Step 3** 替换调用：

```diff
- UploadResult ur = ossClient.upload(file, "archive-bucket", "archives/");
- String url = ossClient.getPresignedUrl("archive-bucket", ur.getKey(), 3600);
- entity.setUrl(url);
- entity.setKey(ur.getKey());
+ ResourceUploadResult ur = resourceService.upload("demo.archive", null, file);
+ entity.setObjectKey(ur.getObjectKey());   // 入库存 objectKey，不存 URL
```

**Step 4** 展示侧按需生成 URL：

```diff
- return entity.getUrl();   // 旧：URL 直接入库，切环境会失效
+ return resourceService.resolveUrl("demo.archive", entity.getObjectKey());
```

### 7.5 并存策略

- `OssClient.upload(...)` 的若干重载当前标注为 `@Deprecated`，**短期内不会删除**，留给未迁移模块使用。
- 迁移应以 **Service 为单位** 逐个进行，不必一次性全切。
- 迁移完成后，建议在 PR 描述中说明"本次已从 OssClient 切换至 ResourceService"，便于代码 Review 聚焦。

---

## 8. 常见问题排查

### 8.1 启动期

| 现象 | 原因 | 处理 |
|------|------|------|
| `IllegalStateException: 场景配置缺少 code 字段` | scene 列表某项未写 `code` | 检查 yml |
| `IllegalStateException: 场景编码重复` | 两个 scene 写了相同 `code` | 重命名 |
| `IllegalStateException: type=LOCAL 但未配置 path` | LOCAL 场景忘配 `path` | 补 `path` |
| `IllegalStateException: type=OSS 但未配置 bucket` | OSS 场景忘配 `bucket` | 补 `bucket` |
| `IllegalStateException: serveMode=DIRECT 但未配置 base-url` | DIRECT 场景忘配 `base-url` | 补 `base-url` |
| `IllegalStateException: PRESIGNED 不能与 type=LOCAL 组合` | LOCAL 场景误配 PRESIGNED | 改为 DIRECT 或 TOKEN |
| `IllegalStateException: PRESIGNED 但 expire 无效` | `expire` 为 null / ≤0 | 设置正整数秒数 |
| `IllegalStateException: path 配置非法 [...]` | `path` 含 `..` / `\` / 绝对路径等 | 用相对路径，不带特殊字符 |
| WARN `未配置任何资源场景` | `scenes` 为空 | 至少配一个 scene（否则 ResourceService 不可用） |

### 8.2 运行期

| 现象 | 原因 | 处理 |
|------|------|------|
| `IllegalArgumentException: 未找到资源场景配置` | sceneCode 拼错或未在 yml 定义 | 对齐 sceneCode |
| `IllegalStateException: OssClient 未在容器中注册` | 场景配了 OSS 但没引 `sz-common-oss` | 引入依赖或改用 LOCAL |
| `UnsupportedOperationException`（LOCAL 驱动 presign） | 代码绕过校验直接调 driver | 理论不可达；如果出现说明启动校验被短路，检查是否有自定义 `ResourceSceneProvider` 跳过了 validate |
| `IllegalArgumentException: 文件类型 .xxx 在黑名单中` | 上传了 `exe/bat/php/jsp/...` 等危险文件 | 硬编码不可覆盖，拒绝上传 |
| `IllegalArgumentException: 检测到双重扩展名攻击` | 文件名形如 `report.php.jpg` | 拒绝 |
| `IllegalArgumentException: 不支持的文件类型` | 超出场景 `exts` 范围 | 检查 exts 配置 |
| `IllegalArgumentException: 文件大小超限` | 超出 `min(全局 maxSize, 场景 maxSize)` | 检查两处 max-size |
| `IllegalArgumentException: namingKey 不合法` | BIZ_KEY 命名但 namingKey 清洗后为空 | 传有效字符 |
| `accessUrl` 前缀重复 / 缺斜杠 | `base-url` 写了末尾 `/` 或 `bucket` 前缀重复 | `base-url` 不要以 `/` 结尾；objectKey 以 `bucket/` 开头是故意设计（OSS 驱动会自动剥离） |
| 文件名含中文变成 `_` | `naming=ORIGINAL` 清洗规则 | 正则 `[^a-zA-Z0-9_\-.\u4e00-\u9fa5]` 保留中文；其他字符替换为 `_`（如 `空格`、`()`） |
| OSS 上传后路径是 `bucket/bucket/xxx` | 上层重复拼接了 bucket 前缀 | 业务代码不要手工拼 bucket；由驱动 `ObjectKeyUtils.toPhysicalKey` 统一处理 |

---

# 第二部分 · 进阶原理

## 9. 架构原理

### 9.1 分层

```
┌─────────────────────────────────────────┐
│ 业务层 Controller / Service               │   ← 只感知 ResourceService
└────────────────────┬────────────────────┘
                     │
         ┌───────────▼───────────┐
         │  ResourceService      │         ← 编排：校验 + 构 key + 选驱动 + 算 URL
         └───┬───────────────┬───┘
             │               │
   ┌─────────▼────┐ ┌────────▼─────────┐
   │ SceneProvider │ │ SecurityProvider │   ← SPI 扩展点
   │ (Yml 默认)    │ │ (Yml 默认)       │
   └──────────────┘ └──────────────────┘
             │
  ┌──────────▼──────────────┐
  │ ResourceStorageDriver   │
  │   ├─ LocalDriver         │
  │   └─ OssDriver (可选)    │                ← @ConditionalOnBean(OssClient)
  └─────────────────────────┘
             │
       ┌─────▼──────┐
       │ sz-common-oss│                       ← 可选依赖
       └─────────────┘
```

### 9.2 OSS 驱动的可选装配

`OssResourceStorageDriver` 带 `@ConditionalOnBean(OssClient.class)`；未引入 `sz-common-oss` 时 bean 不会注册，`ResourceService` 通过 `ObjectProvider.getIfAvailable()` 拿到 `null`，仅当业务访问 OSS 场景时才抛 `IllegalStateException`。

这意味着：**仅用 LOCAL 的项目可以完全不依赖 OSS**。

### 9.3 objectKey 双语义

| 语义 | 使用场景 | 示例 |
|------|---------|------|
| 业务逻辑 key（含 bucket 前缀） | 数据库存储、`resolveUrl`、入库、跨场景日志 | `my-bucket/20260424/abc.png` |
| OSS 物理 key（不含 bucket） | 调 OSS SDK 的 `putObject` / `getObject` | `20260424/abc.png` |

`ObjectKeyUtils.toPhysicalKey(logicalKey, bucket)` 负责"逻辑 → 物理"剥离：若 `logicalKey` 以 `bucket/` 开头则剥离，否则原样返回（兼容旧数据）。

**原因**：若逻辑 key 不含 bucket 前缀，跨 bucket 的 URL 还原、入库数据迁移、bucket 名变更等都会失去稳定标识；统一规定"业务侧逻辑 key 始终含 bucket"可简化模型。

---

## 10. 安全模型（深入）

### 10.1 三层模型与校验顺序

```
文件清洗（文件名去路径分隔符 / 控制字符 / 长度截断）
  ↓
硬编码黑名单（BLOCKED_EXTS）—— 不可覆盖
  ↓
双重扩展名检测（file.php.jpg → 拒绝）
  ↓
全局扩展名白名单（security.allowed-exts / DEFAULT_ALLOWED_EXTS）
  ↓
场景扩展名白名单（scene.exts，非空时进一步收窄）
  ↓
MIME 校验（场景 mimes 优先，次查全局 mimes）
  ↓
大小校验（min(全局 max-size, 场景 maxSize)）
```

### 10.2 `BLOCKED_EXTS`（24 项，硬编码不可覆盖）

```
exe  bat  cmd  sh   bash ps1
php  jsp  jspx asp  aspx cgi
jar  war  class dll  so   msi
com  scr  pif  vbs  wsf  reg
```

即使在 `security.allowed-exts` 里显式写了这些扩展名，仍然会被拒绝。

### 10.3 `DEFAULT_ALLOWED_EXTS`（未配全局白名单时的默认值）

```
# 图片
jpg jpeg png gif bmp webp svg ico

# 文档
pdf doc docx xls xlsx ppt pptx rtf txt csv odt ods odp pages numbers keynote

# 压缩包
zip rar 7z tar gz

# 音视频
mp3 wav ogg mp4 mov avi wmv
```

`DEFAULT_ALLOWED_MIMES` 为上述扩展名对应的 MIME 类型集合，另追加 `application/octet-stream`（部分浏览器上传时使用）。

### 10.4 LOCAL 驱动路径防护（两层）

LOCAL 存储驱动在读写文件时执行两层路径安全校验：

**第一层：路径穿越防护**（上传和读取均生效）

```
物理路径 = root.toAbsolutePath().normalize().resolve(objectKey).normalize()
要求：物理路径必须以 root 绝对路径为前缀
```

若 `objectKey` 包含 `..` 等穿越序列导致路径逃逸出 `root`，直接抛 `IllegalArgumentException`。

**第二层：符号链接防护**（仅读取时生效）

```
realPath = file.toPath().toRealPath()   // 解析所有符号链接，得到真实物理路径
要求：真实路径仍必须在 root 目录之下
```

通过符号链接把 `root` 内某个文件指向 `root` 外部时，第一层校验通过（路径看起来合法），但 `toRealPath()` 解析后真实地址在 `root` 外，第二层拦截并抛 `IllegalArgumentException`。

> 注意：符号链接防护**只在读取时**生效（`readBytes` / `readStream`）；写入时不做此校验，由操作系统决定符号链接的写行为。

### 10.5 `PathSanitizer` 三模式

| 模式 | 调用时机 | 失败行为 | 失败抛出 |
|------|---------|---------|---------|
| `OBJECT_KEY` | 驱动内部 / `normalizeObjectKey` 还原后 | 抛异常 | `IllegalArgumentException` |
| `HTTP_PATH` | Controller 接收 URL 参数前 | 返回 `false` | — |
| `CONFIG` | `ResourceProperties.validate()` | 抛异常 | `IllegalStateException` |

共同禁止的字符/模式：`..` / `./` / 绝对路径前导 `/` / `\` / `\0` / 长度 >512 / `//` / `#?&` / 以 `.` 开头的隐藏段。

`HTTP_PATH` 额外会对值做 URL decode 再次校验（防御 `%252e%252e` 等双重编码攻击）。

---

## 11. SPI 扩展

### 11.1 `ResourceSceneProvider`

**默认实现**：`YmlResourceSceneProvider`，从 `ResourceProperties.sceneMap` 读取。

**扩展示例 · 数据库驱动**：

```java
@Component
@RequiredArgsConstructor
public class DbResourceSceneProvider implements ResourceSceneProvider {

    private final SysResourceSceneMapper mapper;
    private volatile Map<String, ResourceSceneConfig> cache;

    @Override
    public ResourceSceneConfig getScene(String sceneCode) {
        return getCache().get(sceneCode);
    }

    @Override
    public Map<String, ResourceSceneConfig> getAllScenes() {
        return getCache();
    }

    @Scheduled(fixedDelay = 60_000)
    public void refresh() {
        cache = mapper.selectAll().stream()
                .collect(Collectors.toMap(ResourceSceneConfig::getCode, s -> s));
    }

    private Map<String, ResourceSceneConfig> getCache() {
        if (cache == null) refresh();
        return cache;
    }
}
```

### 11.2 `ResourceSecurityPolicyProvider`

**默认实现**：`YmlSecurityPolicyProvider`，从 `ResourceProperties.security` 读取。

**扩展示例 · 数据库驱动**：

```java
@Component
@RequiredArgsConstructor
public class DbSecurityPolicyProvider implements ResourceSecurityPolicyProvider {

    private final SysConfigService configService;

    @Override
    public Set<String> getAllowedExts() {
        String val = configService.getConfigValue("resource.security.allowed-exts");
        return val != null ? Set.of(val.split(","))
                           : ResourceSecurityDefaults.DEFAULT_ALLOWED_EXTS;
    }

    @Override
    public Set<String> getAllowedMimeTypes() {
        String val = configService.getConfigValue("resource.security.allowed-mime-types");
        return val != null ? Set.of(val.split(","))
                           : ResourceSecurityDefaults.DEFAULT_ALLOWED_MIMES;
    }

    @Override
    public long getMaxSizeBytes() {
        String val = configService.getConfigValue("resource.security.max-size");
        return val != null ? DataSize.parse(val).toBytes()
                           : ResourceSecurityDefaults.DEFAULT_MAX_SIZE_BYTES;
    }
}
```

### 11.3 让位机制

`ResourceAutoConfiguration` 注册默认实现时使用 `@ConditionalOnMissingBean`：上层一旦提供自定义 `@Component` 实现，框架自动让位，无需额外配置。

### 11.4 自定义 Driver（不推荐）

理论上可实现 `ResourceStorageDriver` 接口新增存储介质（如 FTP、HDFS）。但考虑到：
- 安全校验、URL 构建等逻辑耦合在 `ResourceService` 内
- OSS 驱动已覆盖所有 S3 兼容存储

**当前不推荐自行扩展 Driver**。若确有需要，请先与架构组评审。

---

## 12. 相关文件索引

| 类型 | 路径 |
|------|------|
| 核心服务 | `sz-common-resource/src/main/java/com/sz/resource/service/ResourceService.java` |
| 配置属性 | `sz-common-resource/src/main/java/com/sz/resource/config/ResourceProperties.java` |
| 场景配置 | `sz-common-resource/src/main/java/com/sz/resource/config/ResourceSceneConfig.java` |
| 安全默认值 | `sz-common-resource/src/main/java/com/sz/resource/config/ResourceSecurityDefaults.java` |
| 自动装配 | `sz-common-resource/src/main/java/com/sz/resource/config/ResourceAutoConfiguration.java` |
| 存储驱动接口 | `sz-common-resource/src/main/java/com/sz/resource/driver/ResourceStorageDriver.java` |
| LOCAL 驱动 | `sz-common-resource/src/main/java/com/sz/resource/driver/LocalResourceStorageDriver.java` |
| OSS 驱动 | `sz-common-resource/src/main/java/com/sz/resource/driver/OssResourceStorageDriver.java` |
| 命名规则枚举 | `sz-common-resource/src/main/java/com/sz/resource/enums/NamingRuleEnum.java` |
| 路径策略枚举 | `sz-common-resource/src/main/java/com/sz/resource/enums/PathStrategyEnum.java` |
| 访问模式枚举 | `sz-common-resource/src/main/java/com/sz/resource/enums/ServeModeEnum.java` |
| 存储类型枚举 | `sz-common-resource/src/main/java/com/sz/resource/enums/StorageTypeEnum.java` |
| 上传结果模型 | `sz-common-resource/src/main/java/com/sz/resource/model/ResourceUploadResult.java` |
| 场景 SPI | `sz-common-resource/src/main/java/com/sz/resource/spi/ResourceSceneProvider.java` |
| 安全 SPI | `sz-common-resource/src/main/java/com/sz/resource/spi/ResourceSecurityPolicyProvider.java` |
| objectKey 工具 | `sz-common-resource/src/main/java/com/sz/resource/util/ObjectKeyUtils.java` |
| 路径清洗工具 | `sz-common-resource/src/main/java/com/sz/resource/util/PathSanitizer.java` |
| OSS 厂商配置指南 | `sz-common-oss/docs/oss-provider-guide.md` |
| **—以下为框架内部组件，扩展时参考—** | |
| VO URL 自动回填注解 | `sz-common-resource/src/main/java/com/sz/resource/annotation/OssUrlFill.java` |
| VO URL 自动回填切面 | `sz-common-resource/src/main/java/com/sz/resource/aspect/OssUrlFillAspect.java` |
| VO URL 填充处理器 | `sz-common-resource/src/main/java/com/sz/resource/processor/OssUrlFillProcessor.java` |
| 自定义 URL 解析器 SPI | `sz-common-resource/src/main/java/com/sz/resource/spi/OssUrlResolver.java` |
| 场景 SPI 默认实现 | `sz-common-resource/src/main/java/com/sz/resource/spi/YmlResourceSceneProvider.java` |
| 安全 SPI 默认实现 | `sz-common-resource/src/main/java/com/sz/resource/spi/YmlSecurityPolicyProvider.java` |
| 模块业务异常枚举 | `sz-common-resource/src/main/java/com/sz/resource/enums/ResourceResponseEnum.java` |

---

# 附录

## 附录 A · ResourceService API 速查表

| 方法 | 签名 | 返回 | 备注 |
|------|------|------|------|
| 上传 | `upload(sceneCode, namingKey, file, segments...)` | `ResourceUploadResult` | 三层安全校验；自动选驱动 |
| 生成 URL | `resolveUrl(sceneCode, objectKey)` | `String` / `null` | TOKEN 模式返回 `null` |
| 规范化 | `normalizeObjectKey(sceneCode, rawValue)` | `String` | 完整 URL → objectKey 防御 |
| 读字节 | `readBytes(sceneCode, objectKey)` | `byte[]` | **不用于大文件** |
| 读流 | `readStream(sceneCode, objectKey)` | `InputStream` | 调用方负责关闭 |

## 附录 B · yml 示例

### B.1 最小可运行配置（LOCAL + DIRECT，拷贝即用）

```yaml
sz:
  resource:
    root: ./data
    scenes:
      - code: demo.avatar
        type: LOCAL
        serve-mode: DIRECT
        path: avatars/
        base-url: http://127.0.0.1:8080/api/resource/file/avatars
        naming: UUID
        exts: [jpg, jpeg, png, webp]
        max-size: 5
```

### B.2 进阶混合配置（LOCAL + OSS + 三种访问模式）

```yaml
sz:
  resource:
    root: ./data
    default-storage-type: LOCAL
    security:
      allowed-exts: [jpg, jpeg, png, gif, webp, pdf, docx, xlsx, zip, mp4]
      allowed-mime-types:
        - image/jpeg
        - image/png
        - image/webp
        - application/pdf
        - application/vnd.openxmlformats-officedocument.wordprocessingml.document
        - application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
        - application/zip
        - video/mp4
      max-size: 100MB

    scenes:
      # 1. 本地 + 明文：用户头像（UUID + 按日期分层）
      - code: demo.avatar
        type: LOCAL
        serve-mode: DIRECT
        path: avatars/
        base-url: https://cdn.example.com/static/avatars
        naming: UUID
        path-strategy: DATE
        exts: [jpg, jpeg, png, webp]
        mimes: [image/jpeg, image/png, image/webp]
        max-size: 5

      # 2. OSS + 签名：私有 bucket 文档
      - code: demo.document
        type: OSS
        serve-mode: PRESIGNED
        bucket: private-docs
        expire: 900
        naming: UUID
        path-strategy: BIZ_DATE
        exts: [pdf, docx, xlsx]
        max-size: 20

      # 3. 本地 + TOKEN：合同（URL 由上层按需签发）
      - code: demo.archive
        type: LOCAL
        serve-mode: TOKEN
        path: archives/
        naming: BIZ_KEY
        path-strategy: BIZ
        exts: [zip, pdf]
        max-size: 50
```

## 附录 C · 与 sz-common-oss 的配合

| 约定 | 说明 |
|------|------|
| bucket 命名 | kebab-case（如 `private-docs`），与 OSS 厂商要求一致 |
| endpoint | 由 `sz-common-oss` 的 `sz.oss.*` 配置决定，本模块透明 |
| URL 拼接 | `base-url` + （objectKey 去 bucket 前缀后的相对部分）；本模块自动处理 |
| 依赖关系 | `sz-common-resource` pom 中 `sz-common-oss` 为 `optional=true`；OSS 驱动按需装配 |
| 详细配置 | 参见 `sz-common-oss/docs/oss-provider-guide.md` |
