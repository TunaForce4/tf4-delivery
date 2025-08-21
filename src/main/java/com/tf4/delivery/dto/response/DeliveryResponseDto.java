package com.tf4.delivery.dto.response;

import com.tf4.delivery.entity.Delivery;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryResponseDto {
    private UUID deliveryId;
    private String status;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private String deliveryAddress;
    private BigInteger receivedUserId;
    private String receivedSlackId;
    private BigInteger companyDeliveryAgentId;
    private UUID orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DeliveryResponseDto from(Delivery d) {
        return DeliveryResponseDto.builder()
                .deliveryId(d.getDeliveryId())
                .orderId(d.getOrderId())
                .status(d.getStatus())
                .departureHubId(d.getDepartureHubId())
                .arrivalHubId(d.getArrivalHubId())
                .deliveryAddress(d.getDeliveryAddress())
                .receivedUserId(d.getReceivedUserId())
                .receivedSlackId(d.getReceivedSlackId())
                .companyDeliveryAgentId(d.getCompanyDeliveryAgentId())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
