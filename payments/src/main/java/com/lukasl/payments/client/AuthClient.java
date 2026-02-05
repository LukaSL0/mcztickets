package com.lukasl.payments.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lukasl.payments.dto.external.UserDto;

@FeignClient(
    name = "auth-service",
    url = "http://localhost:8081"
)
public interface AuthClient {
    
    @GetMapping("/users/{id}")
    UserDto getUserById(
        @PathVariable UUID id
    );

}
