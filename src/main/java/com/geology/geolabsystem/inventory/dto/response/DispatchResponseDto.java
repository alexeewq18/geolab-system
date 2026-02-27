package com.geology.geolabsystem.inventory.dto.response;

import com.geology.geolabsystem.inventory.entity.LabOrderEntity;
import com.geology.geolabsystem.inventory.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DispatchResponseDto {

    private Long id;
    private LabOrderEntity labOrderEntity;
    private Integer amount;
    private LocalDate dispatchDate;
    private String note;
    private LocalDateTime createdAt;
}
