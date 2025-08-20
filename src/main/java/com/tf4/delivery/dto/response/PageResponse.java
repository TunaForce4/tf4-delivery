package com.tf4.delivery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private String sort;

    public static <T> PageResponse<T> of(Page<T> page) {
        String sortParam = page.getPageable().getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                .findFirst()
                .orElse("createdAt,desc");
        /*
         * order.getProperty() → 어떤 컬럼으로 정렬했는지 (예: "createdAt")
         * order.getDirection().name().toLowerCase() → 정렬 방향 (예: "desc")
         * 합쳐서 "createdAt,desc" 같은 문자열을 생성.
         * 만약 정렬 조건이 비어있다면 기본 "createdAt,desc"로 대체.
         * */

        return new PageResponse<>(
                page.getContent(),
                page.getNumber() + 1,   // PageRequest는 0-based, 요구사항은 1-based
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                sortParam
        );
    }
}