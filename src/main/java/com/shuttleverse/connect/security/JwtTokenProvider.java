package com.shuttleverse.connect.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String jwtSecret;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  public String getUserIdFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, claims -> claims.get("username", String.class));
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public Boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token);
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String extractTokenFromHeader(String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }
}
