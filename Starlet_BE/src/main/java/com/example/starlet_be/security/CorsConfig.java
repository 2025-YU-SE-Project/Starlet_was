package com.example.starlet_be.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    private final String webServerUrl;

    public CorsConfig(@Value("${web.server}") String webServerUrl) {
        this.webServerUrl = webServerUrl;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("http://localhost:*");
        configuration.addAllowedOriginPattern("http://localhost");
        configuration.addAllowedOriginPattern(webServerUrl + ":*");
        configuration.addAllowedOriginPattern(webServerUrl);

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.SET_COOKIE)); // 응답에서 노출할 헤더
        configuration.setMaxAge(3600L); // Preflight 요청 캐시 시간 (초)
        configuration.setAllowCredentials(true); // 쿠키 포함 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 설정 적용
        return source;
    }
}