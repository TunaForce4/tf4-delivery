package com.tf4.delivery.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryRouteLegCreateRequestDto {

    private UUID routeLegId;
    private UUID deliveryId;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Double estimatedDistanceKm;
    private Double estimatedTimeMin;
    private Double actualDistanceKm;
    private Double actualTimeMin;
    private String status;
    private UUID hubDeliveryAgentId;

}
