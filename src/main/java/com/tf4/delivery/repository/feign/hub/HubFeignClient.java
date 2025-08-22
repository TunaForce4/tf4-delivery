package com.tf4.delivery.repository.feign.hub;

import com.tf4.delivery.repository.feign.hub.response.HubFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "hub",
        url = "localhost:3340",
        path = "/hub-routes",
        fallbackFactory = HubFeignFallbackFactory.class)
public interface HubFeignClient {

    @GetMapping("/{userId}")
    HubFindInfoResponseDto findHubInfoByUserId(@PathVariable("userId") UUID userId);

    @GetMapping("/{hubId}")
    HubFindInfoResponseDto findHubInfoByHubId(@PathVariable("hubId") UUID hubId);

    @GetMapping()
    HubFindInfoResponseDto findHubIDByHubId(@RequestParam("from") UUID departureHubId,
                                              @RequestParam("to") UUID arrivalHubId);
}

