package com.tf4.delivery.service;

import com.tf4.delivery.dto.request.DeliveryRouteLegCreateRequestDto;
import com.tf4.delivery.dto.request.DeliveryRouteLegPatchRequestDto;
import com.tf4.delivery.dto.response.DeliveryRouteLegCreateResponseDto;
import com.tf4.delivery.dto.response.DeliveryRouteLegResponseDto;
import com.tf4.delivery.entity.DeliveryRouteLeg;
import com.tf4.delivery.repository.jpa.DeliveryRouteLegRepository;
import com.tf4.delivery.spec.DeliveryRouteLegSpecifications;
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
public class DeliveryRouteLegService {

    private final DeliveryRouteLegRepository deliveryRouteLegRepository;
//    private final HubClient hubClient;
//    private final UserClient userClient;

    public Page<DeliveryRouteLegResponseDto> searchDeliveryRouteLeg(String q, Pageable pageable){

        Pageable capped = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 100),
                pageable.getSort()
        );

        Specification<DeliveryRouteLeg> spec = DeliveryRouteLegSpecifications.searchAllFields(q);

        return deliveryRouteLegRepository.findAll(spec, capped).map(DeliveryRouteLegResponseDto::from);
    }

    public DeliveryRouteLegResponseDto getDeliveryRouteLeg(UUID routeLegId){

        DeliveryRouteLeg deliveryRouteLeg = deliveryRouteLegRepository.findById(routeLegId)
                .orElseThrow(() -> new NotFoundException("배송 경로 기록을 찾을 수 없습니다."));

        return DeliveryRouteLegResponseDto.from(deliveryRouteLeg);
    }


    @Transactional
    public DeliveryRouteLegCreateResponseDto createDeliveryRouteLeg(DeliveryRouteLegCreateRequestDto requestDto){

        // 허브한테 출발허브 - 도착허브 => 예상 거리, 예상 소요시간  달라 해야함

        DeliveryRouteLeg deliveryRouteLeg = DeliveryRouteLeg.builder()
                .departureHubId(requestDto.getDepartureHubId())
                .arrivalHubId(requestDto.getArrivalHubId())
                .estimatedDistanceKm(requestDto.getEstimatedDistanceKm())
                .estimatedTimeMin(requestDto.getEstimatedTimeMin())
                .actualDistanceKm(requestDto.getActualDistanceKm())
                .actualTimeMin(requestDto.getActualTimeMin())
                .status("WAITING_AT_HUB")
                .hubDeliveryAgentId(requestDto.getHubDeliveryAgentId())
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

        DeliveryRouteLeg savedDelivery = deliveryRouteLegRepository.save(deliveryRouteLeg);

        return new DeliveryRouteLegCreateResponseDto(savedDelivery.getDeliveryId());
    }

    @Transactional
    public DeliveryRouteLegResponseDto patchDeliveryRouteLeg(UUID routeLegId,
                                                             DeliveryRouteLegPatchRequestDto requestDto){
        DeliveryRouteLeg deliveryRouteLeg = deliveryRouteLegRepository.findById(routeLegId)
                .orElseThrow(() -> new NotFoundException("배송 경로 기록을 찾을 수 없습니다. "));

        if (requestDto.getStatus() != null) {
            deliveryRouteLeg.setStatus(requestDto.getStatus());
        }

        // 출발/도착 허브
        if (requestDto.getDepartureHubId() != null) {
            // 검증
            // ensureHubExists(requestDto.getDepartureHubId());
            deliveryRouteLeg.setDepartureHubId(requestDto.getDepartureHubId());
        }

        if (requestDto.getArrivalHubId() != null) {
            // ensureHubExists(requestDto.getArrivalHubId());
            deliveryRouteLeg.setArrivalHubId(requestDto.getArrivalHubId());
        }

        // 예상 거리
        if (requestDto.getEstimatedDistanceKm() != null) {
            deliveryRouteLeg.setEstimatedDistanceKm(requestDto.getEstimatedDistanceKm());
        }

        // 예상 시간
        if (requestDto.getEstimatedTimeMin() != null) {
            deliveryRouteLeg.setEstimatedTimeMin(requestDto.getEstimatedTimeMin());
        }

        // 실제 거리
        if (requestDto.getActualDistanceKm() != null) {
            deliveryRouteLeg.setActualDistanceKm(requestDto.getActualDistanceKm());
        }

        // 실제 시간
        if (requestDto.getActualTimeMin() != null) {
            deliveryRouteLeg.setActualTimeMin(requestDto.getActualTimeMin());
        }

        // 허브 배송 담당자
        if (requestDto.getHubDeliveryAgentId() != null) {
            //ensureUserExists(requestDto.getReceivedUserId());
            deliveryRouteLeg.setHubDeliveryAgentId(requestDto.getHubDeliveryAgentId());
        }

        return DeliveryRouteLegResponseDto.from(deliveryRouteLeg);
    }

    @Transactional
    public void deleteDeliveryRouteLeg(UUID routeLegId){
        DeliveryRouteLeg deliveryRouteLeg = deliveryRouteLegRepository.findById(routeLegId)
                .orElseThrow(() ->  new NotFoundException("배송 경로 기록을 찾을 수 없습니다. "));

        if (deliveryRouteLeg.getDeletedAt() != null) return;

        // 지운자가 누군지 값이 필요함
        // 추후 수정 필요함
        UUID actorId = UUID.randomUUID();
        deliveryRouteLeg.delete(actorId);
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