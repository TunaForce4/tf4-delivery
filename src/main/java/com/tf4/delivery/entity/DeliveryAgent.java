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
@Table(name = "p_delivery_agent")
public class DeliveryAgent extends Timestamped {

    @Id
    @Column(name = "user_id")
    private BigInteger userId;

    @Column(name = "delivery_type", length = 50) // VARCHAR(50)
    private String deliveryType;

    @Column(name = "delivery_seq")
    private BigInteger deliverySeq;

    @Column(name = "hub_id")
    private UUID hubId;


    @Builder
    public DeliveryAgent(BigInteger userId, String deliveryType, BigInteger deliverySeq, UUID hubId) {
        this.userId = userId;
        this.deliveryType = deliveryType;
        this.deliverySeq = deliverySeq;
        this.hubId = hubId;
    }
}
