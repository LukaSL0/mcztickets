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
public class EventsRouteConfig {

    @Value("${EVENTS_SERVICE_URL:http://localhost:8082}")
    private String eventsServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> eventsRoutes() {
        return route("events-service")
                .route(RequestPredicates.path("/events/**"), http())
                .before(uri(eventsServiceUrl))
                .before(addRequestHeader(BASE_GATEWAY_HEADER, BASE_GATEWAY_VALUE))
                .build();
    }
}
