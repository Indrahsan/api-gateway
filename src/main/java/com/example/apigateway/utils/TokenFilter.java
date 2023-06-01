package com.example.apigateway.utils;

import com.example.apigateway.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TokenFilter implements GlobalFilter, Ordered, GatewayFilter {

    private static final int FILTER_ORDER = 1;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String HEADER1 = "Header1";
    private static final String HEADER2 = "Header2";

    private final TokenService tokenService;

    @Autowired
    public TokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = tokenService.getToken();

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(AUTHORIZATION_HEADER, "Bearer " + token)
//                .header(HEADER1, "Value1")
//                .header(HEADER2, "Value2")
                .build();

        HttpHeaders headers = request.getHeaders();
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.addAll(headers)).build();
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        return FILTER_ORDER;
    }

    @Override
    public ShortcutType shortcutType() {
        return GatewayFilter.super.shortcutType();
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return GatewayFilter.super.shortcutFieldOrder();
    }

    @Override
    public String shortcutFieldPrefix() {
        return GatewayFilter.super.shortcutFieldPrefix();
    }
}
