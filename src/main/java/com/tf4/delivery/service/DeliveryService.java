package com.tf4.delivery.service;

import com.tf4.delivery.dto.request.DeliveryCreateRequestDto;
import com.tf4.delivery.dto.response.DeliveryCreateResponseDto;
import com.tf4.delivery.dto.response.DeliveryResponseDto;
import com.tf4.delivery.entity.Delivery;
import com.tf4.delivery.repository.jpa.DeliveryRepository;
import com.tf4.delivery.spec.DeliverySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Page<DeliveryResponseDto> searchDeliveries(String q, Pageable pageable){

        Pageable capped = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 100),
                pageable.getSort()
        );

        Specification<Delivery> spec = DeliverySpecifications.searchAllFields(q);

        return deliveryRepository.findAll(spec, capped).map(DeliveryResponseDto::from);
    }

    @Transactional
    public DeliveryCreateResponseDto createDelivery(DeliveryCreateRequestDto requestDto){

        // [order쪽에서 받아 올 수 있는 것]
        // 주문 Id
        // 공급 업체 ID
        // 수령 업체 ID

        Delivery delivery = Delivery.builder()
                .departureHubId(requestDto.getReceiveCompanyId())// 임시 값
                .arrivalHubId(requestDto.getReceiveCompanyId())
                .deliveryAddress("tmp")
                .companyDeliveryAgentId(requestDto.getTmp())
                .orderId(requestDto.getReceiveCompanyId())
                .status("tmp")
                .receivedSlackId(requestDto.getTmpString())
                .receivedUserId(requestDto.getTmp())
                .build();

        // 주문 ID 가져와서 할당

        // 공급 업체 ID로 업체 테이블에서
        // 허브 ID 가져와서 출발 허브 ID에 할당

        // 수령 업체 ID 로 업체 테이블에서
        // 사용자 ID 가져와서 업체 관리자(수령인)에 할당
        // 수령 업체 주소 가져와서 업체 주소에 할당
        // 허브 ID 가져와서 목적지 허브에 할당

        // 수령인 ID를 통해서 수령인 Slack Id랑 전화번호 가져오기
        // 강혁님한테 혹시 전화번호 왜 지우라고 한건지 여쭤보기 배송테이블에서

        // 현재상태는 WAITING_AT_HUB에 할당
        // 업체 배송 담당자 할당

        // 생성일시, 삭제일시, 삭제 유저, 마지막 수정일시는 자동 생성임

        Delivery savedDelivery = deliveryRepository.save(delivery);

        return new DeliveryCreateResponseDto(savedDelivery.getDeliveryId());
    }
}
