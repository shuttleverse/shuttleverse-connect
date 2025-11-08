package com.shuttleverse.connect.exception;

public class UserNotInChatException extends RuntimeException {
  public UserNotInChatException(String message) {
    super(message);
  }
}

