package com.geology.geolabsystem.tracking.repository;

import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabOrdersRepository extends JpaRepository<LabOrderEntity, Long> {

    Optional<LabOrderEntity> findByOrderName(String orderNumber);
}
