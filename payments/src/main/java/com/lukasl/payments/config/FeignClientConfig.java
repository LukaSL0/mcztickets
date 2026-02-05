package com.lukasl.payments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Client;
import feign.httpclient.ApacheHttpClient;

@Configuration
public class FeignClientConfig {
    
    @Bean
    public Client feignClient() {
        return new ApacheHttpClient();
    }
}