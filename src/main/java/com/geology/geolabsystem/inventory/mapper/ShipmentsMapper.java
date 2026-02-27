package com.geology.geolabsystem.inventory.mapper;


import com.geology.geolabsystem.inventory.dto.request.ShipmentRequestDto;
import com.geology.geolabsystem.inventory.dto.response.ShipmentResponseDto;
import com.geology.geolabsystem.inventory.entity.ShipmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShipmentsMapper {

    ShipmentEntity toEntity (ShipmentRequestDto dto);
    ShipmentResponseDto toResponseDto (ShipmentEntity entity);

}
