package com.tf4.delivery.repository.feign.company;

import com.tf4.delivery.repository.feign.company.response.CompanyFindInfoListResponseDto;
import com.tf4.delivery.repository.feign.company.response.CompanyFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "companies",
        url = "localhost:3360",
        path = "/internal/companies/order-company",
        fallbackFactory = CompanyFeignFallbackFactory.class)
public interface CompanyFeignClient {

    @GetMapping("/find-by-user-id/{userId}")
    CompanyFindInfoResponseDto findCompanyInfoByUserId(@PathVariable("userId") UUID userId);

    @GetMapping("/find-by-company-id/{companyId}")
    CompanyFindInfoResponseDto findCompanyInfoByCompanyId(@PathVariable("companyId") UUID companyId);

    @GetMapping("/find-by-hub-id/{hubId}")
    CompanyFindInfoListResponseDto findCompanyInfoByHubId(@PathVariable("hubId") UUID hubId);
}