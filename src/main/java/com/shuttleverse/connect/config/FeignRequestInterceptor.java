package com.shuttleverse.connect.config;

import com.shuttleverse.connect.util.FeignAuthContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

  private static final String AUTHORIZATION_HEADER = "Authorization";

  @Override
  public void apply(RequestTemplate template) {
    String authorizationHeader = null;

    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
    }

    if (authorizationHeader == null) {
      String token = FeignAuthContext.getToken();
      if (token != null) {
        authorizationHeader = "Bearer " + token;
      }
    }

    if (authorizationHeader != null) {
      template.header(AUTHORIZATION_HEADER, authorizationHeader);
    }
  }
}

