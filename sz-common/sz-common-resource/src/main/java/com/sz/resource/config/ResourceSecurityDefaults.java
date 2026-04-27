package com.sz.resource.config;

import java.util.Set;

/**
 * 资源安全校验默认值（硬编码常量）
 *
 * <p>
 * 三层安全校验模型：
 * <ol>
 * <li><b>硬编码黑名单</b>（{@link #BLOCKED_EXTS}）— 不可覆盖，永远拒绝</li>
 * <li><b>全局白名单</b>（{@code security.allowed-exts}）— 可配置，未配时使用
 * {@link #DEFAULT_ALLOWED_EXTS}</li>
 * <li><b>场景白名单</b>（{@code scene.exts}）— 可选，进一步收窄允许范围</li>
 * </ol>
 *
 * <p>
 * 校验顺序：黑名单拒绝 → 全局白名单过滤 → 场景白名单过滤 → MIME 校验 → 大小校验
 */
public final class ResourceSecurityDefaults {

    private ResourceSecurityDefaults() {
    }

    /**
     * 硬编码黑名单扩展名（不可覆盖）
     * <p>
     * 包含可执行文件、脚本文件、服务端脚本等危险类型，无论任何配置都不允许上传。
     */
    public static final Set<String> BLOCKED_EXTS = Set.of("exe", "bat", "cmd", "sh", "bash", "ps1", "php", "jsp", "jspx", "asp", "aspx", "cgi", "jar", "war",
            "class", "dll", "so", "msi", "com", "scr", "pif", "vbs", "wsf", "reg");

    /**
     * 内置默认白名单扩展名（{@code security.allowed-exts} 未配时使用）
     * <p>
     * 覆盖常见的图片、文档、压缩包、音视频格式。
     */
    public static final Set<String> DEFAULT_ALLOWED_EXTS = Set.of(
            // 图片
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico",
            // 文档
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "rtf", "txt", "csv", "odt", "ods", "odp", "pages", "numbers", "keynote",
            // 压缩包
            "zip", "rar", "7z", "tar", "gz",
            // 音视频
            "mp3", "wav", "ogg", "mp4", "mov", "avi", "wmv");

    /**
     * 内置默认 MIME 白名单（{@code security.allowed-mime-types} 未配时使用）
     * <p>
     * 与 {@link #DEFAULT_ALLOWED_EXTS} 对应的 MIME 类型。
     */
    public static final Set<String> DEFAULT_ALLOWED_MIMES = Set.of(
            // 图片
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp", "image/svg+xml", "image/x-icon", "image/vnd.microsoft.icon",
            // 文档
            "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/rtf", "text/plain", "text/csv",
            "application/vnd.oasis.opendocument.text", "application/vnd.oasis.opendocument.spreadsheet", "application/vnd.oasis.opendocument.presentation",
            // 压缩包
            "application/zip", "application/x-rar-compressed", "application/x-7z-compressed", "application/x-tar", "application/gzip",
            // 音视频
            "audio/mpeg", "audio/wav", "audio/ogg", "video/mp4", "video/quicktime", "video/x-msvideo", "video/x-ms-wmv",
            // 通用二进制流（部分浏览器上传时使用）
            "application/octet-stream");

    /**
     * 全局默认最大文件大小（字节）：50MB
     */
    public static final long DEFAULT_MAX_SIZE_BYTES = 50L * 1024 * 1024;
}
