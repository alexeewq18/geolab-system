package com.geology.geolabsystem.inventory.repository;

import com.geology.geolabsystem.inventory.entity.DispatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispatchesRepository extends JpaRepository<DispatchEntity, Long> {
}
