package com.geology.geolabsystem.tracking.repository;

import com.geology.geolabsystem.tracking.entity.DailyWorksEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyWorksRepository extends JpaRepository<DailyWorksEntity, Long> {

    @Query("SELECT SUM(d.amount) FROM DailyWorksEntity d " +
            "WHERE d.labOrderEntity.orderName = :orderName " +
            "AND d.labOrderEntity.description = :description " +
            "AND d.labOrderEntity.geologistName = :geologistName")
    Long sumAmountByOrderNameAndDescriptionAndGeologistName(
            @Param("orderName") String orderName,
            @Param("description") String description,
            @Param("geologistName") String geologistName
    );

    List<DailyWorksEntity> findByWorkDate(LocalDate date);

    List<DailyWorksEntity> findAllByLabOrderEntity_OrderNameAndLabOrderEntity_DescriptionAndLabOrderEntity_GeologistName(
            String orderName, String description, String geologistName);
}
