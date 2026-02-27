package com.geology.geolabsystem.inventory.repository;

import com.geology.geolabsystem.inventory.entity.LabOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabOrdersRepository extends JpaRepository<LabOrderEntity, Long> {
}
