package com.geology.geolabsystem.inventory.dto.request;

import com.geology.geolabsystem.inventory.entity.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LabOrderRequestDto {

    @NotBlank(message = "Номер заказа обязателен")
    private String orderNumber;

    private String description;

    @NotBlank(message = "Фамилия геолога обязательна")
    private String geologistName;
    private OrderStatus status;

    @Min(value = 0, message = "Баланс не может быть отрицательным")
    private Integer amount;
}
