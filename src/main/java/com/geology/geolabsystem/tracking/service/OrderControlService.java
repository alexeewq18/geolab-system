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
import java.time.LocalDateTime;
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
        entity.setCreatedAt(LocalDateTime.now());

        LabOrderEntity saveOrder = labOrdersRepository.save(entity);
        return mapper.toResponseDto(saveOrder);
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
        if (totalShipped == null) totalShipped = 0L;

        Long totalWorked = dailyWorksRepository.sumAmountByOrderName(orderName);
        if (totalWorked == null) totalWorked = 0L;

        Long totalDispatched = dispatchesRepository.sumAmountByOrderName(orderName);
        if (totalDispatched == null) totalDispatched = 0L;

        Long balance = totalShipped - totalWorked - totalDispatched;

        LabOrderEntity order = labOrdersRepository.findByOrderName(orderName)
                .orElseThrow(() -> new RuntimeException("Заказ с номером " + orderName + " не найден"));

        order.setAmount(balance);
        labOrdersRepository.save(order);

        return balance;
    }
}