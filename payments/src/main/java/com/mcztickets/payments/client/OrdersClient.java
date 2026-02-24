package com.mcztickets.payments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.mcztickets.payments.config.FeignClientConfig;
import com.mcztickets.payments.dto.external.OrderDto;
import com.mcztickets.payments.dto.external.UpdateOrderStatusDto;

@FeignClient(
    name = "orders-service",
    url = "${ORDERS_SERVICE_URL:http://localhost:8083}",
    configuration = FeignClientConfig.class
)
public interface OrdersClient {
    
    @GetMapping("/orders/{orderId}")
    OrderDto getOrderById(
        @PathVariable Long orderId
    );

    @PatchMapping("/orders/{orderId}/status")
    void updateOrderStatus(
        @PathVariable Long orderId,
        @RequestBody UpdateOrderStatusDto dto
    );

}
