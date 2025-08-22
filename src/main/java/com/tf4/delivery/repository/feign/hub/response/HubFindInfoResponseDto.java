package com.tf4.delivery.repository.feign.hub.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class HubFindInfoResponseDto{
        UUID hubId;
        Long transitTime;
        Double distance;
}