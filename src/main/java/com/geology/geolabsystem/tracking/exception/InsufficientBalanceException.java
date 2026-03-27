package com.geology.geolabsystem.tracking.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String orderName, String description,
                                        String geologistName, Long available, Long requested) {

        super(String.format(
                "Недостаточно монолитов для заказа [№%s, Объект: %s, Геолог: %s]. " +
                        "На складе: %d шт, попытка отправить: %d шт.",
                orderName, description, geologistName, available, requested
        ));
    }
}
