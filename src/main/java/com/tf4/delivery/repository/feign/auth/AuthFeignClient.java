package com.tf4.delivery.repository.feign.auth;

import com.tf4.delivery.repository.feign.auth.response.AuthFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "Auths",
        url = "localhost:3360",
        path = "/iternal/users")
public interface AuthFeignClient {

    @GetMapping("/{userId}")
    AuthFindInfoResponseDto getUserInfo(@PathVariable("userId") UUID userId);
}
