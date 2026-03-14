package com.geology.geolabsystem.tracking.mapper;


import com.geology.geolabsystem.tracking.dto.request.ShipmentRequestDto;
import com.geology.geolabsystem.tracking.dto.response.ShipmentResponseDto;
import com.geology.geolabsystem.tracking.entity.ShipmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShipmentsMapper {

    @Mapping(target = "labOrderEntity", ignore = true)
    ShipmentEntity toEntity (ShipmentRequestDto dto);

    @Mapping(source = "labOrderEntity.orderName", target = "orderName")
    @Mapping(source = "labOrderEntity.description", target = "description")
    @Mapping(source = "labOrderEntity.geologistName", target = "geologistName")
    ShipmentResponseDto toResponseDto (ShipmentEntity entity);
}
