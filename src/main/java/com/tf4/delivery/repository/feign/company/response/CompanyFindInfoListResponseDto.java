package com.tf4.delivery.repository.feign.company.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFindInfoListResponseDto {

    private List<CompanyFindInfoResponseDto> data = Collections.emptyList();

    public Map<UUID, String> toMap() {
        if (data == null || data.isEmpty()) return Collections.emptyMap();

        return data.stream()
                .filter(Objects::nonNull)
                .filter(d -> d.getCompanyId() != null && d.getCompanyName() != null)
                .collect(Collectors.toMap(
                        CompanyFindInfoResponseDto::getCompanyId,
                        CompanyFindInfoResponseDto::getCompanyName,
                        (oldVal, newVal) -> oldVal,   // 키 중복 시 기존 값 유지
                        LinkedHashMap::new            // 입력 순서 유지
                ));
    }
}