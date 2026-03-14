package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.entity.DailyWorksEntity;
import com.geology.geolabsystem.tracking.mapper.DailyWorksMapper;
import com.geology.geolabsystem.tracking.repository.DailyWorksRepository;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductionService {

    private final DailyWorksRepository dailyWorksRepository;
    private final DailyWorksMapper mapper;
    private final OrderControlService orderControlService;

    @Transactional
    public DailyWorksResponseDto registerDailyWorks(DailyWorksRequestDto dto) {
        DailyWorksEntity entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        DailyWorksEntity saved = dailyWorksRepository.save(entity);

        Long newBalance = orderControlService.calculateBalance(saved.getLabOrderEntity().getOrderName());
        DailyWorksResponseDto response = mapper.toResponseDto(saved);
        response.setAmount(newBalance);
        return response;
    }

    @Transactional
    public List<DailyWorksResponseDto>  registerAllDailyWorks(List<DailyWorksRequestDto> listDto) {
        if (listDto == null || listDto.isEmpty()) {
            System.out.println("Нет данных для сохранения");
            return List.of();
        }

        List<DailyWorksResponseDto> results = new ArrayList<>();

        for (DailyWorksRequestDto item : listDto) {
            DailyWorksEntity entity = mapper.toEntity(item);
            entity.setCreatedAt(LocalDateTime.now());
            DailyWorksEntity saved = dailyWorksRepository.save(entity);

            Long newBalance = orderControlService.calculateBalance(saved.getLabOrderEntity().getOrderName());

            DailyWorksResponseDto response = mapper.toResponseDto(saved);
            response.setAmount(newBalance);
            results.add(response);
        }
        return results;
    }

    public List<DailyWorksResponseDto> getWorksByDate (LocalDate date) {
        return dailyWorksRepository.findByWorkDate(date)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public List<DailyWorksResponseDto> getAllDailyWorks(String orderName, String geologistName) {
        return dailyWorksRepository.findAllByLabOrderEntity_OrderNameAndGeologistName(orderName, geologistName)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }
}
