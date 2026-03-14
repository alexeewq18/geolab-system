package com.geology.geolabsystem.tracking.dto.response;

import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShipmentResponseDto {

    private Long id;
    private String orderNumber;
    private Long amount;
    private Boolean quality;
    private String comment;
    private LocalDate shippedAt;
    private LocalDateTime createdAt;

}

