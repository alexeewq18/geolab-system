package com.geology.geolabsystem.inventory.entity;


import com.geology.geolabsystem.inventory.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_orders")
@NoArgsConstructor
@Getter
@Setter
public class LabOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "geologist_name")
    private String geologistName;

    @Column(name="amount", nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING) // Чтобы в БД было "LOCAL_PROCESSING", а не 1
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

}
