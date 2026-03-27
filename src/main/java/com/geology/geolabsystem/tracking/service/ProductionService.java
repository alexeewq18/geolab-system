package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.entity.DailyWorksEntity;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import com.geology.geolabsystem.tracking.exception.OrderNotFoundException;
import com.geology.geolabsystem.tracking.mapper.DailyWorksMapper;
import com.geology.geolabsystem.tracking.repository.DailyWorksRepository;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionService {

    private final DailyWorksRepository dailyWorksRepository;
    private final DailyWorksMapper mapper;
    private final OrderControlService orderControlService;
    private final LabOrdersRepository labOrdersRepository;

    @Transactional
    public List<DailyWorksResponseDto> registerDailyWorks(List<DailyWorksRequestDto> listDto) {
        if (listDto == null || listDto.isEmpty()) return List.of();

        List<DailyWorksEntity> entitiesToSave = new ArrayList<>();
        Set<LabOrderEntity> ordersToRecalculate = new HashSet<>();

        String currentWorkDayId = java.util.UUID.randomUUID().toString();
        for (DailyWorksRequestDto dto : listDto) {
            LabOrderEntity order = labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                            dto.getOrderName(),
                            dto.getDescription(),
                            dto.getGeologistName()
                    )
                    .orElseThrow(() -> new OrderNotFoundException(
                            "Ошибка фиксации работ! ",
                            dto.getOrderName(),
                            dto.getDescription(),
                            dto.getGeologistName()
                    ));

            DailyWorksEntity entity = mapper.toEntity(dto);
            entity.setLabOrderEntity(order);
            entity.setWorkDayId(currentWorkDayId);
            entitiesToSave.add(entity);

            ordersToRecalculate.add(order);
        }

        List<DailyWorksEntity> savedEntities = dailyWorksRepository.saveAll(entitiesToSave);

        ordersToRecalculate.forEach(order ->
                orderControlService.calculateBalance(
                        order.getOrderName(),
                        order.getDescription(),
                        order.getGeologistName()
                )
        );

        return mapper.toResponseDtoList(savedEntities);
    }

    public List<DailyWorksResponseDto> getWorksByDate(LocalDate date) {
        return mapper.toResponseDtoList(dailyWorksRepository.findByWorkDate(date));
    }

    public Page<DailyWorksResponseDto> getAllDailyWorks(Pageable pageable) {
        return dailyWorksRepository.findAll(pageable)
                .map(mapper::toResponseDto);
    }
}
