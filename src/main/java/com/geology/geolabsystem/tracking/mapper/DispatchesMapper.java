package com.geology.geolabsystem.tracking.mapper;


import com.geology.geolabsystem.tracking.dto.request.DispatchRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.dto.response.DispatchResponseDto;
import com.geology.geolabsystem.tracking.entity.DispatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DispatchesMapper {

    @Mapping(target = "labOrderEntity", ignore = true)
    DispatchEntity toEntity (DispatchRequestDto dto);

    @Mapping(source = "labOrderEntity.orderName", target = "orderName")
    @Mapping(source = "labOrderEntity.description", target = "description")
    @Mapping(source = "labOrderEntity.geologistName", target = "geologistName")
    DispatchResponseDto toResponseDto (DispatchEntity entity);

}
