package com.tf4.delivery.controller;


import com.tf4.delivery.dto.request.DeliveryAgentCreateRequestDto;
import com.tf4.delivery.dto.request.DeliveryAgentPatchRequestDto;
import com.tf4.delivery.dto.response.DeliveryAgentCreateResponseDto;
import com.tf4.delivery.dto.response.DeliveryAgentResponseDto;
import com.tf4.delivery.dto.response.PageResponse;
import com.tf4.delivery.service.DeliveryAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/delivery-agents")
@RequiredArgsConstructor
public class DeliveryAgentController {

    private final DeliveryAgentService deliveryAgentService;

    // 배송 담당자 목록 조회 및 검색
    @GetMapping
    public ResponseEntity<PageResponse<DeliveryAgentResponseDto>> getDeliveryAgent(
            @PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "q", required = false) String q
    ){
        Page<DeliveryAgentResponseDto> page = deliveryAgentService.searchDeliveryAgents(q, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    // 배송 담당자 단 건 조회
    @GetMapping("/{deliveryAgentId}")
    public ResponseEntity<DeliveryAgentResponseDto> getDeliveryAgent (@PathVariable BigInteger deliveryAgentId){
        return new ResponseEntity<>(deliveryAgentService.getDeliveryAgent(deliveryAgentId), HttpStatus.OK);
    }

    // 배송 담당자 생성
    @PostMapping
    public ResponseEntity<DeliveryAgentCreateResponseDto> createDeliveryAgent(
            @Validated
            @RequestBody DeliveryAgentCreateRequestDto requestDto){
        return new ResponseEntity<>(deliveryAgentService.createDeliveryAgent(requestDto), HttpStatus.CREATED);
    }

    // 배송 담당자 수정
    @PatchMapping("/{deliveryAgentId}")
    public ResponseEntity<DeliveryAgentResponseDto> patchDeliveryAgent (
            @PathVariable BigInteger deliveryAgentId,
            @Validated
            @RequestBody DeliveryAgentPatchRequestDto requestDto){
        return new ResponseEntity<>(deliveryAgentService.patchDeliveryAgent(deliveryAgentId, requestDto), HttpStatus.OK);
    }
    // 배송 삭제
    @DeleteMapping("/{deliveryAgentId}")
    public ResponseEntity<Void> deleteDeliveryAgent(@PathVariable BigInteger deliveryAgentId){
        deliveryAgentService.deleteDeliveryAgent(deliveryAgentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
