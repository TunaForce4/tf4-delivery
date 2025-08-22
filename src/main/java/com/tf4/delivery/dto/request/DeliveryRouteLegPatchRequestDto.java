package com.tf4.delivery.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryRouteLegPatchRequestDto {
//    private UUID routeLegId;
//    private UUID deliveryId;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Double estimatedDistanceKm;
    private Long estimatedTimeMin;
    private Double actualDistanceKm;
    private Long actualTimeMin;
    private String status;
    private UUID hubDeliveryAgentId;
}
