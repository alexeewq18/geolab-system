package com.geology.geolabsystem.tracking.exception;


public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(
            String orderName, String description, String geologistName, String customMessage) {
        super(String.format("%s Заказ не найден: Номер [%s], Объект [%s], Геолог [%s]",
                customMessage, orderName, description, geologistName));
    }

    public OrderNotFoundException(Long id) {
        super("Заказ с ID " + id + " не найден");
    }

}
