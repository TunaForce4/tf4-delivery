package com.tf4.delivery.repository;

import com.tf4.delivery.entity.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigInteger;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, BigInteger>,
        JpaSpecificationExecutor<DeliveryAgent> {
    DeliveryAgent save(DeliveryAgent deliveryAgent);
}
