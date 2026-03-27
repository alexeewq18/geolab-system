package com.geology.geolabsystem.tracking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderSummaryResponseDto {
    private Long totalInStock;
    private Long totalInProgress;
}
