package com.geology.geolabsystem.inventory.repository;

import com.geology.geolabsystem.inventory.entity.DailyWorksEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyWorksRepository extends JpaRepository<DailyWorksEntity, Long> {
}
