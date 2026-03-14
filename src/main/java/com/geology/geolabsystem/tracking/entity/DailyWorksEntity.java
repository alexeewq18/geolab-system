package com.geology.geolabsystem.tracking.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_works")
@NoArgsConstructor
@Getter
@Setter
public class DailyWorksEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrderEntity labOrderEntity;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "geologist_name")
    private String geologistName;

    @Column(nullable = false)
    private Long amount;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate = LocalDate.now();

    @Column(length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
