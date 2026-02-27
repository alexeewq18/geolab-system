package com.geology.geolabsystem.inventory.dto.request;

import com.geology.geolabsystem.inventory.entity.LabOrderEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShipmentRequestDto {

    @NotNull(message = "номер заказа обязателен")
    private LabOrderEntity labOrderEntity;

    @NotNull(message = "Количество не может быть пустым")
    @Positive(message = "Количество должно быть больше 0")
    private Integer amount;

    private String note;
    private Boolean quality;
    private String comment;

}
