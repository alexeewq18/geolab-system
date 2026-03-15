package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.LabOrderRequestDto;
import com.geology.geolabsystem.tracking.dto.response.LabOrderResponseDto;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import com.geology.geolabsystem.tracking.mapper.LabOrdersMapper;
import com.geology.geolabsystem.tracking.repository.DailyWorksRepository;
import com.geology.geolabsystem.tracking.repository.DispatchesRepository;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
import com.geology.geolabsystem.tracking.repository.ShipmentsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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
                .orElseThrow(() -> new RuntimeException("Заказ с таким номером не найден: " + id));

        return mapper.toResponseDto(entity);
    }

    @Transactional
    public Long calculateBalance(String orderName) {
        Long totalShipped = shipmentsRepository.sumAmountByOrderName(orderName);
        Long totalWorked = dailyWorksRepository.sumAmountByOrderName(orderName);
        Long totalDispatched = dispatchesRepository.sumAmountByOrderName(orderName);

        Long balance = totalShipped - totalWorked - totalDispatched;

        LabOrderEntity order = labOrdersRepository.findByOrderName(orderName)
                .orElseThrow(() -> new RuntimeException("Заказ " + orderName + " не найден"));

        order.setAmount(balance);

        boolean hasOperations = (totalShipped > 0 || totalWorked > 0 || totalDispatched > 0);

        if (balance == 0 && hasOperations) {
            order.setStatus(OrderStatus.COMPLETED);
        } else if (balance > 0) {
            order.setStatus(OrderStatus.IN_PROGRESS);
        }

        return balance;
    }
}