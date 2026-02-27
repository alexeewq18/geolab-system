package com.geology.geolabsystem.inventory.dto.response;

import com.geology.geolabsystem.inventory.entity.LabOrderEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShipmentResponseDto {

    private Long id;
    private LabOrderEntity labOrderEntity;
    private Integer amount;
    private Boolean quality;
    private String comment;
    private LocalDate shippedAt;
    private LocalDateTime createdAt;

}

