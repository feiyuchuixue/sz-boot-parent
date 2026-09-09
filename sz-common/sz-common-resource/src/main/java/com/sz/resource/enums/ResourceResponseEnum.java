package com.sz.resource.enums;

import com.sz.core.common.enums.ErrorPrefixEnum;
import com.sz.core.common.enums.ResponseEnumTemplate;

/**
 * resource 模块业务异常枚举
 *
 * <p>
 * 响应码格式：前缀 {@code R} + code，例如 {@code R1}、{@code R2}。
 * </p>
 */
public enum ResourceResponseEnum implements ResponseEnumTemplate<ResourceResponseEnum> {

    // @formatter:off
    /** 文件类型命中黑名单，禁止上传 */
    BLOCKED_EXT(1, "文件类型在黑名单中，禁止上传"),
    /** 检测到双重扩展名攻击 */
    DOUBLE_EXT_ATTACK(2, "检测到双重扩展名，禁止上传"),
    /** 文件类型不在全局白名单中 */
    GLOBAL_EXT_NOT_ALLOWED(3, "文件类型不在全局白名单中"),
    /** 文件类型不在场景白名单中 */
    SCENE_EXT_NOT_ALLOWED(4, "不支持的文件类型"),
    /** MIME 类型不允许 */
    MIME_NOT_ALLOWED(5, "不支持的 MIME 类型"),
    /** 文件大小超限 */
    FILE_SIZE_EXCEEDED(6, "文件大小超限"),
    /** 上传文件为空 */
    FILE_EMPTY(7, "上传文件不能为空"),
    /** 未找到资源场景配置 */
    SCENE_NOT_FOUND(8, "未找到资源场景配置"),
    /** namingKey 不能为空（BIZ_KEY 命名规则） */
    NAMING_KEY_REQUIRED(9, "命名规则为 BIZ_KEY 时，namingKey 不能为空"),
    /** 无法将完整 URL 规范化为 objectKey */
    OBJECT_KEY_NORMALIZE_FAILED(10, "无法将值规范化为 objectKey，请勿提交完整 URL"),
    /** 路径分段不合法 */
    INVALID_PATH_SEGMENT(11, "路径分段不合法"),
    ;
    // @formatter:on

    private final int code;

    private final String message;

    ResourceResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public ErrorPrefixEnum getCodePrefixEnum() {
        return ErrorPrefixEnum.RESOURCE;
    }
}
