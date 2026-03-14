package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.DispatchRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DispatchResponseDto;
import com.geology.geolabsystem.tracking.entity.DispatchEntity;
import com.geology.geolabsystem.tracking.mapper.DispatchesMapper;
import com.geology.geolabsystem.tracking.repository.DispatchesRepository;
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

    @Transactional
    public DispatchResponseDto registerDispatch(DispatchRequestDto dto) {

        String orderNumber = dto.getOrderNumber();
        Long currentBalance = orderControlService.calculateBalance(orderNumber);

        if (currentBalance < dto.getAmount()) {
            throw new RuntimeException(
                    String.format("Недостаточно монолитов для отправки. Остаток: %d, попытка отправить: %d",
                            currentBalance, dto.getAmount()));
        }

        DispatchEntity entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        DispatchEntity saved = dispatchesRepository.save(entity);

        Long newBalance = orderControlService.calculateBalance(saved.getLabOrderEntity().getOrderName());
        DispatchResponseDto response = mapper.toResponseDto(saved);
        response.setAmount(newBalance);
        return response;
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
