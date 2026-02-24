package com.mcztickets.orders.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mcztickets.orders.dto.external.UserDto;

@FeignClient(
    name = "auth-service",
    url = "${AUTH_SERVICE_URL:http://localhost:8081}"
)
public interface AuthClient {
    
    @GetMapping("/users/{userId}")
    UserDto getUserById(
        @PathVariable UUID userId
    );

}
