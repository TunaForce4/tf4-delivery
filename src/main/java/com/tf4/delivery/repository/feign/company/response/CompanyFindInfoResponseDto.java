package com.tf4.delivery.repository.feign.company.response;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CompanyFindInfoResponseDto {
    private UUID companyId;
    private String companyName;
    private String companyType;
    private String address;
    private UUID hubId;
    private String hubName;
    private UUID userId;
}