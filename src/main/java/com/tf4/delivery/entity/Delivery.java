package com.tf4.delivery.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_delivery")
public class Delivery extends Timestamped {

    @Id
    @Column(name = "delivery_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deliveryId;

    @Column(name = "status", nullable = false, length = 100) // VARCHAR(100)
    private String status;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(name = "delivery_address", nullable = false, length = 300)
    private String deliveryAddress;

    @Column(name = "received_user_id", nullable = false)
    private BigInteger receivedUserId;

    @Column(name = "received_slack_id", length = 200)
    private String receivedSlackId;

    @Column(name = "company_delivery_agent_id")
    private BigInteger companyDeliveryAgentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Builder
    public Delivery(String status, UUID departureHubId, UUID arrivalHubId, String deliveryAddress, BigInteger receivedUserId, String receivedSlackId, BigInteger companyDeliveryAgentId, UUID orderId) {
        this.status = status;
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
        this.deliveryAddress = deliveryAddress;
        this.receivedUserId = receivedUserId;
        this.receivedSlackId = receivedSlackId;
        this.companyDeliveryAgentId = companyDeliveryAgentId;
        this.orderId = orderId;
    }
}
