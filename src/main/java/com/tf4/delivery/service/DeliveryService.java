package com.tf4.delivery.service;

import com.tf4.delivery.dto.request.DeliveryCreateRequestDto;
import com.tf4.delivery.dto.request.DeliveryPatchRequestDto;
import com.tf4.delivery.dto.response.DeliveryCreateResponseDto;
import com.tf4.delivery.dto.response.DeliveryResponseDto;
import com.tf4.delivery.entity.Delivery;
import com.tf4.delivery.repository.jpa.DeliveryRepository;
import com.tf4.delivery.spec.DeliverySpecifications;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
//    private final HubClient hubClient;
//    private final UserClient userClient;

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
                .status("WAITING_AT_HUB")
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
        // 강혁님한테 혹시 전화번호 왜 지우라고 한건지 다 여쭤보기 배송테이블에서

        // 업체 배송 담당자 할당

        Delivery savedDelivery = deliveryRepository.save(delivery);

        return new DeliveryCreateResponseDto(savedDelivery.getDeliveryId());
    }

    @Transactional
    public DeliveryResponseDto patchDelivery(UUID deliveryId, DeliveryPatchRequestDto requestDto){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(()-> new NotFoundException("배송을 찾을 수 없습니다."));

        if (requestDto.getStatus() != null && requestDto.getStatus().equals("WAITING_AT_HUB")) {
            // 배송이 시작된 경우, 변경 불가
            delivery.setStatus(requestDto.getStatus());
        }

        // 출발/도착 허브
        if (requestDto.getDepartureHubId() != null) {
            //ensureHubExists(requestDto.getDepartureHubId());           // 없으면 404/422 던지기
            delivery.setDepartureHubId(requestDto.getDepartureHubId());
        }
        if (requestDto.getArrivalHubId() != null) {
            //ensureHubExists(requestDto.getArrivalHubId());
            delivery.setArrivalHubId(requestDto.getArrivalHubId());
        }

        // 주소
        if (requestDto.getDeliveryAddress() != null) {
            delivery.setDeliveryAddress(requestDto.getDeliveryAddress());
        }

        // 수령인/담당자/슬랙
        if (requestDto.getReceivedUserId() != null) {
            //ensureUserExists(requestDto.getReceivedUserId());
            delivery.setReceivedUserId(requestDto.getReceivedUserId());
        }
        if (requestDto.getCompanyDeliveryAgentId() != null) {
            //ensureUserExists(requestDto.getCompanyDeliveryAgentId());
            delivery.setCompanyDeliveryAgentId(requestDto.getCompanyDeliveryAgentId());
        }
        if (requestDto.getReceivedSlackId() != null) {
            delivery.setReceivedSlackId(requestDto.getReceivedSlackId());
        }

        return DeliveryResponseDto.from(delivery);
    }

    @Transactional
    public void deleteDelivery(UUID deliveryId){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() ->  new NotFoundException("배송을 찾을 수 없습니다. "));

        if (delivery.getDeletedAt() != null) return;

        // 지운자가 누군지 값이 필요함
        // 추후 수정 필요함
        UUID actorId = UUID.randomUUID();
        delivery.delete(actorId);
    }

    // 허브Id검증 필요
//    private void ensureHubExists(UUID hubId) {
//        if (!hubClient.exists(hubId)) {
//            throw new UnprocessableEntityException("존재하는 허브 ID가 아닙니다.");
//        }
//    }

    // 유저Id검증 필요
//    private void ensureUserExists(java.math.BigInteger userId) {
//        if (!userClient.exists(userId)) {
//            throw new UnprocessableEntityException("존재하는 유저 ID가 아닙니다.");
//        }
//    }

}
