package com.geology.geolabsystem.tracking.mapper;


import com.geology.geolabsystem.tracking.dto.request.DispatchRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.dto.response.DispatchResponseDto;
import com.geology.geolabsystem.tracking.entity.DispatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DispatchesMapper {

    DispatchEntity toEntity (DispatchRequestDto dto);
    DispatchResponseDto toResponseDto (DispatchEntity entity);

}
