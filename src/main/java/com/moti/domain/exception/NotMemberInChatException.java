package com.moti.domain.exception;

public class NotMemberInChatException extends RuntimeException {
  public NotMemberInChatException() {
  }

  public NotMemberInChatException(String message) {
    super(message);
  }
}
