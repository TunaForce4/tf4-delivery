package com.tf4.delivery.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DeliveryPatchRequestDto {
    private String status;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private String deliveryAddress;
    private UUID receivedUserId;
    private String receivedSlackId;
    private UUID companyDeliveryAgentId;
}
