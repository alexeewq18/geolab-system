package com.geology.geolabsystem.tracking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DispatchRequestDto {

    @NotNull(message = "номер заказа обязателен")
    private String orderName;

    @NotNull(message = "Название объекта обязательно")
    private String description;

    @NotNull(message = "Фамилия геолога обязательна")
    private String geologistName;

    @NotNull(message = "Количество не может быть пустым")
    @Positive(message = "Количество должно быть больше 0")
    private Long amount;

    private String notes;

    @NotNull(message = "Дата создания не может быть пустой")
    @PastOrPresent(message = "Дата создания не может быть в будущем")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dispatchDate;
}
