package com.geology.geolabsystem.tracking.repository;

import com.geology.geolabsystem.tracking.dto.response.DispatchResponseDto;
import com.geology.geolabsystem.tracking.entity.DispatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispatchesRepository extends JpaRepository<DispatchEntity, Long> {
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM ShipmentEntity s WHERE s.labOrderEntity.orderName = :orderName")
    Long sumAmountByOrderName(@Param("orderName") String orderName);

    List<DispatchEntity> findAllByLabOrderEntityOrderNameAndGeologistName (String labOrder, String geologistName);
}
