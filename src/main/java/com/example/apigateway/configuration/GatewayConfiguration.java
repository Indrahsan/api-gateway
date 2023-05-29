package com.example.apigateway.configuration;

import com.example.apigateway.data.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.RequestSizeGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.AddRequestHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

public class GatewayConfiguration {
    @Autowired
    Login login;

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
                .route("dummy-api", r -> r.path("/api/articles")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f.filter(addRequestHeaderFilterFactory.apply(c -> c.setName(HttpHeaders.AUTHORIZATION).setValue("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiMjAzMDFiMTMwMWZlNzY0MDMwYmU4OTU3N2RhMmIxZmY0Y2UyODIyMDM2NDk1OTc1NjY1NmU2NzkzZWE4ZTMwMGQzZjhmNGNhNzI1NTM5NjMiLCJpYXQiOjE2ODUzNzg4MjMuMDM4NTY3LCJuYmYiOjE2ODUzNzg4MjMuMDM4NTczLCJleHAiOjE3MTcwMDEyMjMuMDM1MTQsInN1YiI6IjExNSIsInNjb3BlcyI6W119.XZmnFZzNiZbvpzLHQuPW5UvIekJlwMy8mZLnAwPEP95xM9ZSkDtO9JIhtZpWYxvqwfkxz-X4Sf3wqgjXa7OQow7FXoXuwikT_F4D4AAmDzBDFl1VJLA-up9Bf4MBXCRyCeFV2mQ90u4H-hU5A446dA-wzA1UgD54KNrN19pmyJ5XgTjpyLbZwPM5O_Z1xKJZ3tX2anu-0kfjvSIdfh3ize2Oc0zmtNRLOymbOndEbnmgYy70j1jX0K1Z2qda7xkZQMkB_waQhtpQwXRIUoivaaJ_lapS7qZiYyCQAqBd6Re7uh5C25nY_CEzBzF3XISZMF6_zbmiTd_rgVKfB-hF9-XNpJdX-vMLnxuJhwZV5Wzj61cxkv36jYuRtmErrVJmFwUoFg-mECoto0qjlf7F4bH5FBZHeGAC-AO24tsMGh4eoBSHf4QnRHHMkYD48xgK5R1lgP7KOm8-0mF_ctqDeK2qbzTAs956s2avDkWz5J74vYmAhB8hetLjFJHEXjmiinWFNJ-00JgzghjDXs1Zhhbq3fCiR15HSmr7Wy3AcpSL7WsImptbRDjqxwaen4mgCmviHnIhIV4Fs0nywX3k6apoDy-Jh38BFamnrHNqbCOvJg8mH4zeR7yaRsrXDmyaMdjTlimU1p4UYdJLdeW56FH76p6lrBk1sPGHCIkJy14"))))
                        .uri("https://apingweb.com"))
                .build();
    }
}
