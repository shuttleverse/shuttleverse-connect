package com.shuttleverse.connect.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class UserContext {
  
  private static final String USER_ID_HEADER = "X-User-Id";

  public static String getCurrentUserId() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      String userId = request.getHeader(USER_ID_HEADER);
      if (userId != null) {
        return userId;
      }
    }
    throw new IllegalStateException("User ID not found in request context");
  }
}

