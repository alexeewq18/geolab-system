package com.geology.geolabsystem.inventory.mapper;


import com.geology.geolabsystem.inventory.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.inventory.dto.request.LabOrderRequestDto;
import com.geology.geolabsystem.inventory.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.inventory.dto.response.LabOrderResponseDto;
import com.geology.geolabsystem.inventory.entity.DailyWorksEntity;
import com.geology.geolabsystem.inventory.entity.LabOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DailyWorksMapper {

    DailyWorksEntity toEntity (DailyWorksRequestDto dto);
    DailyWorksResponseDto toResponseDto (DailyWorksEntity entity);

}
