package com.sz.core.common.enums;

import com.sz.core.common.exception.common.BusinessExceptionCustomAssert;

/**
 * 通用响应枚举模板接口，提供线程安全的message方法
 * 
 * @param <E>
 *            枚举类型
 */
public interface ResponseEnumTemplate<E extends Enum<E> & ResponseEnumTemplate<E>> extends BusinessExceptionCustomAssert {

    class CustomMessageWrapper<T extends Enum<T> & ResponseEnumTemplate<T>> implements BusinessExceptionCustomAssert {

        private final T original;

        private final int customCode;

        private final String customMessage;

        public CustomMessageWrapper(T original, int customCode, String customMessage) {
            this.original = original;
            this.customCode = customCode;
            this.customMessage = customMessage;
        }

        @Override
        public int getCode() {
            return this.customCode;
        }

        @Override
        public String getMessage() {
            return this.customMessage;
        }

        @Override
        public ErrorPrefixEnum getCodePrefixEnum() {
            return original.getCodePrefixEnum();
        }
    }

    @SuppressWarnings("unchecked")
    default BusinessExceptionCustomAssert message(int code, String message) {
        return new CustomMessageWrapper<>((E) this, code, message);
    }

    default BusinessExceptionCustomAssert message(String message) {
        return message(getCode(), message);
    }
}
