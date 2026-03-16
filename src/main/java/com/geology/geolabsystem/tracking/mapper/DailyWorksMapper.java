package com.geology.geolabsystem.tracking.mapper;


import com.geology.geolabsystem.tracking.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.entity.DailyWorksEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DailyWorksMapper {

    @Mapping(target = "labOrderEntity", ignore = true)
    DailyWorksEntity toEntity (DailyWorksRequestDto dto);

    @Mapping(source = "labOrderEntity.orderName", target = "orderName")
    @Mapping(source = "labOrderEntity.description", target = "description")
    @Mapping(source = "labOrderEntity.geologistName", target = "geologistName")
    DailyWorksResponseDto toResponseDto (DailyWorksEntity entity);

    List<DailyWorksResponseDto> toResponseDtoList(List<DailyWorksEntity> entities);

}
