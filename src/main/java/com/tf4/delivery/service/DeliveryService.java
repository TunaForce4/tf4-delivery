package com.tf4.delivery.service;

import com.tf4.delivery.dto.request.DeliveryCreateRequestDto;
import com.tf4.delivery.dto.request.DeliveryPatchRequestDto;
import com.tf4.delivery.dto.response.DeliveryCreateResponseDto;
import com.tf4.delivery.dto.response.DeliveryResponseDto;
import com.tf4.delivery.entity.Delivery;
import com.tf4.delivery.repository.feign.auth.AuthFeignClient;
import com.tf4.delivery.repository.feign.auth.response.AuthFindInfoResponseDto;
import com.tf4.delivery.repository.feign.company.CompanyFeignClient;
import com.tf4.delivery.repository.feign.company.response.CompanyFindInfoResponseDto;
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

import java.math.BigInteger;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
//    private final HubClient hubClient;
//    private final UserClient userClient;
    private final CompanyFeignClient companyFeignClient;
    private final AuthFeignClient authFeignClient;

    public Page<DeliveryResponseDto> searchDeliveries(String q, Pageable pageable){

        Pageable capped = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 100),
                pageable.getSort()
        );

        Specification<Delivery> spec = DeliverySpecifications.searchAllFields(q);

        return deliveryRepository.findAll(spec, capped).map(DeliveryResponseDto::from);
    }

    public DeliveryResponseDto getDelivery(UUID deliveryId){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException(""));

        return DeliveryResponseDto.from(delivery);
    }

    @Transactional
    public DeliveryCreateResponseDto createDelivery(DeliveryCreateRequestDto requestDto){

        // [order쪽에서 받아 올 수 있는 것]
        // 주문 Id

        // 공급 업체 ID -> 출발 허브 id,
        CompanyFindInfoResponseDto supplyCompany = companyFeignClient.getCompanyInfo(requestDto.getSupplyCompanyId());
        UUID departmentHubId = supplyCompany.getHubId();

        // 수령 업체 ID -> 수령인 주소, 도착 허브 id, 수령인 id
        CompanyFindInfoResponseDto receivedCompany = companyFeignClient.getCompanyInfo(requestDto.getReceiveCompanyId());
        UUID userId = receivedCompany.getUserId();
        String deliveryAddress = receivedCompany.getAddress();
        UUID arrivalHubId = receivedCompany.getHubId();

        // 수령인을 통해 얻을 수 있는 것 -> 수령인 slackId
        AuthFindInfoResponseDto receivedUser = authFeignClient.getUserInfo(requestDto.getId());
        String slackId = receivedUser.getSlackId();


        // 업체 배송 담당자 = Null
        Delivery delivery = Delivery.builder()
                .departureHubId(departmentHubId)
                .arrivalHubId(arrivalHubId)
                .deliveryAddress(deliveryAddress)
                .orderId(requestDto.getId())
                .status("WAITING_AT_HUB") // 고정
                .receivedSlackId(slackId)
                .receivedUserId(userId)
                .companyDeliveryAgentId(null)
                .build();

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
