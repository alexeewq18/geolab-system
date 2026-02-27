package com.geology.geolabsystem.inventory.mapper;


import com.geology.geolabsystem.inventory.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.inventory.dto.request.DispatchRequestDto;
import com.geology.geolabsystem.inventory.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.inventory.entity.DailyWorksEntity;
import com.geology.geolabsystem.inventory.entity.DispatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DispatchesMapper {

    DispatchEntity toEntity (DispatchRequestDto dto);
    DailyWorksResponseDto toResponseDto (DispatchEntity entity);

}
