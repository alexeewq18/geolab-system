package com.geology.geolabsystem.tracking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LabOrderRequestDto {

    @NotNull(message = "Номер заказа обязателен")
    private String orderName;

    @NotNull(message = "Название объекта обязательно")
    private String description;

    @NotBlank(message = "Фамилия геолога обязательна")
    private String geologistName;

    @Min(value = 0, message = "Баланс не может быть отрицательным")
    private Long amount;
}
