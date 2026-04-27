package com.sz.resource.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 路径安全校验工具
 *
 * <p>
 * 提供统一入口 {@link #validate(String, Mode)} 对路径值进行安全校验， 覆盖路径穿越攻击的常见变体，包括原始字符和 URL
 * 编码形式。
 *
 * <h3>校验规则（所有模式通用）</h3>
 * <ul>
 * <li>值不能为 null 或空白</li>
 * <li>不能包含 {@code ..}（父目录引用）</li>
 * <li>不能包含 {@code /./} 或以 {@code ./} 开头（当前目录引用）</li>
 * <li>不能以 {@code /} 开头（绝对路径）</li>
 * <li>不能包含 {@code \}（Windows 反斜杠）</li>
 * <li>不能包含 null 字节 {@code \0}</li>
 * <li>路径总长度不能超过 512 字符</li>
 * <li>不能包含连续斜杠 {@code //}</li>
 * <li>不能包含 URL 特殊字符（{@code #}、{@code ?}、{@code &}）</li>
 * <li>不能包含隐藏文件或目录（以 {@code .} 开头的路径段）</li>
 * </ul>
 *
 * <h3>模式差异</h3>
 * <ul>
 * <li>{@link Mode#OBJECT_KEY} - objectKey 校验，直接对原始值校验，校验失败抛
 * {@link IllegalArgumentException}</li>
 * <li>{@link Mode#HTTP_PATH} - HTTP 请求路径校验，先做 URL decode 再校验，返回
 * boolean（不抛异常）</li>
 * <li>{@link Mode#CONFIG} - YAML 配置值校验，直接对原始值校验，校验失败抛
 * {@link IllegalStateException}</li>
 * </ul>
 *
 * <h3>典型调用示例</h3>
 * 
 * <pre>{@code
 * // objectKey 校验（存储驱动内部调用）
 * PathSanitizer.validate(objectKey, PathSanitizer.Mode.OBJECT_KEY);
 *
 * // HTTP 路径校验（Controller 内调用，返回 false 表示不安全）
 * if (!PathSanitizer.validate(subPath, PathSanitizer.Mode.HTTP_PATH)) {
 *     return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
 * }
 *
 * // 配置值校验（@PostConstruct 内调用，启动时 fail-fast）
 * PathSanitizer.validate(scene.getPath(), PathSanitizer.Mode.CONFIG);
 * }</pre>
 */
public final class PathSanitizer {

    private PathSanitizer() {
    }

    /**
     * 校验模式
     */
    public enum Mode {
        /**
         * objectKey 校验：严格校验原始值，失败抛 {@link IllegalArgumentException}
         */
        OBJECT_KEY,

        /**
         * HTTP 请求路径校验：先做 URL decode 再校验，返回 boolean，不抛异常
         */
        HTTP_PATH,

        /**
         * YAML 配置值校验：校验原始值，失败抛 {@link IllegalStateException}（启动 fail-fast）
         */
        CONFIG
    }

    /**
     * 统一路径安全校验入口
     *
     * <ul>
     * <li>{@link Mode#OBJECT_KEY} — 校验失败抛
     * {@link IllegalArgumentException}，返回值无意义</li>
     * <li>{@link Mode#HTTP_PATH} — 不抛异常，返回 {@code true} 表示安全，{@code false}
     * 表示不安全</li>
     * <li>{@link Mode#CONFIG} — 校验失败抛 {@link IllegalStateException}，返回值无意义</li>
     * </ul>
     *
     * @param value
     *            待校验的路径值
     * @param mode
     *            校验模式
     * @return HTTP_PATH 模式下：true=安全，false=不安全；其他模式始终返回 true（失败时已抛异常）
     */
    public static boolean validate(String value, Mode mode) {
        return switch (mode) {
            case OBJECT_KEY -> {
                doValidateObjectKey(value);
                yield true;
            }
            case HTTP_PATH -> doValidateHttpPath(value);
            case CONFIG -> {
                doValidateConfig(value);
                yield true;
            }
        };
    }

    /**
     * objectKey 校验：对原始值做严格检查
     */
    private static void doValidateObjectKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("objectKey 不能为空");
        }
        String reason = findViolation(value);
        if (reason != null) {
            throw new IllegalArgumentException("非法 objectKey [" + reason + "]：" + value);
        }
    }

    /**
     * HTTP 路径校验：先 URL decode，再检查原始值和解码值
     *
     * <p>
     * 同时检查 decode 前后两次，防止双重编码攻击（如 {@code %252e%252e} decode 一次得 {@code %2e%2e}）。
     */
    private static boolean doValidateHttpPath(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        // 检查原始值
        if (findViolation(value) != null) {
            return false;
        }
        // URL decode 后再检查
        try {
            String decoded = URLDecoder.decode(value, StandardCharsets.UTF_8);
            if (!decoded.equals(value) && findViolation(decoded) != null) {
                return false;
            }
        } catch (IllegalArgumentException ignored) {
            // decode 失败（如含非法 % 序列），视为不安全
            return false;
        }
        return true;
    }

    /**
     * 配置值校验：对原始值做检查，失败时抛启动异常
     */
    private static void doValidateConfig(String value) {
        if (value == null || value.isBlank()) {
            // 配置值为空由调用方决定是否允许，此处不做 null 检查
            return;
        }
        String reason = findViolation(value);
        if (reason != null) {
            throw new IllegalStateException("配置路径包含非法字符 [" + reason + "]，请检查配置：" + value);
        }
    }

    /**
     * 核心校验逻辑：检查路径值是否包含非法模式
     *
     * @param value
     *            待检查的值（非 null）
     * @return 违规原因描述；{@code null} 表示安全
     */
    /**
     * 路径最大长度限制
     */
    private static final int MAX_PATH_LENGTH = 512;

    private static String findViolation(String value) {
        // null 字节
        if (value.indexOf('\0') >= 0) {
            return "包含 null 字节";
        }
        // 路径长度限制
        if (value.length() > MAX_PATH_LENGTH) {
            return "路径长度超过 " + MAX_PATH_LENGTH + " 字符";
        }
        // Windows 反斜杠
        if (value.contains("\\")) {
            return "包含反斜杠";
        }
        // 父目录引用
        if (value.contains("..")) {
            return "包含父目录引用 ..";
        }
        // 以绝对路径 / 开头
        if (value.startsWith("/")) {
            return "以 / 开头（绝对路径）";
        }
        // 当前目录引用：开头 ./ 或 中间 /./
        if (value.startsWith("./") || value.contains("/./")) {
            return "包含当前目录引用 ./";
        }
        // 连续斜杠
        if (value.contains("//")) {
            return "包含连续斜杠 //";
        }
        // URL 特殊字符（不应出现在存储路径中）
        if (value.indexOf('#') >= 0 || value.indexOf('?') >= 0 || value.indexOf('&') >= 0) {
            return "包含 URL 特殊字符（#?&）";
        }
        // 隐藏文件/目录（以 . 开头的路径段）
        if (value.startsWith(".") || value.contains("/.")) {
            return "包含隐藏文件或目录（.开头的路径段）";
        }
        return null;
    }
}
