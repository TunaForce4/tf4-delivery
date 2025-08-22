package com.tf4.delivery.repository.jpa;

import com.tf4.delivery.entity.DeliveryAgent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, UUID>,
        JpaSpecificationExecutor<DeliveryAgent> {

    DeliveryAgent save(DeliveryAgent deliveryAgent);

    Optional<DeliveryAgent> findFirstByDeliveryTypeAndDeletedAtIsNullOrderByDeliverySeqDesc(String DeliveryType);
    Optional<DeliveryAgent>
    findFirstByDeliveryTypeAndHubIdAndDeletedAtIsNullOrderByDeliverySeqDesc(String DeliveryType, UUID hubId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DeliveryAgent> findFirstByDeliveryTypeAndDeletedAtIsNullAndDeliverySeqGreaterThanOrderByDeliverySeqAsc(
            String DeliveryType, BigInteger seq);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DeliveryAgent> findFirstByDeliveryTypeAndDeletedAtIsNullOrderByDeliverySeqAsc(
            String DeliveryType);


}
