package com.sintergica.apiv2.utilidades;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;

public final class TokenUtils {

  private static final String ACCESS_TOKEN_SECRET = System.getenv("DATASOURCE_SEED");
  private static final Long TOKEN_LIFETIME_MILLS = 60L * 60 * 1000;
  private static final Long REFRESH_TOKEN_LIFETIME_MILLS = 7L * 24 * 60 * 60 * 1000;

  public static final String SESSION_TOKEN = "session_token";
  public static final String REFRESH_TOKEN = "session_refresh_token";
  public static final String SUFFIX = "type";

  private TokenUtils() {
    throw new UnsupportedOperationException(
        "Esta es una clase de utilidades y no debe ser instanciada.");
  }

  public static String createToken(String username, String email, Long id) {

    Date expirationDate = new Date(System.currentTimeMillis() + TOKEN_LIFETIME_MILLS);
    HashMap<String, Object> claims = new HashMap<>();

    claims.put("username", username);
    claims.put("id", id);

    return Jwts.builder()
        .subject(email)
        .expiration(expirationDate)
        .claims(claims)
        .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
        .compact();
  }

  public static String createToken(Claims claims) {
    Date expirationDate = new Date(System.currentTimeMillis() + TOKEN_LIFETIME_MILLS);
    return Jwts.builder()
        .subject(claims.getSubject())
        .expiration(expirationDate)
        .claims(claims)
        .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
        .compact();
  }

  public static String createRefreshToken(Claims claims) {
    Date expirationDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_LIFETIME_MILLS);
    return Jwts.builder()
        .subject(claims.getSubject())
        .expiration(expirationDate)
        .claims(claims)
        .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
        .compact();
  }

  public static Claims getTokenClaims(String token) {
    try {
      Claims claims =
          Jwts.parser()
              .setSigningKey(ACCESS_TOKEN_SECRET.getBytes())
              .build()
              .parseSignedClaims(token)
              .getPayload();
      if (claims.getExpiration().before(new Date(System.currentTimeMillis()))) {
        return null;
      }
      return claims;
    } catch (JwtException | IllegalArgumentException e) {
      return null;
    }
  }

  public static String extractToken(String bearer) {
    if (bearer != null && bearer.startsWith("Bearer ")) {
      String token = bearer.substring(7);
      return token;
    }
    return null;
  }

  public static String getTypeToken(String token) {
    return (String) TokenUtils.getTokenClaims(token).get(TokenUtils.SUFFIX);
  }

  public static boolean isExpired(String token) {
    return TokenUtils.isExpired(getTokenClaims(token));
  }

  public static boolean isExpired(Claims claims) {
    if (claims == null) {
      return true;
    }
    return claims.getExpiration().before(new Date(System.currentTimeMillis()));
  }
}
