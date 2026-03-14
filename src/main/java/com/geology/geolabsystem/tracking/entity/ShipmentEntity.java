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
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrderEntity labOrderEntity;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "description")
    private String description;

    @Column(name = "geologist_name")
    private String geologistName;

    @Column(name = "amount")
    private Long amount;

    @Column(length = 500)
    private String note;

    @Column(name = "workDate", nullable = false, updatable = false)
    private LocalDate workDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}