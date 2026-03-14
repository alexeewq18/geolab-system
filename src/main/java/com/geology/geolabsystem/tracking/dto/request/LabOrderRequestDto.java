package com.geology.geolabsystem.tracking.dto.request;

import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LabOrderRequestDto {

    @NotNull(message = "номер заказа обязателен")
    private String orderNumber;

    private String description;

    @NotBlank(message = "Фамилия геолога обязательна")
    private String geologistName;
    private OrderStatus status;

    @Min(value = 0, message = "Баланс не может быть отрицательным")
    private Long amount;
}
