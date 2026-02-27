package com.geology.geolabsystem.inventory.repository;

import com.geology.geolabsystem.inventory.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentsRepository extends JpaRepository<ShipmentEntity, Long> {
}
