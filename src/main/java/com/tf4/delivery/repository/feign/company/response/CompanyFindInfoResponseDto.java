package com.tf4.delivery.repository.feign.company.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class CompanyFindInfoResponseDto {

    private UUID companyId;
    private UUID hubId;
    private String companyName;
}
