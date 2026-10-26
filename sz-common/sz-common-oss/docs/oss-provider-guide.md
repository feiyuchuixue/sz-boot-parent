# OSS 对象存储厂商配置指南

本文档说明如何在 `oss.yml` 或 `application.yml` 中配置 `sz-common-oss`。配置前缀为 `sz.oss`；下列示例均从 `sz:` 开始。

本轮 RustFS / OSS 改造的修改前后行为、字段用途和跨项目配置链路见[改造前后对照与使用说明](./oss-rustfs-change-comparison.md)。scene 直接配置另一套 S3 连接的写法和优先级见[OSS 全局默认与 scene 独立连接设计](./oss-scene-connection-design.md)。

## 先用最少的配置

```yaml
sz:
  oss:
    provider: RUSTFS
    endpoint: http://192.168.1.100:9000
    addressing-style: PATH
    accessKey: your-access-key
    secretKey: your-secret-key
    bucketName: my-bucket
  resource:
    scenes:
      - code: admin.user.logo
        type: OSS
        serve-mode: DIRECT
```

`endpoint` 同时能被应用和浏览器访问时，以上配置即可生成 `http://192.168.1.100:9000/my-bucket/<对象名>`。若应用通过内部地址访问 OSS，而浏览器通过反向代理访问，再增加 `sz.oss.domain: https://files.example.com`。`domain` 只影响公开 URL，不改变 SDK 连接地址或预签名 URL。

OSS scene 的 `bucket` 默认继承 `sz.oss.bucketName`，`DIRECT` scene 的 `base-url` 默认根据 `domain`（若未配置则根据 `endpoint`）及最终 bucket 生成；scene 显式配置的值优先。`PROTECTED` / `PRESIGNED` scene 无需 `base-url`。`scheme` 和 `region` 有默认值；新示例显式写 `addressing-style: PATH` 表明 SDK 当前使用路径风格，旧 `force-path-style` 在未设置新字段时继续生效。公开访问还需要存储桶允许匿名读取。

scene `base-url` 支持完整 HTTP(S) 地址（如 `https://files.example.com/images`）和 `/` 开头同源路径（如 `/images`），两种形式都生效且优先级相同。显式值最高，直接使用，仅追加去掉 bucket 或 LOCAL path 前缀后的文件相对路径，不与 endpoint 拼接。同源路径由浏览器使用当前页面的协议、域名和端口，需配套路由或代理。OSS 独立连接未配置 `base-url` 时按自身 endpoint 推导，不继承全局 domain；LOCAL DIRECT 必须显式填写。两种形式只适用于 scene `base-url`，全局 domain 不用于配置同源路径。完整规则与代理示例见[base-url 的两种写法与优先级](oss-scene-connection-design.md#base-url-的两种写法与优先级)。

---

## 目录

- [MinIO（自建）](#minio自建)
- [RustFS（自建）](#rustfs自建)
- [最少配置与覆盖规则](#先用最少的配置)
- [阿里云 OSS](#阿里云-oss)
- [腾讯云 COS](#腾讯云-cos)
- [七牛云 Kodo](#七牛云-kodo)
- [配置边界：sz.oss vs sz.resource](#配置边界szoss-vs-szresource)
- [通用字段说明](#通用字段说明)
- [常见问题排查](#常见问题排查)
- [相关文件](#相关文件)

---

## MinIO（自建）

```yaml
sz:
  oss:
    provider: minio
    endpoint: http://192.168.1.100:9000
    addressing-style: PATH
    accessKey: your-access-key
    secretKey: your-secret-key
    bucketName: my-bucket
```

**关键说明：**
- MinIO 使用**路径风格**（`http://host:port/bucket/object`），无需配置 `force-path-style`，框架自动处理。
- `region` 默认 `us-east-1`；与服务端地域不同或要求严格签名时填写实际值。
- 若使用自定义域名（如反向代理），配置 `domain: http://your-proxy-host`。路径风格下不要包含 bucket，代码会按生效的 scene bucket 拼接。

---

## RustFS（自建）

```yaml
sz:
  oss:
    provider: RUSTFS
    endpoint: http://rustfs:9000
    addressing-style: PATH
    accessKey: your-access-key
    secretKey: your-secret-key
    bucketName: my-bucket
    domain: https://files.example.com
  resource:
    scenes:
      - code: admin.user.logo
        type: OSS
        serve-mode: DIRECT
```

RustFS 默认使用路径风格。`endpoint` 是应用访问 S3 API 的地址，`domain` 是浏览器访问公开对象的地址；公开 URL 形如 `https://files.example.com/my-bucket/object-key`。未配置 `domain` 时以 `endpoint` 生成公开 URL，若 `endpoint` 已带协议则沿用该协议。scene 可单独配置 `bucket` 或 `base-url`，显式值优先。公开访问还需要对应 bucket 的匿名读取策略；受保护资源应使用 `PROTECTED` 或 `PRESIGNED` 场景。

---

## 阿里云 OSS

```yaml
sz:
  oss:
    provider: aliyun
    # endpoint 示例值，按阿里云官方 S3 兼容文档填写
    endpoint: https://s3.oss-cn-hangzhou.aliyuncs.com
    accessKey: your-access-key-id
    secretKey: your-access-key-secret
    bucketName: my-bucket
```

**关键说明：**
- 使用**虚拟主机风格**（`https://my-bucket.<endpoint>`）。
- `endpoint` 按**阿里云官方 S3 兼容文档**填写；本项目未在阿里云环境实测，具体域名与是否需要 `s3.` 前缀以官方最新文档为准，上例中的 `s3.oss-cn-hangzhou.aliyuncs.com` 为示意值。
- **2025 年 3 月起**：中国内地地域禁止通过默认公开 endpoint 访问数据 API，生产环境必须配置自定义 CNAME 域名：
  ```yaml
  sz:
    oss:
      domain: https://your-cname.example.com
  ```
- `region` 参与 SDK 请求与预签名。请按实际 bucket 地域和所用 S3 兼容 endpoint 填写；本项目未在阿里云环境实测该配置组合。

---

## 腾讯云 COS

```yaml
sz:
  oss:
    provider: tencent
    endpoint: https://cos.ap-guangzhou.myqcloud.com
    accessKey: your-secret-id
    secretKey: your-secret-key
    bucketName: my-bucket-1234567890   # 腾讯云 bucket 名称含 AppID 后缀
    # 必须填写正确地域，否则 Presigned URL 返回 403
    region: ap-guangzhou
    # force-path-style 不填，自动使用虚拟主机风格（腾讯云默认）
```

**关键说明：**
- 使用**虚拟主机风格**（`https://my-bucket-1234567890.cos.ap-guangzhou.myqcloud.com`）。
- `region` **必须与 bucket 所在地域一致**，否则 Presigned URL 签名验证失败（403）。
- 腾讯云 bucket 名称格式：`{bucketName}-{AppID}`，如 `test-1250000000`。
- 常用地域值：`ap-beijing`、`ap-guangzhou`、`ap-shanghai`、`ap-chengdu`、`ap-singapore` 等。

---

## 七牛云 Kodo

```yaml
sz:
  oss:
    provider: qiniu
    endpoint: https://s3.cn-east-1.qiniucs.com
    accessKey: your-access-key
    secretKey: your-secret-key
    bucketName: my-bucket
    # 必须与 endpoint 中的地域一致，否则 Presigned URL 返回 403
    region: cn-east-1
    # 七牛云同时支持路径风格和虚拟主机风格，不填使用虚拟主机风格（推荐）
    # force-path-style: false
```

**关键说明：**
- `region` **必须与 endpoint 中的地域标识一致**，否则 Presigned URL 签名失败（403）。
- 常用地域 endpoint 对照：

  | 地域 | endpoint | region |
  |------|----------|--------|
  | 华东-浙江 | `s3.cn-east-1.qiniucs.com` | `cn-east-1` |
  | 华北-河北 | `s3.cn-north-1.qiniucs.com` | `cn-north-1` |
  | 华南-广东 | `s3.cn-south-1.qiniucs.com` | `cn-south-1` |
  | 北美-洛杉矶 | `s3.us-north-1.qiniucs.com` | `us-north-1` |

---

## 配置边界：sz.oss vs sz.resource

`sz-common-oss` 仅承担 S3 兼容厂商的接入能力；业务侧的"文件上传"能力由 `sz-common-resource` 对外提供。

- **`sz.oss.*`**：S3 兼容厂商接入凭据（`endpoint` / `accessKey` / `secretKey` / 默认 `bucketName` / 签名 `region` / 自定义 `domain`）。
- **`sz.resource.*`**：业务资源体系（场景定义、命名策略 `naming`、扩展名白名单 `exts`、路径 `path`、存储类型 `LOCAL` / `OSS` 分发）。

业务代码通过 `ResourceService` 上传；`sz-common-oss` 作为底层 S3 驱动，由 `sz-common-resource` 的 `OssResourceStorageDriver` 调用。

仅配置 `sz.oss.*` 而不配置 `sz.resource.*` 不是标准用法：业务仍需在 `sz.resource.scenes[]` 中声明 scene code 和存储类型。OSS scene 的 `bucket` 未配置时继承 `sz.oss.bucketName`；`DIRECT` scene 的 `base-url` 未配置时按 `sz.oss.domain`（若无则按 `endpoint`）和生效 bucket 生成。场景级配置始终优先。

---

## 通用字段说明

| 字段 | 说明 | 默认值 |
|------|------|--------|
| `provider` | 厂商标识（minio / rustfs / aliyun / tencent / qiniu），大小写均可；项目内 yml 使用大写枚举值 | `MINIO` |
| `endpoint` | 应用连接 S3 API 的地址；建议写完整 `http://` 或 `https://` | — |
| `accessKey` | Access Key ID / SecretID | — |
| `secretKey` | Access Key Secret / SecretKey | — |
| `bucketName` | 默认存储桶；scene 未指定 bucket 时继承 | — |
| `scheme` | 仅为未带协议的 `endpoint` / `domain` 补协议；完整 URL 无需填写 | `https` |
| `region` | AWS 签名 Region；腾讯云、七牛云必须填写实际地域 | `us-east-1` |
| `addressing-style` | 推荐显式配置 `PATH` 或 `VIRTUAL_HOSTED`，同时影响 SDK 请求和预签名；scene 可覆盖 | 未设时按旧 `force-path-style`，再按 provider 推导 |
| `force-path-style` | 旧布尔配置，仅在 `addressing-style` 未设时生效；两者冲突会启动失败 | `null`（自动） |
| `domain` | 浏览器公开访问地址与 `endpoint` 不同时填写；路径风格下不含 bucket | — |

`provider` 仅决定未显式指定风格时的默认值：MinIO / RustFS 为路径风格，其余厂商为虚拟主机风格。公开 `domain` 与 SDK `endpoint` 分工不同。虚拟主机风格的 `domain` 如含 `{bucket}`，会用实际桶名替换；无占位符时按已指向某个桶的完整地址处理，换桶的 scene 应配置自己的 `base-url`。

> scene 使用另一套 S3 连接时，直接在 scene 顶层成组填写 `endpoint`、`access-key`、`secret-key`；只改 `bucket`、`region`、`addressing-style` 或 `base-url` 时无需重复凭据。`region` 单独覆盖只改变签名区域，不切换 endpoint。旧 `sz.oss.instances` 与 `scenes[].oss-instance` 已移除，迁移示例见[新方案](oss-scene-connection-design.md)。

---

## 常见问题排查

### Presigned URL 请求返回 403

**最常见原因：region 填写错误。**

检查步骤：
1. 腾讯云：`region` 必须填写 `ap-guangzhou`、`ap-beijing` 等正确地域
2. 七牛云：`region` 必须与 endpoint 中的地域一致（如 `cn-east-1`）
3. 查看日志中 Presigned URL 的 `X-Amz-Credential` 字段，确认其中的 region 部分是否正确

### 阿里云 OSS 上传返回 403

可能原因：
1. `endpoint` 使用了与阿里云官方 S3 兼容文档不一致的地址
2. 2025 年 3 月后，中国内地地域需要配置自定义 CNAME 域名

### 启动报错：Configuration property name 'oss.xxx' is not valid

检查 yaml 根节点是否为 `sz.oss:`（两级嵌套）。历史版本曾使用根级 `oss:` 前缀，已在重构中统一迁移为 `sz.oss`。

### MinIO URL 拼接出现协议重复（`https://http://...`）

此为旧版本 Bug，已在当前版本修复。

若 `endpoint` 含协议前缀（如 `http://192.168.1.100:9000`），`scheme` 字段对 endpoint 连接无效。未配置 `domain` 时公开 URL 也沿用 endpoint 的协议；`domain` 未带协议时使用 `scheme`。

### 上传文件类型被拦截

扩展名与 MIME 类型校验已迁移至 `sz-common-resource`，不再由 `sz-common-oss` 承担。请配置 `sz.resource.security.allowed-exts`（全局白名单）或 `sz.resource.scenes[].exts`（场景级白名单）。

### bucket 为空导致 SDK 报错

旧版本中存在 bucket 参数传入空串的 Bug，已在当前版本修复：
- `OssClient` 的 bucket 参数若为 `null` 或空串，自动回退到 `sz.oss.bucketName` 配置值；公开 URL 入口也使用相同规则。
- `sz.resource.scenes[]` 的 OSS scene 未设置 `bucket` 时，启动校验阶段会填充 `sz.oss.bucketName`；两处都为空则启动失败。

---

## 相关文件

- `sz-common/sz-common-oss/src/main/java/com/sz/oss/OssProperties.java` — 配置字段定义与字段级 javadoc
- `sz-common/sz-common-oss/src/main/resources/application.yml` — 模块默认配置（starter 兜底值）
- `config/{local,dev,preview,prod}/oss.yml` — 各环境实际覆盖
- `sz-common/sz-common-resource/` — 业务资源体系（场景、命名、白名单、路径策略）
