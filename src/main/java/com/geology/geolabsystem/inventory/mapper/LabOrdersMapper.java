package com.geology.geolabsystem.inventory.mapper;


import com.geology.geolabsystem.inventory.dto.request.LabOrderRequestDto;
import com.geology.geolabsystem.inventory.dto.response.LabOrderResponseDto;
import com.geology.geolabsystem.inventory.entity.LabOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LabOrdersMapper {

    LabOrderEntity toEntity (LabOrderRequestDto dto);
    LabOrderResponseDto toResponseDto (LabOrderEntity entity);

}
