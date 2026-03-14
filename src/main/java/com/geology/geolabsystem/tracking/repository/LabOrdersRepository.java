package com.geology.geolabsystem.tracking.repository;

import com.geology.geolabsystem.tracking.entity.DailyWorksEntity;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabOrdersRepository extends JpaRepository<LabOrderEntity, Long> {

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM ShipmentEntity s WHERE s.labOrderEntity.orderName = :orderName")
    Long sumAmountByOrderName(@Param("orderName") String orderName);

    Optional <LabOrderEntity> findByOrderName(String orderName);

}
