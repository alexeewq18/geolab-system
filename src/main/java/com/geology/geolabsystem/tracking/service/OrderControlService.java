package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.LabOrderRequestDto;
import com.geology.geolabsystem.tracking.dto.response.LabOrderResponseDto;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import com.geology.geolabsystem.tracking.exception.OrderNotFoundException;
import com.geology.geolabsystem.tracking.mapper.LabOrdersMapper;
import com.geology.geolabsystem.tracking.repository.DailyWorksRepository;
import com.geology.geolabsystem.tracking.repository.DispatchesRepository;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
import com.geology.geolabsystem.tracking.repository.ShipmentsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderControlService {

    private final LabOrdersRepository labOrdersRepository;
    private final DailyWorksRepository dailyWorksRepository;
    private final ShipmentsRepository shipmentsRepository;
    private final DispatchesRepository dispatchesRepository;
    private final LabOrdersMapper mapper;

    @Transactional
    public LabOrderResponseDto createOrder(LabOrderRequestDto dto) {
        LabOrderEntity entity = mapper.toEntity(dto);
        entity.setStatus(OrderStatus.CREATED);
        entity.setAmount(0L);
        LabOrderEntity saved = labOrdersRepository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public List<LabOrderResponseDto> getAllOrders() {
        return labOrdersRepository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public LabOrderResponseDto getOrderById(Long id) {
        LabOrderEntity entity = labOrdersRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return mapper.toResponseDto(entity);
    }

    public LabOrderResponseDto getOrderByDetails(String orderName, String description, String geologistName) {
        log.debug("Поиск заказа: {}, {}, {}", orderName, description, geologistName);
        LabOrderEntity order = labOrdersRepository
                .findByOrderNameAndDescriptionAndGeologistName(
                        orderName, description, geologistName)
                .orElseThrow(() -> new OrderNotFoundException("", orderName, description, geologistName));
        return mapper.toResponseDto(order);

    }

    @Transactional
    public Long calculateBalance(String orderName, String description, String geologistName) {
        log.info("Пересчёт баланса для заказа: {}, {}, {}", orderName, description, geologistName);
        Long totalShipped = shipmentsRepository.sumAmountByOrderNameAndDescriptionAndGeologistName(orderName, description, geologistName);
        Long totalWorked = dailyWorksRepository.sumAmountByOrderNameAndDescriptionAndGeologistName(orderName, description, geologistName);
        Long totalDispatched = dispatchesRepository.sumAmountByOrderNameAndDescriptionAndGeologistName(orderName, description, geologistName);
        long shipped = totalShipped != null ? totalShipped : 0L;
        long worked = totalWorked != null ? totalWorked : 0L;
        long dispatched = totalDispatched != null ? totalDispatched : 0L;
        long balance = shipped - worked - dispatched;
        LabOrderEntity order = labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(orderName, description, geologistName)
                .orElseThrow(() -> new OrderNotFoundException("Ошибка пересчета. ", orderName, description, geologistName));

        order.setAmount(balance);

        boolean hasOperations = (shipped > 0 || worked > 0 || dispatched > 0);
        if (balance == 0 && hasOperations) {
            order.setStatus(OrderStatus.COMPLETED);
        } else if (balance > 0 && worked > 0 || dispatched > 0) {
            order.setStatus(OrderStatus.IN_PROGRESS);
        }
        return balance;
    }
}