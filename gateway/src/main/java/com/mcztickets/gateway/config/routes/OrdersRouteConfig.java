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
public class OrdersRouteConfig {

    @Value("${ORDERS_SERVICE_URL:http://localhost:8083}")
    private String ordersServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> ordersRoutes() {
        return route("orders-service")
                .route(RequestPredicates.path("/orders/**"), http())
                .before(uri(ordersServiceUrl))
                .before(addRequestHeader(BASE_GATEWAY_HEADER, BASE_GATEWAY_VALUE))
                .build();
    }
}
