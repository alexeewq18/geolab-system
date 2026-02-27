package com.geology.geolabsystem.inventory.dto.response;

import com.geology.geolabsystem.inventory.entity.LabOrderEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyWorksResponseDto {

    private Long id;
    private LabOrderEntity labOrderEntity;
    private Integer amount;
    private LocalDate workDate;
    private String note;
    private LocalDateTime createdAt;
    }
