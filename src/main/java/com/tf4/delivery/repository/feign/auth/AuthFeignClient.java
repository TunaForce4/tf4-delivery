package com.tf4.delivery.repository.feign.auth;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "auth-service",
        url = "localhost:3350",
        path = "/internal/auth/order-auth",
        fallbackFactory = AuthFeignFallbackFactory.class)
public interface AuthFeignClient {
}
