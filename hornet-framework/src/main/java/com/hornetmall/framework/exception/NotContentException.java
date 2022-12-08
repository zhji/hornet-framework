package com.hornetmall.framework.exception;

public class NotContentException extends RuntimeException {
    public NotContentException() {
    }

    public NotContentException(String message) {
        super(message);
    }

    public NotContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotContentException(Throwable cause) {
        super(cause);
    }

    public NotContentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
