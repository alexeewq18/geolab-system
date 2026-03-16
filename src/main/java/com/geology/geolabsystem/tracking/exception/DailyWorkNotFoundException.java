package com.geology.geolabsystem.tracking.exception;

    public class DailyWorkNotFoundException extends RuntimeException {
        public DailyWorkNotFoundException(Long id) {
            super("Работа с ID " + id + " не найдена");
        }
    }
