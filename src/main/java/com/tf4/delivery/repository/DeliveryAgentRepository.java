package com.tf4.delivery.repository;

import com.tf4.delivery.entity.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, UUID>,
        JpaSpecificationExecutor<DeliveryAgent> {

    DeliveryAgent save(DeliveryAgent deliveryAgent);

    Optional<DeliveryAgent> findFirstByDeliveryTypeAndDeletedAtIsNullOrderByDeliverySeqDesc(String DeliveryType);
    Optional<DeliveryAgent>
    findFirstByDeliveryTypeAndHubIdAndDeletedAtIsNullOrderByDeliverySeqDesc(String DeliveryType, UUID hubId);



}
