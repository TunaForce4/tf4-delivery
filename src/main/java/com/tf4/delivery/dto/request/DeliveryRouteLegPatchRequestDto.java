package com.tf4.delivery.dto.request;
import jakarta.persistence.Column;
import lombok.Getter;

import java.math.BigInteger;
import java.util.UUID;

@Getter
public class DeliveryRouteLegPatchRequestDto {
//    private UUID routeLegId;
//    private UUID deliveryId;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Double estimatedDistanceKm;
    private Double estimatedTimeMin;
    private Double actualDistanceKm;
    private Double actualTimeMin;
    private String status;
    private BigInteger hubDeliveryAgentId;
}
