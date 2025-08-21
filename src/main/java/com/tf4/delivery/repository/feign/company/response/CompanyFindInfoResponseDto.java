package com.tf4.delivery.repository.feign.company.response;

import lombok.Getter;

import java.math.BigInteger;
import java.util.UUID;

@Getter
public class CompanyFindInfoResponseDto {
    private UUID companyId;
    private UUID hubId;
    private String companyName;
    private BigInteger userId;
    private String address;
}