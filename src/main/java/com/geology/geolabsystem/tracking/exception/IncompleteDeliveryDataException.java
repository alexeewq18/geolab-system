package com.geology.geolabsystem.tracking.exception;

public class IncompleteDeliveryDataException extends RuntimeException {

    public IncompleteDeliveryDataException(
            String orderName,
            String description,
            String geologistName,
            Long amount,
            Long pushAmount) {
        super(String.format("Ошибка переброски! Недостаточно монолитов для заказа [№%s, Объект: %s, Геолог: %s]. " +
                        "На складе: %d шт, попытка отправить: %d шт.",
                orderName, description, geologistName, amount, pushAmount));
    }
}
