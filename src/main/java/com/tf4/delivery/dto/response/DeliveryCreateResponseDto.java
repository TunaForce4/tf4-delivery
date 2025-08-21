package com.tf4.delivery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeliveryCreateResponseDto {
    private UUID deliveryId;
}
