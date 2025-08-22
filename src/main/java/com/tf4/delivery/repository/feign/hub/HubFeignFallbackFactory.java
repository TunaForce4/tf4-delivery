package com.tf4.delivery.repository.feign.hub;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class HubFeignFallbackFactory implements FallbackFactory<HubFeignClient> {

    @Override
    public HubFeignClient create(Throwable cause) {
        return null;
    }
}