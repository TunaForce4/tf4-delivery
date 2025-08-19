package com.tf4.delivery.dto.request;

import lombok.Getter;

import java.math.BigInteger;
import java.util.UUID;

@Getter
public class DeliveryCreateRequestDto {
    private UUID id;
    private UUID supplyCompanyId;
    private UUID receiveCompanyId;
    // 아래는 임시 값. 실제 값 받아오는 api 완성되면 삭제 예정
    private BigInteger tmp;
    private String tmpString;
}
