package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.DispatchRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DispatchResponseDto;
import com.geology.geolabsystem.tracking.entity.DispatchEntity;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import com.geology.geolabsystem.tracking.mapper.DispatchesMapper;
import com.geology.geolabsystem.tracking.repository.DispatchesRepository;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogisticsService {

    private final DispatchesRepository dispatchesRepository;
    private final DispatchesMapper mapper;
    private final OrderControlService orderControlService;
    private final LabOrdersRepository labOrdersRepository;

    @Transactional
    public DispatchResponseDto registerDispatch(DispatchRequestDto dto) {

        // 1. Найти заказ
        LabOrderEntity order = labOrdersRepository.findByOrderName(dto.getOrderName())
                .orElseThrow(() -> new RuntimeException("Заказ " + dto.getOrderName() + " не найден"));

        // 2. Проверка баланса
        if (order.getAmount() < dto.getAmount()) {
            throw new RuntimeException(String.format("Недостаточно монолитов. Остаток: %d", order.getAmount()));
        }

        // 3. МАГИЯ МАППЕРА: Создаем сущность в одну строку
        DispatchEntity entity = mapper.toEntity(dto);
        entity.setLabOrderEntity(order); // Привязываем заказ руками, так как это связь с БД

        // 4. Сохраняем и пересчитываем
        DispatchEntity saved = dispatchesRepository.save(entity);
        orderControlService.calculateBalance(dto.getOrderName());

        // 5. МАГИЯ МАППЕРА: Возвращаем ответ
        return mapper.toResponseDto(saved);
    }

    public List<DispatchResponseDto> getAllDispatch(String labOrder, String geologistName) {
        return dispatchesRepository.findAllByLabOrderEntityOrderNameAndGeologistName (labOrder, geologistName)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public DispatchResponseDto getDispatchById(Long id) {
        DispatchEntity entity = dispatchesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отправка в Краснодар с таким id не найдена: " + id));
        return mapper.toResponseDto(entity);
    }
}
