package com.tf4.delivery.dto.response;

import com.tf4.delivery.entity.DeliveryAgent;
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
public class DeliveryAgentResponseDto {
    private BigInteger userId;
    private String deliveryType;
    private BigInteger deliverySeq;
    private UUID hubId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DeliveryAgentResponseDto from(DeliveryAgent d) {
        return DeliveryAgentResponseDto.builder()
                .userId(d.getUserId())
                .deliveryType(d.getDeliveryType())
                .deliverySeq(d.getDeliverySeq())
                .hubId(d.getHubId())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
