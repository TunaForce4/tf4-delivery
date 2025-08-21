package com.tf4.delivery.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryCreateRequestDto {
    private UUID id;
    private UUID supplyCompanyId;
    private UUID receiveCompanyId;
}
