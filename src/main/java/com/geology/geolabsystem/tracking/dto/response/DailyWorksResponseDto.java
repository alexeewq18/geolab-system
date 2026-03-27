package com.geology.geolabsystem.tracking.dto.response;

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
    private String notes;
    private LocalDate workDate;
    private String workDayId;
    private LocalDateTime createdAt;
}
