package com.example.apigateway.configuration;

import com.example.apigateway.data.Login;
import com.example.apigateway.service.TokenService;
import com.example.apigateway.utils.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.RequestSizeGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.AddRequestHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;

public class GatewayConfiguration {
    @Autowired
    Login login;

    private final TokenService tokenService;

    @Autowired
    public GatewayConfiguration(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder, ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilterFactory,
                                     AddRequestHeaderGatewayFilterFactory addRequestHeaderFilterFactory,
                                     RequestSizeGatewayFilterFactory requestSizeFilterFactory) {
        return routeLocatorBuilder.routes()
                .route("dummy-api", r -> r.path("/api/login")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f.filter((exchange, chain) -> {
                            ServerRequest serverRequest = ServerRequest.create(exchange, (List<HttpMessageReader<?>>) HandlerStrategies.builder().build());

                            // Mengambil request body
                            Mono<Login> bodyMono = serverRequest.bodyToMono(Login.class);
                            bodyMono.subscribe(body -> {
                                // Menggunakan body sesuai kebutuhan
                                System.out.println("Email: " + body.getEmail());
                                System.out.println("Password: " + body.getPassword());
                            });

                            // Menambahkan header ke permintaan
                            exchange.getRequest().mutate().headers(headers -> headers.add("Content-Type", "application/json"));

                            // Melanjutkan permintaan ke backend
                            return chain.filter(exchange);
                        }))
                        .uri("https://apingweb.com"))
                .route("dummy-api", r -> (Buildable<Route>) r.path("/api/articles")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f.filter(new TokenFilter(tokenService))))
                .build();
    }
}

