package com.geology.geolabsystem.tracking.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispatches")
@NoArgsConstructor
@Getter
@Setter
public class DispatchEntity {

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


    @Column(name = "dispatch_date", nullable = false, updatable = false)
    private LocalDate dispatchDate;

    @Column(name = "sending_id")
    private String sendingId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
