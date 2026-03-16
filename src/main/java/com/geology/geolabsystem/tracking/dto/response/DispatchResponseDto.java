package com.geology.geolabsystem.tracking.dto.response;

import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DispatchResponseDto {

    private Long id;
    private String orderName;
    private String description;
    private String geologistName;
    private Long amount;
    private String notes;
    private LocalDate dispatchDate;
    private String sendingId;
    private LocalDateTime createdAt;
}
