package com.geology.geolabsystem.tracking.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@NoArgsConstructor
@Getter
@Setter
public class ShipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrderEntity labOrderEntity;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(length = 500)
    private String notes;

    @Column(name = "shipment_date", nullable = false, updatable = false)
    private LocalDate shipmentDate;

    @Column(name = "shipping_id")
    private String shippingId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

}