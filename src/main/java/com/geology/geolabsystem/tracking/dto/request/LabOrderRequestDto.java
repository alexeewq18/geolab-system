package com.geology.geolabsystem.tracking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LabOrderRequestDto {

    @NotBlank(message = "Номер заказа обязателен")
    private String orderName;

    @NotBlank(message = "Название объекта обязательно")
    private String description;

    @NotBlank(message = "Фамилия геолога обязательна")
    private String geologistName;

    @Min(value = 0, message = "Баланс не может быть отрицательным")
    private Long amount;
}
