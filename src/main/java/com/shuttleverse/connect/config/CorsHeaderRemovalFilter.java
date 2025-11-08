package com.shuttleverse.connect.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Removes CORS headers from responses since the gateway handles CORS.
 * SockJS still needs setAllowedOriginPatterns for validation, but we don't want
 * it to add CORS headers to responses.
 */
@Component
@Order(2)
public class CorsHeaderRemovalFilter implements Filter {

    private static final String[] CORS_HEADERS = {
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Headers",
            "Access-Control-Expose-Headers",
            "Access-Control-Max-Age"
    };

    @Override
    public void doFilter(
            jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Wrap response to intercept and remove CORS headers
        HttpServletResponse wrappedResponse = new HttpServletResponseWrapper(httpResponse) {
            @Override
            public void setHeader(String name, String value) {
                if (!isCorsHeader(name)) {
                    super.setHeader(name, value);
                }
            }

            @Override
            public void addHeader(String name, String value) {
                if (!isCorsHeader(name)) {
                    super.addHeader(name, value);
                }
            }
        };

        chain.doFilter(request, wrappedResponse);
    }

    private boolean isCorsHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        for (String corsHeader : CORS_HEADERS) {
            if (corsHeader.equalsIgnoreCase(headerName)) {
                return true;
            }
        }
        return false;
    }

    private static class HttpServletResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {
        public HttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }
    }
}
