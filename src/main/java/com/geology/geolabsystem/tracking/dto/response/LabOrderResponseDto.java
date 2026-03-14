package com.geology.geolabsystem.tracking.dto.response;

import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LabOrderResponseDto {

    private Long id;
    private String orderName;
    private String description;
    private String geologistName;
    private Long amount;
    private OrderStatus status;
    private LocalDate workDate;
    private LocalDateTime createdAt;
}
