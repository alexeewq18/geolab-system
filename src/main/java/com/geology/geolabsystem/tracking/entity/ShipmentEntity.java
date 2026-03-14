package com.geology.geolabsystem.tracking.entity;


import jakarta.persistence.*;
import lombok.*;
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
    @JoinColumn (name = "order_id", nullable = false)
    private LabOrderEntity labOrderEntity;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "geologist_name")
    private String geologistName;

    @Column(name = "amount")
    private Long amount;

    @Column(nullable = false)
    private Boolean quality = true;

    @Column(length = 500)
    private String comment;

    @Column(name = "shipped_at", nullable = false)
    private LocalDate shippedAt = LocalDate.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}