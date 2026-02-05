package com.lukasl.gateway.constants;

public final class GatewayConstants {

    public static final String BASE_GATEWAY_HEADER = "X-Gateway-Request";
    public static final String BASE_GATEWAY_VALUE = "TicketBlitz-Gateway";
    
    private GatewayConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
