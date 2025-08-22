package com.tf4.delivery.dto.response;

import com.tf4.delivery.entity.DeliveryRouteLeg;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogisticsManageResponseDto {
    private final Long id;
    private final Long currentSequence;
}
