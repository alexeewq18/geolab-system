package com.geology.geolabsystem.tracking.mapper;


import com.geology.geolabsystem.tracking.dto.request.ShipmentRequestDto;
import com.geology.geolabsystem.tracking.dto.response.ShipmentResponseDto;
import com.geology.geolabsystem.tracking.entity.ShipmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShipmentsMapper {

    ShipmentEntity toEntity (ShipmentRequestDto dto);
    ShipmentResponseDto toResponseDto (ShipmentEntity entity);

}
