package com.geology.geolabsystem.tracking.dto.response;

import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyWorksResponseDto {

    private Long id;
    private String orderName;
    private String description;
    private String geologistName;
    private Long amount;
    private String note;
    private LocalDate workDate;
    private LocalDateTime createdAt;
    }
