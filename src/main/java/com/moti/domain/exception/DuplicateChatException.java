package com.moti.domain.exception;

public class DuplicateChatException extends RuntimeException {
  public DuplicateChatException() {
  }

  public DuplicateChatException(String message) {
    super(message);
  }
}
