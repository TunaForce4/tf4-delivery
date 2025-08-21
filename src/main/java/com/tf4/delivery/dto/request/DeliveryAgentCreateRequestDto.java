package com.tf4.delivery.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryAgentCreateRequestDto {
    private String deliveryType;
    private UUID hubId;
    private UUID userId;
}
