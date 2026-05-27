package com.prasac.gateway.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret:your-super-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm}")
    private String jwtSecret;

    private SecretKey secretKey;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes for HS256");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Skip authentication for login and register endpoints
            String path = exchange.getRequest().getURI().getPath();
            if (path.contains("/api/auth/login") || path.contains("/api/auth/register") || path.contains("/api/auth/refresh")) {
                return chain.filter(exchange);
            }

            // Get the authorization header
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.replace("Bearer ", "");

            Claims claims = parseClaims(token);
            if (claims == null) {
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            String username = claims.getSubject();
            Object userId = claims.get("userId");

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest()
                            .mutate()
                            .header("X-Username", username)
                            .header("X-User-Id", userId == null ? "" : String.valueOf(userId))
                            .build())
                    .build();

            return chain.filter(mutatedExchange);
        };
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return null;
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"code\":%d,\"message\":\"%s\"}", httpStatus.value(), message);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes())));
    }

    public static class Config {
    }
}
