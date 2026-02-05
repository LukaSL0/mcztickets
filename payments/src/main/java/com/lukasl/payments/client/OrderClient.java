package com.lukasl.payments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.lukasl.payments.config.FeignClientConfig;
import com.lukasl.payments.dto.external.OrderDto;
import com.lukasl.payments.dto.external.UpdateOrderStatusDto;

@FeignClient(
    name = "order-service",
    url = "http://localhost:8083",
    configuration = FeignClientConfig.class
)
public interface OrderClient {
    
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
