package com.tf4.delivery.repository.feign.auth;

import com.tf4.delivery.repository.feign.auth.response.AuthFindInfoResponseDto;
import com.tf4.delivery.repository.feign.company.CompanyFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "auth",
        url = "localhost:3350",
        path = "/users",
        fallbackFactory = AuthFeignFallbackFactory.class)
public interface AuthFeignClient {

    @GetMapping("/{userId}")
    AuthFindInfoResponseDto getUserInfo(@PathVariable("userId") UUID userId);
}
