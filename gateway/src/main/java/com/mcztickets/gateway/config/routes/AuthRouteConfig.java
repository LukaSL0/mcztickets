package com.mcztickets.gateway.config.routes;

import static com.mcztickets.gateway.constants.GatewayConstants.BASE_GATEWAY_HEADER;
import static com.mcztickets.gateway.constants.GatewayConstants.BASE_GATEWAY_VALUE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AuthRouteConfig {

    @Value("${AUTH_SERVICE_URL:http://localhost:8081}")
    private String authServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return route("auth-service")
                .route(RequestPredicates.path("/auth/**")
                    .or(RequestPredicates.path("/users/**")), http())
                .before(uri(authServiceUrl))
                .before(addRequestHeader(BASE_GATEWAY_HEADER, BASE_GATEWAY_VALUE))
                .build();
    }
}
