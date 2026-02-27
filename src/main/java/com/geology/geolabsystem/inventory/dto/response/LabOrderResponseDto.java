package com.geology.geolabsystem.inventory.dto.response;

import com.geology.geolabsystem.inventory.entity.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LabOrderResponseDto {

    private Long id;
    private String orderNumber;
    private String description;
    private String geologistName;
    private Integer amount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
