# OSS 对象存储厂商配置指南

本文档说明如何在 `application.yml` 中为不同云存储厂商正确配置 `sz-common-oss`。配置前缀为 `sz.oss`；本文档所有 yaml 示例均以此为根节点。

---

## 目录

- [MinIO（自建）](#minio自建)
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
    accessKey: your-access-key
    secretKey: your-secret-key
    bucket-name: my-bucket
    scheme: http
    region: us-east-1        # MinIO 不校验 region，任意值均可
    # force-path-style 不填，自动启用路径风格（MinIO 默认）
```

**关键说明：**
- MinIO 使用**路径风格**（`http://host:port/bucket/object`），无需配置 `force-path-style`，框架自动处理。
- `region` 填任意值均可，MinIO 不校验。
- 若使用自定义域名（如反向代理），配置 `domain: http://your-proxy-host/my-bucket`，需包含 bucket 名称。

---

## 阿里云 OSS

```yaml
sz:
  oss:
    provider: aliyun
    # endpoint 示例值，按阿里云官方 S3 兼容文档填写
    endpoint: s3.oss-cn-hangzhou.aliyuncs.com
    accessKey: your-access-key-id
    secretKey: your-access-key-secret
    bucket-name: my-bucket
    scheme: https
    region: us-east-1        # 阿里云 OSS 不校验 region，任意值均可
    # force-path-style 不填，自动使用虚拟主机风格（阿里云默认）
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
- `region` 填任意值均可，OSS 签名算法不校验 region。

---

## 腾讯云 COS

```yaml
sz:
  oss:
    provider: tencent
    # endpoint 不含协议前缀
    endpoint: cos.ap-guangzhou.myqcloud.com
    accessKey: your-secret-id
    secretKey: your-secret-key
    bucket-name: my-bucket-1234567890   # 腾讯云 bucket 名称含 AppID 后缀
    scheme: https
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
    # endpoint 为 S3 兼容 endpoint，不含协议前缀
    endpoint: s3.cn-east-1.qiniucs.com
    accessKey: your-access-key
    secretKey: your-secret-key
    bucket-name: my-bucket
    scheme: https
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

- **`sz.oss.*`**：S3 兼容厂商接入凭据（`endpoint` / `accessKey` / `secretKey` / 默认 `bucket-name` / 签名 `region` / 自定义 `domain`）。
- **`sz.resource.*`**：业务资源体系（场景定义、命名策略 `naming`、扩展名白名单 `exts`、路径 `path`、存储类型 `LOCAL` / `OSS` 分发）。

业务代码通过 `ResourceService` 上传；`sz-common-oss` 作为底层 S3 驱动，由 `sz-common-resource` 的 `OssResourceStorageDriver` 调用。

仅配置 `sz.oss.*` 而不配置 `sz.resource.*` 不是标准用法：OSS 场景需在 `sz.resource.scenes[]` 中声明对应 `bucket`、`naming`、`exts` 等。

---

## 通用字段说明

| 字段 | 说明 | 默认值 |
|------|------|--------|
| `provider` | 厂商标识（minio / aliyun / tencent / qiniu），大小写均可；项目内 yml 统一使用大写枚举值（`MINIO` / `ALIYUN` / `TENCENT` / `QIUNIU`） | `MINIO` |
| `endpoint` | S3 兼容服务 endpoint（可含或不含协议前缀；含协议前缀时 `scheme` 仅影响公开 URL 拼接） | — |
| `accessKey` | Access Key ID / SecretID | — |
| `secretKey` | Access Key Secret / SecretKey | — |
| `bucket-name` | 默认存储桶名称；上传时未指定 bucket 则使用此值 | — |
| `scheme` | 协议（http / https） | `https` |
| `region` | AWS 签名 Region；腾讯云、七牛云必须填写实际地域 | `us-east-1` |
| `force-path-style` | 强制路径风格（true / false / 不填=自动，provider=minio 时为 true） | `null`（自动） |
| `domain` | 自定义 CDN / CNAME 域名（可选） | — |

> 命名策略（`naming`）、扩展名白名单（`exts`）、目录策略（`path-strategy`）、场景级 `bucket` 等属 `sz-common-resource` 的 `sz.resource.scenes[]` 配置，不在本表范围。

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

若 `endpoint` 含协议前缀（如 `http://192.168.1.100:9000`），`scheme` 字段对 endpoint 连接无效，仅影响公开 URL 拼接的协议部分。

### 上传文件类型被拦截

扩展名与 MIME 类型校验已迁移至 `sz-common-resource`，不再由 `sz-common-oss` 承担。请配置 `sz.resource.security.allowed-exts`（全局白名单）或 `sz.resource.scenes[].exts`（场景级白名单）。

### bucket 为空导致 SDK 报错

旧版本中存在 bucket 参数传入空串的 Bug，已在当前版本修复：
- `OssClient` 所有方法的 bucket 参数若为 `null` 或空串，自动回退到 `sz.oss.bucket-name` 配置值。
- `OssResourceStorageDriver.resolveBucket()` 已修正为返回 `null`（而非空串）触发回退逻辑。

---

## 相关文件

- `sz-common/sz-common-oss/src/main/java/com/sz/oss/OssProperties.java` — 配置字段定义与字段级 javadoc
- `sz-common/sz-common-oss/src/main/resources/application.yml` — 模块默认配置（starter 兜底值）
- `config/{local,dev,preview,prod}/oss.yml` — 各环境实际覆盖
- `sz-common/sz-common-resource/` — 业务资源体系（场景、命名、白名单、路径策略）
