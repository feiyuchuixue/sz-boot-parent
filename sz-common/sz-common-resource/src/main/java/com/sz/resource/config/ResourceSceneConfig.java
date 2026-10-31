package com.sz.resource.config;

import com.sz.oss.OssAddressingStyle;
import com.sz.oss.OssProviderEnum;
import com.sz.oss.OssSceneConnection;
import com.sz.resource.enums.NamingRuleEnum;
import com.sz.resource.enums.PathStrategyEnum;
import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.enums.StorageTypeEnum;
import lombok.Data;
import org.springframework.util.unit.DataSize;

import java.util.Set;

/**
 * 单个资源场景配置
 *
 * <pre>
 * sz:
 *   resource:
 *     root: ./data
 *     scenes:
 *       - code: sso.provider.logo
 *         type: LOCAL
 *         serve-mode: DIRECT
 *         path: providers/
 *         base-url: http://127.0.0.1:5000/api/admin/resource/file/providers
 *         naming: BIZ_KEY
 *         exts: [svg, png, jpg, jpeg, webp]
 *         max-size: 2MB
 *
 *       - code: user.avatar
 *         type: LOCAL
 *         serve-mode: DIRECT
 *         path: avatars/
 *         base-url: http://127.0.0.1:5000/api/admin/resource/file/avatars
 *         naming: UUID
 *         path-strategy: BIZ_DATE
 *         exts: [png, jpg, jpeg, webp, gif]
 *         max-size: 5MB
 * </pre>
 */
@Data
public class ResourceSceneConfig {

    /**
     * 场景唯一编码，用于上传、读取、URL 解析和 OSS 客户端缓存。静态 scenes 中必须非空白且不可重复；运行时找不到对应 code 会报
     * 场景不存在。
     *
     * @schemaExample user.avatar | 用户头像场景编码
     */
    private String code;

    /**
     * 场景显示名称，仅承载可读标签；可不配置，未配置时场景仍由 code 标识，不影响存储路由和安全校验。
     *
     * @schemaExample 用户头像 | 场景的可读名称
     */
    private String name;

    /**
     * 场景存储类型。YAML 省略或空值时保留本字段的初始化值，不读取 ResourceProperties.defaultStorageType。LOCAL
     * 写入服务器 root 下， 必须配置合法 path，且禁止 bucket、addressing-style、独立连接、provider、region 等
     * OSS 专用字段；OSS 委托 S3 兼容驱动， bucket 可继承全局 bucket-name，逻辑 objectKey 含 bucket 前缀而
     * SDK 物理 key 会剥离该前缀。
     */
    private StorageTypeEnum type = StorageTypeEnum.LOCAL;

    /**
     * 场景访问地址生成模式。YAML 省略或空值时保留本字段的初始化值。该字段只决定 ResourceService 返回地址的方式，不创建或 放开 HTTP
     * 路由，也不自动实施登录、权限、数据范围或资源归属校验；调用方和部署环境必须自行保证相应访问策略。PRESIGNED 仅允许 type=OSS，LOCAL
     * 与 PRESIGNED 组合会在场景校验时报错。
     */
    private ServeModeEnum serveMode = ServeModeEnum.PROTECTED;

    /**
     * PRESIGNED 临时 URL 的有效期，单位秒。YAML 省略、null 或精确空串时保留本字段的初始化值；只含空白字符的非空字符串会 转换失败。仅
     * serve-mode=PRESIGNED 时用于签名；场景校验要求大于 0，当前 AWS SDK 的 SigV4 预签名要求 1 至 604800 秒
     * （7 天），超出上限在生成签名时报错。DIRECT 和 PROTECTED 不使用该值。字符串绑定支持移除 Java
     * Character.isWhitespace 空白后的正负十进制，以及 0x/0X/# 十六进制形式；范围和溢出由 Spring Binder 检查。
     *
     * @schemaExample 3600 | 一小时有效期
     */
    private Long expire = 3600L;

    /**
     * LOCAL 场景的基础目录，也是逻辑 objectKey 前缀，相对于全局 root。type=LOCAL 时必须非空白并通过配置路径校验：不超过 512
     * 字符，禁止 null 字节、反斜杠、父目录引用 ..、以 / 开头的绝对路径、./ 当前目录段、连续 //、#?& 和以点开头的
     * 隐藏目录段。最终物理路径为 root/objectKey。OSS 场景使用 bucket 作为基础前缀，不使用 path。
     *
     * @schemaExample avatars/ | 用户头像的本地基础目录
     */
    private String path;

    /**
     * DIRECT 模式的完整直连地址前缀，可为 HTTP(S) 地址或以 / 开头的同源路径。显式场景值优先级最高，直接使用，不再追加或 改写为 global
     * domain、endpoint、bucket 或 LOCAL path。仅当 objectKey 以场景 path/bucket
     * 加斜杠开头时，生成地址才去除 该基础前缀。OSS 的前缀为空或不匹配时容错追加原 objectKey；LOCAL 会先校验 objectKey
     * 必须属于场景 path，跨场景 key 会 报错。LOCAL DIRECT 必须显式配置；OSS DIRECT 未配置时，默认连接按 global
     * domain 优先、endpoint 兜底推导，独立连接只按 自身 endpoint 推导。PROTECTED 和 PRESIGNED
     * 不使用本字段。本字段只决定返回的地址字符串，不创建可访问路由或修改存储 访问权限。
     *
     * @schemaExample /api/admin/resource/file/avatars | 使用应用同源直连地址前缀
     * @schemaExample https://files.example.com/avatars | 使用独立静态域名直连地址前缀
     */
    private String baseUrl;

    /**
     * OSS 场景的存储桶名称；为空白时继承全局 bucket-name，两者都为空白会校验失败。它同时作为 SDK bucket 参数和逻辑
     * objectKey 的一级前缀；调用 S3 SDK 时驱动会从 objectKey 剥离该 bucket 前缀得到物理 key。该值执行与 path
     * 相同的配置路径 校验：不超过 512 字符，禁止 null 字节、反斜杠、..、绝对路径、点目录段、连续 // 和 #?&。LOCAL
     * 场景禁止配置本字段。
     *
     * @schemaExample ${OSS_BUCKET:sz-admin} | 使用环境变量配置场景存储桶
     */
    private String bucket;

    /**
     * 场景级 S3 寻址风格。本值优先于全局 addressing-style、旧 force-path-style 和生效 provider 判定值；但全局
     * addressing-style 与 force-path-style 的冲突会先被检查，不能用场景值绕过。LOCAL 场景禁止配置本字段。
     */
    private OssAddressingStyle addressingStyle;

    /**
     * 场景独立 S3 API 端点。endpoint、access-key、secret-key 任一项绑定为非 null 字符串（包括空串或空白字符串）即触发
     * 独立连接，三项随后都必须为非空白值；仅三项绑定结果均为 null 时继承全局 endpoint 和凭据。YAML 应省略三项以继承，显式
     * null、空串或空白字符串都会触发独立连接并校验失败。provider 或 region 单独覆盖不触发独立连接。完整 HTTP(S) 地址沿用
     * 自身协议，未带协议时由全局 scheme 补全。独立连接推导 DIRECT 直连地址时只使用本 endpoint，不使用全局 domain。全局
     * sz.oss.endpoint 控制 S3 客户端配置是否装配，仅配置本字段不会独立启用 OSS。LOCAL 场景禁止配置本字段。
     *
     * @schemaExample ${SCENE_OSS_ENDPOINT:http://127.0.0.1:9000} | 使用环境变量配置场景独立端点
     */
    private String endpoint;

    /**
     * 场景独立 S3 连接的访问密钥 ID。endpoint、access-key、secret-key 任一项绑定为非 null
     * 字符串（包括空串或空白字符串） 即触发独立连接，三项随后都必须为非空白值；仅三项绑定结果均为 null 时继承全局连接。YAML 应省略三项以继承，显式
     * null、 空串或空白字符串都会触发独立连接并校验失败。provider 或 region 单独覆盖不触发独立连接。LOCAL 场景禁止配置本字段。
     *
     * @schemaExample ${SCENE_OSS_ACCESS_KEY} | 从环境变量读取场景访问密钥 ID
     */
    private String accessKey;

    /**
     * 场景独立 S3 连接的访问密钥密码。endpoint、access-key、secret-key 任一项绑定为非 null 字符串（包括空串或空白字符串）
     * 即触发独立连接，三项随后都必须为非空白值；仅三项绑定结果均为 null 时继承全局连接。YAML 应省略三项以继承，显式 null、
     * 空串或空白字符串都会触发独立连接并校验失败。provider 或 region 单独覆盖不触发独立连接。LOCAL 场景禁止配置本字段。
     *
     * @schemaExample ${SCENE_OSS_SECRET_KEY} | 从环境变量读取场景访问密钥密码
     */
    private String secretKey;

    /**
     * 场景级对象存储提供商覆盖，仅用于该场景的寻址风格判定和直连地址推导。未配置时继承全局 provider。可单独配置而继续继承 全局 endpoint
     * 和凭据，不会自行切换 endpoint；LOCAL 场景禁止配置。
     */
    private OssProviderEnum provider;

    /**
     * 场景级 AWS S3 签名 Region，影响该场景客户端签名和 Presigned URL，不会切换 endpoint。未配置或为空白时继承全局
     * region，全局值仍为空白时最终回退 us-east-1。可单独配置而继续继承全局 endpoint 和凭据；LOCAL 场景禁止配置。
     *
     * @schemaExample us-east-1 | S3 兼容服务常用签名区域
     */
    private String region;

    public OssSceneConnection toOssSceneConnection() {
        if (endpoint == null && accessKey == null && secretKey == null && provider == null && region == null) {
            return null;
        }
        OssSceneConnection connection = new OssSceneConnection();
        connection.setEndpoint(endpoint);
        connection.setAccessKey(accessKey);
        connection.setSecretKey(secretKey);
        connection.setProvider(provider);
        connection.setRegion(region);
        return connection;
    }

    /**
     * 上传文件命名规则。YAML 省略或空值时保留本字段的初始化值。BIZ_KEY、UUID、TIMESTAMP 使用原文件最后一段扩展名的小写
     * 形式，无扩展名时使用 bin；ORIGINAL 使用清洗后的完整原文件名。
     */
    private NamingRuleEnum naming = NamingRuleEnum.UUID;

    /**
     * objectKey 的路径分层策略。YAML 省略或空值时保留本字段的初始化值。pathSegments 中仅非空白元素参与分层，参与的元素会 清洗为
     * ASCII 字母、数字、下划线和连字符组成的目录段。基础前缀由存储类型决定：LOCAL 使用 path，OSS 使用 bucket。
     */
    private PathStrategyEnum pathStrategy = PathStrategyEnum.FLAT;

    /**
     * 场景级允许扩展名集合，值应使用小写且不带点。非空时在全局 allowed-exts 之后再次检查，只能进一步收窄全局白名单；未配置
     * 或空集合时不增加场景限制。硬编码危险扩展名黑名单始终优先，不能通过本字段覆盖。
     */
    private Set<String> exts;

    /**
     * 场景级 MIME 白名单，值应使用小写标准 MIME。非空时优先并替代全局 allowed-mime-types，而不是与全局集合取交集；未配置
     * 或空集合时回退全局 MIME 白名单。客户端 MIME 非空白时，将传入的完整值转为小写后与生效集合精确匹配，不解析或去除 charset
     * 等参数；客户端未提供 MIME 或值为空白时跳过 MIME 校验。
     */
    private Set<String> mimes;

    /**
     * 场景单文件大小上限，绑定为 Spring DataSize。支持裸字节整数或整数串，以及精确大写 B、KB、MB、GB、TB；字符串内 Java
     * Character.isWhitespace 空白会移除，KB 及以上按 1024 进制换算。配置且换算后大于 0 时，与全局 max-size
     * 取较小值；未配置 或小于等于 0 时不增加场景上限，但仍受全局上限约束。
     *
     * @schemaExample 2MB | 小型图片场景上限
     * @schemaExample 5MB | 普通图片场景上限
     */
    private DataSize maxSize;
}
