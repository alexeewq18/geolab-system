package com.geology.geolabsystem.tracking.entity.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    CREATED,
    IN_PROGRESS,
    COMPLETED
}