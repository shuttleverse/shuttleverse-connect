package com.shuttleverse.connect.exception;

public class InvalidJwtTokenException extends RuntimeException {
  public InvalidJwtTokenException(String message) {
    super(message);
  }
}

