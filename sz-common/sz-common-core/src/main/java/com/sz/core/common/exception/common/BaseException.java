package com.sz.core.common.exception.common;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final IResponseEnum responseEnum;

    private final Object[] args;

    public BaseException(IResponseEnum responseEnum, Object[] args, String message) {
        super(message);
        this.responseEnum = responseEnum;
        this.args = args;
    }

    public BaseException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.responseEnum = responseEnum;
        this.args = args;
    }

    public IResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public Object[] getArgs() {
        return args;
    }

}
