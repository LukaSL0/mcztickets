package com.lukasl.gateway.config.routes;

import static com.lukasl.gateway.constants.GatewayConstants.BASE_GATEWAY_HEADER;
import static com.lukasl.gateway.constants.GatewayConstants.BASE_GATEWAY_VALUE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;


@Configuration
public class PaymentsRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> paymentsRoutes() {
        return route("payments-service")
                .route(RequestPredicates.path("/payments/**"), http())
                .before(uri("http://localhost:8084"))
                .before(stripPrefix(1))
                .before(addRequestHeader(BASE_GATEWAY_HEADER, BASE_GATEWAY_VALUE))
                .build();
    }
}
