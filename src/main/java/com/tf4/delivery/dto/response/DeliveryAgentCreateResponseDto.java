package com.tf4.delivery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class DeliveryAgentCreateResponseDto {
    private BigInteger userId;
}
