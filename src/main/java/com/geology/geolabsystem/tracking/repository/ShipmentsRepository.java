package com.geology.geolabsystem.tracking.repository;

import com.geology.geolabsystem.tracking.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentsRepository extends JpaRepository<ShipmentEntity, Long> {
    @Query("SELECT SUM(s.amount) FROM ShipmentEntity s " +
            "WHERE s.labOrderEntity.orderName = :orderName " +
            "AND s.labOrderEntity.description = :description " +
            "AND s.labOrderEntity.geologistName = :geologistName")
    Long sumAmountByOrderNameAndDescriptionAndGeologistName(
            @Param("orderName") String orderName,
            @Param("description") String description,
            @Param("geologistName") String geologistName
    );

    List<ShipmentEntity> findAllByLabOrderEntity_OrderNameAndLabOrderEntity_DescriptionAndLabOrderEntity_GeologistName(
            String orderName, String description, String geologistName);

}
