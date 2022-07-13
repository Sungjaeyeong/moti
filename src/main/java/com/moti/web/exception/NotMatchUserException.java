package com.moti.web.exception;

public class NotMatchUserException extends RuntimeException {

    public NotMatchUserException() {
        super();
    }

    public NotMatchUserException(String message) {
        super(message);
    }
}
