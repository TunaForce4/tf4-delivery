package com.tf4.delivery.common.event;

import com.tf4.delivery.entity.Delivery;

import java.util.Objects;
import java.util.UUID;

public class DeliveryCreatedEvent {

    private final UUID deliveryId;
    private final UUID departureHubId;
    private final UUID arrivalHubId;

    public DeliveryCreatedEvent(UUID deliveryId, UUID departureHubId, UUID arrivalHubId) {
        this.deliveryId = Objects.requireNonNull(deliveryId, "deliveryId");
        this.departureHubId = Objects.requireNonNull(departureHubId, "departureHubId");
        this.arrivalHubId = Objects.requireNonNull(arrivalHubId, "arrivalHubId");
    }

    public static DeliveryCreatedEvent of(Delivery d) {
        return new DeliveryCreatedEvent(
                d.getDeliveryId(),
                d.getDepartureHubId(),
                d.getArrivalHubId()
        );
    }

    public UUID getDeliveryId() { return deliveryId; }
    public UUID getDepartureHubId() { return departureHubId; }
    public UUID getArrivalHubId() { return arrivalHubId; }
}