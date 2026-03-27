package com.geology.geolabsystem.tracking.repository;

import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabOrdersRepository extends JpaRepository<LabOrderEntity, Long> {

    Optional<LabOrderEntity> findByOrderNameAndDescriptionAndGeologistName(String orderName, String description, String geologistName);

    List<LabOrderEntity> findAllByAmountGreaterThan(Long amount);

    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM LabOrderEntity o WHERE o.amount > 0")
    Long sumAmountInStock();

    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM LabOrderEntity o WHERE o.status = 'IN_PROGRESS'")
    Long sumAmountInProgress();

    Page<LabOrderEntity> findAllByAmountGreaterThan(Long amount, Pageable pageable);
}
