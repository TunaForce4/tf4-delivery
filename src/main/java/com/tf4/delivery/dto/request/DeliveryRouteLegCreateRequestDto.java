package com.tf4.delivery.dto.request;

import com.tf4.delivery.dto.response.DeliveryRouteLegResponseDto;
import com.tf4.delivery.entity.DeliveryRouteLeg;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DeliveryRouteLegCreateRequestDto {
//
//    private UUID routeLegId;
    private UUID deliveryId;
    private UUID departureHubId;
    private UUID arrivalHubId;

}
