package com.hornetmall.framework.exception;

public class HornetException extends RuntimeException{
    private int status;

    public HornetException() {

    }

    public HornetException(String message) {
        super(message);
    }

    public HornetException(String message, Throwable cause) {
        super(message, cause);
    }

    public HornetException(Throwable cause) {
        super(cause);
    }

    public HornetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
