package com.sz.core.common.exception.common;

import java.text.MessageFormat;

public interface BusinessExceptionCustomAssert extends IResponseEnum, CustomAssert {

    @Override
    default BaseException newException(Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BusinessException(this, args, msg);
    }

    @Override
    default BaseException newException(Throwable t, Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BusinessException(this, args, msg, t);
    }

    /**
     * 创建异常，允许传递自定义的message和args
     *
     * @param message
     *            自定义错误消息
     * @param args
     *            message占位符对应的参数列表
     * @return 创建的异常
     */
    default BaseException newException(String message, Object... args) {
        String formattedMessage = MessageFormat.format(message, args);
        return new BaseException(this, args, formattedMessage);
    }

}
