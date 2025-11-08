package com.shuttleverse.connect.util;

/**
 * Thread-local context for storing authentication token for Feign client calls
 * when there's no HTTP request context (e.g., WebSocket message handlers).
 */
public class FeignAuthContext {

    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

    public static void setToken(String token) {
        tokenHolder.set(token);
    }

    public static String getToken() {
        return tokenHolder.get();
    }

    public static void clear() {
        tokenHolder.remove();
    }
}

