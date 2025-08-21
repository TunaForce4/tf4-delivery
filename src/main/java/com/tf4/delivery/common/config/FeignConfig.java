package com.tf4.delivery.common.config;

import feign.okhttp.OkHttpClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.tf4.delivery.repository.feign")
public class FeignConfig {

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }
}

