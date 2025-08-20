package com.tf4.delivery.controller;

import com.tf4.delivery.dto.request.DeliveryRouteLegCreateRequestDto;
import com.tf4.delivery.dto.request.DeliveryRouteLegPatchRequestDto;
import com.tf4.delivery.dto.response.DeliveryRouteLegCreateResponseDto;
import com.tf4.delivery.dto.response.DeliveryRouteLegResponseDto;
import com.tf4.delivery.dto.response.PageResponse;
import com.tf4.delivery.service.DeliveryRouteLegService;
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
@RequestMapping("/delivery-route-legs")
@RequiredArgsConstructor
public class DeliveryRouteLegController {

    private final DeliveryRouteLegService deliveryRouteLegService;

    // 허브간 배송 경로 목록 조회 및 검색
    @GetMapping
    public ResponseEntity<PageResponse<DeliveryRouteLegResponseDto>> getDeliveryRouteLegs(
            @PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "q", required = false) String q
    ){
        Page<DeliveryRouteLegResponseDto> page = deliveryRouteLegService.searchDeliveryRouteLeg(q, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    // 허브간 배송 경로 생성
    @PostMapping
    public ResponseEntity<DeliveryRouteLegCreateResponseDto> createDeliveryRouteLeg(
            @Validated
            @RequestBody DeliveryRouteLegCreateRequestDto requestDto){
        return new ResponseEntity<>(deliveryRouteLegService.createDeliveryRouteLeg(requestDto), HttpStatus.CREATED);
    }

    // 허브간 배송 경로 수정
    @PatchMapping("/{routeLegId}")
    public ResponseEntity<DeliveryRouteLegResponseDto> patchDeliveryRouteLeg (
            @PathVariable UUID routeLegId,
            @Validated
            @RequestBody DeliveryRouteLegPatchRequestDto requestDto){
        return new ResponseEntity<>(deliveryRouteLegService.patchDeliveryRouteLeg(routeLegId, requestDto), HttpStatus.OK);
    }

    // 허브간 배송 경로 삭제
    @DeleteMapping("/{routeLegId}")
    public ResponseEntity<Void> deleteDeliveryRouteLeg(@PathVariable UUID routeLegId){
        deliveryRouteLegService.deleteDeliveryRouteLeg(routeLegId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
