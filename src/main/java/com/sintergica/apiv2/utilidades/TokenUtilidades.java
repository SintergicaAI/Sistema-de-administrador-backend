package com.sintergica.apiv2.utilidades;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;


public class TokenUtilidades {

    private static final String ACCESS_TOKEN_SECRET = "EstaEsUnaClaveSecretaSeguraDe256Bits123!!";
    private final static Long TOKEN_LIFETIME_MILLS = /*30L * 24 * 60 * 60 * 1000*/ /*60L * 60 * 1000*/ 60L * 1000;

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

    public static Claims getTokenClaims(String token) {
        try {
            Claims claims = Jwts.parser()
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

    public static boolean isExpired(String token) {
        return TokenUtilidades.isExpired(getTokenClaims(token));
    }

    public static boolean isExpired(Claims claims) {
        if (claims == null) {
            return true;
        }
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }

}
