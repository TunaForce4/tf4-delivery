package com.tf4.delivery.controller;

import com.tf4.delivery.dto.request.DeliveryCreateRequestDto;
import com.tf4.delivery.dto.request.DeliveryPatchRequestDto;
import com.tf4.delivery.dto.response.DeliveryCreateResponseDto;
import com.tf4.delivery.dto.response.DeliveryResponseDto;
import com.tf4.delivery.dto.response.PageResponse;
import com.tf4.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 배송 목록 조회 및 검색
    @GetMapping
    public ResponseEntity<PageResponse<DeliveryResponseDto>> getDeliveries(
            @PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC)Pageable pageable,
            @RequestParam(value = "q", required = false) String q
    ){
        Page<DeliveryResponseDto> page = deliveryService.searchDeliveries(q, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    // 배송 생성(주문시 자동 생성)
    @PostMapping
    public ResponseEntity<DeliveryCreateResponseDto> createDelivery(
            @Validated
            @RequestBody DeliveryCreateRequestDto requestDto){
        return new ResponseEntity<>(deliveryService.createDelivery(requestDto), HttpStatus.CREATED);
    }

    // 배송 수정
    @PatchMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponseDto> patchDelivery (
            @PathVariable UUID deliveryId,
            @Validated
            @RequestBody DeliveryPatchRequestDto requestDto){
        return new ResponseEntity<>(deliveryService.patchDelivery(deliveryId, requestDto), HttpStatus.OK);
    }

    // 배송 삭제
    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable UUID deliveryId){
        deliveryService.deleteDelivery(deliveryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
