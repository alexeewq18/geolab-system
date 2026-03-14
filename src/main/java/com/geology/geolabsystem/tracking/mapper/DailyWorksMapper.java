package com.geology.geolabsystem.tracking.mapper;


import com.geology.geolabsystem.tracking.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.entity.DailyWorksEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DailyWorksMapper {

    DailyWorksEntity toEntity (DailyWorksRequestDto dto);
    DailyWorksResponseDto toResponseDto (DailyWorksEntity entity);

}
