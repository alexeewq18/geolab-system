package com.geology.geolabsystem.tracking.dto.response;

import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyWorksResponseDto {

    private Long id;
    private String orderNumber;
    private Long amount;
    private LocalDate workDate;
    private String note;
    private LocalDateTime createdAt;
    }
