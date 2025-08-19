package com.tf4.delivery.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;

import java.math.BigInteger;
import java.util.UUID;

@Getter
public class DeliveryAgentPatchRequestDto {
    private String deliveryType;
    private BigInteger userId;
    private UUID hubId;
}
