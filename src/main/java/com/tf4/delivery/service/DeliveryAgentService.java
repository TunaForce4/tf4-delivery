package com.tf4.delivery.service;

import com.tf4.delivery.dto.request.DeliveryAgentCreateRequestDto;
import com.tf4.delivery.dto.request.DeliveryAgentPatchRequestDto;
import com.tf4.delivery.dto.response.DeliveryAgentCreateResponseDto;
import com.tf4.delivery.dto.response.DeliveryAgentResponseDto;
import com.tf4.delivery.entity.DeliveryAgent;
import com.tf4.delivery.repository.jpa.DeliveryAgentRepository;
import com.tf4.delivery.spec.DeliveryAgentSpecifications;
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
public class DeliveryAgentService {

    private final DeliveryAgentRepository deliveryAgentRepository;

    public Page<DeliveryAgentResponseDto> searchDeliveryAgents(String q, Pageable pageable){

        Pageable capped = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 100),
                pageable.getSort()
        );

        Specification<DeliveryAgent> spec = DeliveryAgentSpecifications.searchAllFields(q);

        return deliveryAgentRepository.findAll(spec, capped).map(DeliveryAgentResponseDto::from);
    }

    public DeliveryAgentResponseDto getDeliveryAgent(UUID userId){
        DeliveryAgent deliveryAgent = deliveryAgentRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("배송 담당자를 찾을 수 없습니다. "));
        return DeliveryAgentResponseDto.from(deliveryAgent);
    }

    @Transactional
    public DeliveryAgentCreateResponseDto createDeliveryAgent(DeliveryAgentCreateRequestDto requestDto){
        // 사용자 권한에 따라 다르게 해줘야함
        // userId가 Deliver인 상태면 생성가능
        // 타입이 허브면 삭제된거 빼고 젤큰 배송 순번 +1
        // 타입이 업체면 소속 허브 Id중 젤큰 배송 순번 +1
        String agentType = requestDto.getDeliveryType();
        BigInteger deliverySeq = BigInteger.ZERO;
        if(agentType.equals("HUB")){
            DeliveryAgent deliveryAgent = deliveryAgentRepository
                            .findFirstByDeliveryTypeAndDeletedAtIsNullOrderByDeliverySeqDesc("HUB")
                            .orElse(null);
            if(deliveryAgent != null) deliverySeq = deliveryAgent.getDeliverySeq();

        } else if(agentType.equals("COMPANY")){
            DeliveryAgent deliveryAgent =deliveryAgentRepository
                    .findFirstByDeliveryTypeAndHubIdAndDeletedAtIsNullOrderByDeliverySeqDesc("COMPANY",
                            requestDto.getHubId()).orElse(null);
            if(deliveryAgent != null) deliverySeq = deliveryAgent.getDeliverySeq();
        }

        DeliveryAgent deliveryAgent = DeliveryAgent.builder()
                .deliveryType(requestDto.getDeliveryType())
                .hubId(requestDto.getHubId())
                .userId(requestDto.getUserId())
                .deliverySeq(deliverySeq.add(BigInteger.ONE))
                .build();

        DeliveryAgent savedDeliveryAgent = deliveryAgentRepository.save(deliveryAgent);
        return new DeliveryAgentCreateResponseDto(savedDeliveryAgent.getUserId());
    }

    @Transactional
    public DeliveryAgentResponseDto patchDeliveryAgent(UUID userId, DeliveryAgentPatchRequestDto requestDto) {
        DeliveryAgent deliveryAgent = deliveryAgentRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("배송 담당자를 찾을 수 없습니다."));

        // 현재 맡고 있는 배송이 배송중인 경우 수정 불가
        // p_delivery_route_legs에서 현재상태가 WAIT_AT_HUB가 아닌 것들 중에 userId가 포함 되는 배송이 있으면
        // 수정 불가 안내하기 -> 추후 작성 예정

        // 배송 타입
        if (requestDto.getDeliveryType() != null) {
            //ensureHubExists(requestDto.getDepartureHubId());           // 없으면 404/422 던지기
            deliveryAgent.setDeliveryType(requestDto.getDeliveryType());
        }
        // 소속 허브 Id
        if (requestDto.getHubId() != null) {
            //ensureHubExists(requestDto.getArrivalHubId());
            deliveryAgent.setHubId(requestDto.getHubId());
        }
        // 배송 담당 user Id
        if (requestDto.getUserId() != null) {
            deliveryAgent.setUserId(requestDto.getUserId());
        }

        return DeliveryAgentResponseDto.from(deliveryAgent);
    }

    @Transactional
    public void deleteDeliveryAgent(UUID userId){
        DeliveryAgent deliveryAgent = deliveryAgentRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("배송담당자를 찾을 수 없습니다. "));

        if (deliveryAgent.getDeletedAt() != null) return;

        // 지운자가 누군지 값이 필요함
        // 추후 수정 필요함
        UUID actorId = UUID.randomUUID();
        deliveryAgent.delete(actorId);
    }
}
