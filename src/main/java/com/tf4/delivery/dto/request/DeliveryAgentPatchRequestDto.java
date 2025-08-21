package com.tf4.delivery.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryAgentPatchRequestDto {
    private String deliveryType;
    private UUID userId;
    private UUID hubId;
}
