package com.tf4.delivery.repository.feign.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthFeignFallbackFactory implements FallbackFactory<AuthFeignClient> {

    @Override
    public AuthFeignClient create(Throwable cause) {
        return null;
    }
}