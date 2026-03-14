package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.entity.DailyWorksEntity;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
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
    private final LabOrdersRepository labOrdersRepository;

    @Transactional
    public DailyWorksResponseDto registerDailyWorks(DailyWorksRequestDto dto) {

        // 1. Находим заказ
        LabOrderEntity order = labOrdersRepository.findByOrderName(dto.getOrderName())
                .orElseThrow(() -> new RuntimeException("Заказ " + dto.getOrderName() + " не найден"));

        // 2. Маппим DTO в Entity и связываем
        DailyWorksEntity entity = mapper.toEntity(dto);
        entity.setLabOrderEntity(order);

        DailyWorksEntity saved = dailyWorksRepository.save(entity);

        // 3. Пересчитываем баланс
        orderControlService.calculateBalance(dto.getOrderName());

        return mapper.toResponseDto(saved);
    }

    @Transactional
    public List<DailyWorksResponseDto>  registerAllDailyWorks(List<DailyWorksRequestDto> listDto) {
        if (listDto == null || listDto.isEmpty()) return List.of();

        // Мы всё еще используем цикл, так как для каждой записи нужно найти заказ и пересчитать баланс
        List<DailyWorksEntity> entitiesToSave = new ArrayList<>();

        for (DailyWorksRequestDto dto : listDto) {
            LabOrderEntity order = labOrdersRepository.findByOrderName(dto.getOrderName())
                    .orElseThrow(() -> new RuntimeException("Заказ " + dto.getOrderName() + " не найден"));

            DailyWorksEntity entity = mapper.toEntity(dto);
            entity.setLabOrderEntity(order);
            entitiesToSave.add(entity);
        }

        List<DailyWorksEntity> savedEntities = dailyWorksRepository.saveAll(entitiesToSave);

        // Пересчитываем балансы (можно оптимизировать, чтобы не считать один и тот же заказ дважды)
        listDto.stream()
                .map(DailyWorksRequestDto::getOrderName)
                .distinct()
                .forEach(orderControlService::calculateBalance);

        return mapper.toResponseDtoList(savedEntities);
    }

    public List<DailyWorksResponseDto> getWorksByDate (LocalDate date) {
        return mapper.toResponseDtoList(dailyWorksRepository.findByWorkDate(date));
    }

    public List<DailyWorksResponseDto> getAllDailyWorks(String orderName, String geologistName) {
        return mapper.toResponseDtoList(dailyWorksRepository.findAll());
    }
}
