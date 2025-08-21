package com.tf4.delivery.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_delivery_route_legs")
public class DeliveryRouteLeg extends Timestamped{

    @Id
    @Column(name = "route_leg_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID routeLegId;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(name = "estimated_distance_km", nullable = false)
    private Double estimatedDistanceKm;

    @Column(name = "estimated_time_min", nullable = false)
    private Double estimatedTimeMin;

    @Column(name = "actual_distance_km")
    private Double actualDistanceKm;

    @Column(name = "actual_time_min")
    private Double actualTimeMin;

    @Column(name = "status", nullable = false, length = 100) // VARCHAR(100)
    private String status;

    @Column(name = "hub_delivery_agent_id", nullable = false)
    private UUID hubDeliveryAgentId;

    @Builder
    public DeliveryRouteLeg(UUID routeLegId, UUID deliveryId, UUID departureHubId, UUID arrivalHubId,
                            Double estimatedDistanceKm, Double estimatedTimeMin, Double actualDistanceKm,
                            Double actualTimeMin, String status, UUID hubDeliveryAgentId) {

        this.routeLegId = routeLegId;
        this.deliveryId = deliveryId;
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.estimatedTimeMin = estimatedTimeMin;
        this.actualDistanceKm = actualDistanceKm;
        this.actualTimeMin = actualTimeMin;
        this.status = status;
        this.hubDeliveryAgentId = hubDeliveryAgentId;
    }
}
