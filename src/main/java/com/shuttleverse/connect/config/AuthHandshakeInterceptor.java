package com.shuttleverse.connect.config;

import com.shuttleverse.connect.security.JwtTokenProvider;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes) {

        String path = request.getURI().getPath();
        
        // Allow /info endpoint without authentication (it's just SockJS metadata)
        if (path != null && path.endsWith("/info")) {
            return true;
        }

        String token = getTokenFromQuery(request.getURI());

        if (token == null) {
            token = getTokenFromHeaders(request.getHeaders());
        }

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            response.setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
            return false;
        }

        String userId = jwtTokenProvider.getUserIdFromToken(token);
        attributes.put("userId", userId);
        attributes.put("token", token);

        return true;
    }

    private String getTokenFromQuery(URI uri) {
        if (uri == null || uri.getQuery() == null) {
            return null;
        }
        String query = uri.getQuery();
        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        return null;
    }

    private String getTokenFromHeaders(Map<String, List<String>> headers) {
        List<String> authHeaders = headers.get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            return jwtTokenProvider.extractTokenFromHeader(authHeader);
        }
        return null;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @Nullable Exception exception) {
        // No-op: Handshake completed, no additional action needed
    }
}
