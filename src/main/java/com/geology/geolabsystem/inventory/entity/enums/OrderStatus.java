package com.geology.geolabsystem.inventory.entity.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
        /**
         * Заказ зарегистрирован, пробы приняты на склад.
         * Начальное состояние.
         */
        CREATED("Принят на склад"),

        /**
         * По заказу зафиксирована хотя бы одна ежедневная работа (Daily Work).
         */
        LOCAL_PROCESSING("В работе (местный)"),

        /**
         * Часть проб или весь объем был перенаправлен в Краснодар (Dispatch).
         */
        REDIRECTED("Перенаправлен в Краснодар"),

        /**
         * Текущий баланс (currentBalance) стал равен 0.
         * Все пробы либо обработаны на месте, либо отправлены.
         */
        COMPLETED("Завершен"),

        /**
         * Заказ отменен по внешним причинам (ошибка геолога, брак проб и т.д.).
         */
        CANCELLED("Отменен");

        private final String description;

    }