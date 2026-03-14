package com.geology.geolabsystem.tracking.entity;


import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
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

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "description")
    private String description;

    @Column(name = "geologist_name")
    private String geologistName;

    @Column(name="amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "workDate", nullable = false, updatable = false)
    private LocalDate workDate;

    @Column(name = "updated_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
