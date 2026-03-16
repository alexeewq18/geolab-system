package com.geology.geolabsystem.tracking.entity;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_orders", uniqueConstraints = @UniqueConstraint(
        name = "uk_order_object_geologist",
        columnNames = {"order_name", "description", "geologist_name"}))
@NoArgsConstructor
@Getter
@Setter
public class LabOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_name", nullable = false, unique = true, length = 50)
    private String orderName;

    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @Column(name = "geologist_name", length = 500, nullable = false)
    private String geologistName;

    @Column(name="amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

}
