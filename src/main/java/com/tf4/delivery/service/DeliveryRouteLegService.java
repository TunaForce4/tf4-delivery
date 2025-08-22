package com.tf4.delivery.service;

import com.tf4.delivery.dto.request.DeliveryPatchRequestDto;
import com.tf4.delivery.dto.request.DeliveryRouteLegCreateRequestDto;
import com.tf4.delivery.dto.request.DeliveryRouteLegPatchRequestDto;
import com.tf4.delivery.dto.response.DeliveryResponseDto;
import com.tf4.delivery.dto.response.DeliveryRouteLegCreateResponseDto;
import com.tf4.delivery.dto.response.DeliveryRouteLegResponseDto;
import com.tf4.delivery.entity.Delivery;
import com.tf4.delivery.entity.DeliveryAgent;
import com.tf4.delivery.entity.DeliveryRouteLeg;
import com.tf4.delivery.entity.LogisticsManage;
import com.tf4.delivery.repository.feign.hub.HubFeignClient;
import com.tf4.delivery.repository.feign.hub.response.HubFindInfoResponseDto;
import com.tf4.delivery.repository.jpa.DeliveryAgentRepository;
import com.tf4.delivery.repository.jpa.DeliveryRepository;
import com.tf4.delivery.repository.jpa.DeliveryRouteLegRepository;
import com.tf4.delivery.repository.jpa.LogisticsManageRepository;
import com.tf4.delivery.spec.DeliveryRouteLegSpecifications;
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
public class DeliveryRouteLegService {

    private final DeliveryRouteLegRepository deliveryRouteLegRepository;
    private final LogisticsManageRepository logisticsManageRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
//    private final HubClient hubClient;
//    private final UserClient userClient;
    private final HubFeignClient hubFeignClient;
    private final DeliveryRepository deliveryRepository;

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

        UUID arrivalHubId = requestDto.getArrivalHubId();
        UUID departureHubId = requestDto.getDepartureHubId();

        HubFindInfoResponseDto hub = hubFeignClient.findHubIDByHubId(arrivalHubId, departureHubId);
        Long estimatedTimeMin = hub.getTransitTime();
        Double estimatedDistanceKm = hub.getDistance();

        UUID hubDeliveryAgentId = pickNextHubAgentByPointer();


        Delivery parent = deliveryRepository.findById(requestDto.getDeliveryId())
                .orElseThrow(() -> new NotFoundException("배송을 찾을 수 없습니다."));
        parent.setStatus("HUB_TO_HUB_IN_TRANSIT");


        DeliveryRouteLeg deliveryRouteLeg = DeliveryRouteLeg.builder()
                .deliveryId(requestDto.getDeliveryId())
                .departureHubId(requestDto.getDepartureHubId())
                .arrivalHubId(requestDto.getArrivalHubId())
                .estimatedDistanceKm(estimatedDistanceKm)
                .estimatedTimeMin(estimatedTimeMin)
                .actualDistanceKm(null)
                .actualTimeMin(null)
                .status("HUB_TO_HUB_IN_TRANSIT")
                .hubDeliveryAgentId(hubDeliveryAgentId)
                .build();

        DeliveryRouteLeg savedDelivery = deliveryRouteLegRepository.save(deliveryRouteLeg);

        return new DeliveryRouteLegCreateResponseDto(savedDelivery.getDeliveryId());
    }

    @Transactional
    public UUID pickNextHubAgentByPointer() {
        // 전역 1행 잠금 & 초기화
        if (logisticsManageRepository.count()==0) {
            LogisticsManage lm = logisticsManageRepository.save(
                    LogisticsManage.builder()
                            .id(1L)  // 전역 1행 전략 =>  상수 P
                            .currentAgentSeq(BigInteger.ZERO)
                            .build());
        }

        LogisticsManage lm = logisticsManageRepository.findById(1L).orElseThrow(() -> new NotFoundException(""));

        BigInteger cur = lm.getCurrentAgentSeq() == null ? BigInteger.ZERO : lm.getCurrentAgentSeq();
        // 1) seq > cur 중 최솟값
        DeliveryAgent agent = deliveryAgentRepository
                .findFirstByDeliveryTypeAndDeletedAtIsNullAndDeliverySeqGreaterThanOrderByDeliverySeqAsc(
                        "HUB", cur)
                // 2) 없으면 wrap: 전체 중 최솟값
                .orElseGet(() -> deliveryAgentRepository
                        .findFirstByDeliveryTypeAndDeletedAtIsNullOrderByDeliverySeqAsc("HUB")
                        .orElseThrow(() -> new IllegalStateException("가용 HUB 담당자 없음")));
        // 포인터를 선택된 담당자의 seq로 이동
        lm.setCurrentAgentSeq(agent.getDeliverySeq());
        logisticsManageRepository.save(lm);
        return agent.getUserId(); // 이 ID를 허브 배송담당자 ID로 사용
    }

    @Transactional
    public DeliveryRouteLegResponseDto patchDeliveryRouteLeg(UUID routeLegId,
                                                             DeliveryRouteLegPatchRequestDto requestDto){
        DeliveryRouteLeg deliveryRouteLeg = deliveryRouteLegRepository.findById(routeLegId)
                .orElseThrow(() -> new NotFoundException("배송 경로 기록을 찾을 수 없습니다. "));

        if (requestDto.getStatus() != null) {
            deliveryRouteLeg.setStatus(requestDto.getStatus());

            Delivery parent = deliveryRepository.findById(deliveryRouteLeg.getDeliveryId())
                    .orElseThrow(() -> new NotFoundException("배송을 찾을 수 없습니다."));
            parent.setStatus(requestDto.getStatus());

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