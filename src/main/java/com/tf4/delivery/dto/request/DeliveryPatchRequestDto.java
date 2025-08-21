package com.tf4.delivery.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;

import java.math.BigInteger;
import java.util.UUID;

@Getter
public class DeliveryPatchRequestDto {
    private String status;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private String deliveryAddress;
    private BigInteger receivedUserId;
    private String receivedSlackId;
    private BigInteger companyDeliveryAgentId;
}
