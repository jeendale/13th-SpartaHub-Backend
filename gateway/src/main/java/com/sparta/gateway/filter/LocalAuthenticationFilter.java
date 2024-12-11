package com.sparta.gateway.filter;

import com.sparta.gateway.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LocalAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 헤더 초기화: 검증 전 기본값 설정
        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Username", "null")
                        .header("X-User-Role", "null")
                        .build())
                .build();

        if (path.equals("/api/v1/auth") || path.equals("/api/v1/users/signup")) {
            return chain.filter(exchange);
        }

        String token = jwtUtil.extractToken(exchange);

        if (token == null || !jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 토큰 검증 성공 시 헤더 덮어쓰기
        Claims claims = jwtUtil.parseClaims(token);
        // 새 요청 객체를 만들어서 헤더를 설정하고, 기존 exchange의 요청으로 교체
        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Username", claims.get("username", String.class))
                        .header("X-User-Role", claims.get("role", String.class))
                        .build())
                .build();

        return chain.filter(exchange);
    }
}
