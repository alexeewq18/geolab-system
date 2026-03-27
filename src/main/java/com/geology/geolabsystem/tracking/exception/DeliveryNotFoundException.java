package com.geology.geolabsystem.tracking.exception;

public class DeliveryNotFoundException extends RuntimeException {

    public DeliveryNotFoundException(Long id) {
        super(String.format("Отправка в Краснодар с таким id не найдена: " + id));
    }
}
