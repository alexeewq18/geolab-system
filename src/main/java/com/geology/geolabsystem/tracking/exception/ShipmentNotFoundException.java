package com.geology.geolabsystem.tracking.exception;


public class ShipmentNotFoundException extends RuntimeException {
    public ShipmentNotFoundException(Long id) {
        super("Поставка с ID " + id + " не найдена");
    }
}
