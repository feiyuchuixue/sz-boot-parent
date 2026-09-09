package com.sz.resource.enums;

import com.sz.core.common.enums.ErrorPrefixEnum;
import com.sz.core.common.enums.ResponseEnumTemplate;
import org.springframework.http.HttpStatus;

/**
 * 受保护资源访问异常。
 */
public enum ResourceAccessResponseEnum implements ResponseEnumTemplate<ResourceAccessResponseEnum> {

    ACCESS_DENIED(2001, "无权访问该资源", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND(2002, "业务记录或资源不存在", HttpStatus.NOT_FOUND),
    RESOURCE_REFERENCE_INVALID(2003, "资源引用无效", HttpStatus.BAD_REQUEST),
    RESOURCE_REFERENCE_SCENE_MISMATCH(2004, "资源场景不匹配", HttpStatus.BAD_REQUEST),
    RESOURCE_REFERENCE_OWNER_MISMATCH(2005, "新增资源不属于当前用户", HttpStatus.FORBIDDEN),
    PREVIEW_NOT_ALLOWED(2006, "该文件类型不允许预览", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    STORAGE_READ_FAILED(2007, "资源读取失败", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;

    private final String message;

    private final HttpStatus httpStatus;

    ResourceAccessResponseEnum(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public ErrorPrefixEnum getCodePrefixEnum() {
        return ErrorPrefixEnum.RESOURCE;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
