package com.geology.geolabsystem.tracking.dto.request;

import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DispatchRequestDto {

    @NotNull(message = "номер заказа обязателен")
    private String orderNumber;

    @NotNull(message = "Фамилия геолога обязательна")
    private String geologistName;

    @NotNull(message = "Количество не может быть пустым")
    @Positive(message = "Количество должно быть больше 0")
    private Long amount;
    private String note;
}
