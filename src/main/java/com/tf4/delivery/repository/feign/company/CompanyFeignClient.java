package com.tf4.delivery.repository.feign.company;

import com.tf4.delivery.repository.feign.company.response.CompanyFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "companies",
        url = "localhost:3360",
        path = "/companies",
        fallbackFactory = CompanyFeignFallbackFactory.class)
public interface CompanyFeignClient {

    @GetMapping("/{companyId}")
    CompanyFindInfoResponseDto findCompanyInfoByCompanyId(@PathVariable("companyId") UUID companyId);

}