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
public class PaymentsRouteConfig {

    @Value("${PAYMENTS_SERVICE_URL:http://localhost:8084}")
    private String paymentsServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> paymentsRoutes() {
        return route("payments-service")
                .route(RequestPredicates.path("/payments/**"), http())
                .before(uri(paymentsServiceUrl))
                .before(addRequestHeader(BASE_GATEWAY_HEADER, BASE_GATEWAY_VALUE))
                .build();
    }
}
