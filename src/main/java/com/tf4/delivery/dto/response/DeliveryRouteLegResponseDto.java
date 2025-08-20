package com.tf4.delivery.dto.response;

import com.tf4.delivery.entity.DeliveryRouteLeg;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryRouteLegResponseDto {
    private UUID routeLegId;
    private UUID deliveryId;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Double estimatedDistanceKm;
    private Double estimatedTimeMin;
    private Double actualDistanceKm;
    private Double actualTimeMin;
    private String status;
    private BigInteger hubDeliveryAgentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DeliveryRouteLegResponseDto from(DeliveryRouteLeg d) {
        return DeliveryRouteLegResponseDto.builder()
                .routeLegId(d.getRouteLegId())
                .deliveryId(d.getDeliveryId())
                .departureHubId(d.getDepartureHubId())
                .arrivalHubId(d.getArrivalHubId())
                .estimatedDistanceKm(d.getEstimatedDistanceKm())
                .estimatedTimeMin(d.getEstimatedTimeMin())
                .actualDistanceKm(d.getActualDistanceKm())
                .actualTimeMin(d.getActualTimeMin())
                .status(d.getStatus())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}