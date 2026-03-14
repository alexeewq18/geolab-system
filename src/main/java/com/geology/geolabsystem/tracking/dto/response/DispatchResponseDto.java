package com.geology.geolabsystem.tracking.dto.response;

import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DispatchResponseDto {

    private Long id;
    private String orderNumber;
    private Long amount;
    private LocalDate dispatchDate;
    private String note;
    private LocalDateTime createdAt;
}
