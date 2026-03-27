package com.geology.geolabsystem.tracking.repository;

import com.geology.geolabsystem.tracking.entity.DispatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DispatchesRepository extends JpaRepository<DispatchEntity, Long> {
    @Query("SELECT SUM(disp.amount) FROM DispatchEntity disp " +
            "WHERE disp.labOrderEntity.orderName = :orderName " +
            "AND disp.labOrderEntity.description = :description " +
            "AND disp.labOrderEntity.geologistName = :geologistName")
    Long sumAmountByOrderNameAndDescriptionAndGeologistName(
            @Param("orderName") String orderName,
            @Param("description") String description,
            @Param("geologistName") String geologistName
    );

    List<DispatchEntity> findAll();

    List<DispatchEntity> findByDispatchDate(LocalDate date);

    List<DispatchEntity> findAllByLabOrderEntity_OrderNameAndLabOrderEntity_DescriptionAndLabOrderEntity_GeologistName(
            String orderName, String description, String geologistName);
}
