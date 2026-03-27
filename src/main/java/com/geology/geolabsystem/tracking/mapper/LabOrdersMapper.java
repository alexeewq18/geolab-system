package com.geology.geolabsystem.tracking.mapper;


import com.geology.geolabsystem.tracking.dto.request.LabOrderRequestDto;
import com.geology.geolabsystem.tracking.dto.request.ShipmentRequestDto;
import com.geology.geolabsystem.tracking.dto.response.LabOrderResponseDto;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LabOrdersMapper {

    LabOrderEntity toEntity(LabOrderRequestDto dto);

    LabOrderResponseDto toResponseDto(LabOrderEntity entity);

    @Mapping(source = "orderName", target = "orderName")
    LabOrderEntity toEntityFromShipment(ShipmentRequestDto dto);

}
