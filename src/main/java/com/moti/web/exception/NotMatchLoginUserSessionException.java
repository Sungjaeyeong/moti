package com.moti.web.exception;

public class NotMatchLoginUserSessionException extends RuntimeException {

    public NotMatchLoginUserSessionException() {
        super();
    }

    public NotMatchLoginUserSessionException(String message) {
        super(message);
    }
}
