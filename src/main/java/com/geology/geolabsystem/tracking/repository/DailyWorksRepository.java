package com.geology.geolabsystem.tracking.repository;

import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
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
public interface DailyWorksRepository extends JpaRepository<DailyWorksEntity, Long> {
    @Query("SELECT SUM(d.amount) FROM DailyWorksEntity d " +
            "JOIN d.labOrderEntity o " +
            "WHERE o.orderName = :orderName")
    Long sumAmountByOrderName(@Param("orderName") String orderName);

    List<DailyWorksEntity>  findByWorkDate(LocalDate date);

    List<DailyWorksEntity> findAllByLabOrderEntity_OrderNameAndGeologistName(String orderName, String geologositName);
}
