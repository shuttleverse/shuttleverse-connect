package com.shuttleverse.connect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ChatNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleChatNotFoundException(ChatNotFoundException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Chat not found");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(UnauthorizedChatAccessException.class)
  public ResponseEntity<Map<String, String>> handleUnauthorizedChatAccessException(UnauthorizedChatAccessException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Unauthorized access");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(UserNotInChatException.class)
  public ResponseEntity<Map<String, String>> handleUserNotInChatException(UserNotInChatException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "User not in chat");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(InvalidJwtTokenException.class)
  public ResponseEntity<Map<String, String>> handleInvalidJwtTokenException(InvalidJwtTokenException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Invalid token");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Internal server error");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}

