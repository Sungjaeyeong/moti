package com.moti.domain.exception;

public class NotMessageOwnerException extends RuntimeException {
  public NotMessageOwnerException() {
  }

  public NotMessageOwnerException(String message) {
    super(message);
  }
}
