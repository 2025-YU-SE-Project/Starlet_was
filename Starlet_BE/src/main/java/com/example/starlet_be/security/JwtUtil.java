package com.example.starlet_be.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

//    @Value("${jwt.secret-key}")
    private final SecretKey secretKey;

    private final long accessValid = 1000L * 60 * 60; // 1시간
    private final long refreshValid = 1000L * 60 * 60 * 24 * 7; // 7일

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. Access 토큰 발급
    public String createAccessToken(String email) {
        return generateToken(email, accessValid);
    }
    // 2. Refresh 토큰 발급
    public String createRefreshToken(String email) {
        return generateToken(email, refreshValid);
    }

    // 토큰 발급 종합
//    private String generateToken(String email, long validTime) {
//        return Jwts.builder()
//                .setSubject(email)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + validTime))
//                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
//                .compact();
//    }

    private String generateToken(String email, long validTime) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

//    public String getEmailFromToken(String token){
//        return Jwts.parserBuilder()
//                .setSigningKey(secretKey.getBytes())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    public boolean validateToken(String token){
//        try{
//            Jwts.parserBuilder()
//                    .setSigningKey(secretKey.getBytes())
//                    .build()
//                    .parseClaimsJws(token);
//
//            return true;
//        }
//        catch(JwtException | IllegalArgumentException e){
//            return false;
//        }
//    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmailFromToken(String token) {
        // Jwts.parser()가 JwtParserBuilder를 반환합니다
        Jws<Claims> jws = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())   // 서명 검증 키 설정
                .build()                       // JwtParser 생성
                .parseClaimsJws(token);        // JWS 파싱 & 검증

        return jws.getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }
        return null;
    }

    // 제대로 작동되는지는 테스트 필요
    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
