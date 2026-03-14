package com.geology.geolabsystem.tracking.service;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.geology.geolabsystem.tracking.dto.request.ShipmentRequestDto;
import com.geology.geolabsystem.tracking.dto.response.LabOrderResponseDto;
import com.geology.geolabsystem.tracking.dto.response.ShipmentResponseDto;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import com.geology.geolabsystem.tracking.entity.ShipmentEntity;
import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import com.geology.geolabsystem.tracking.mapper.LabOrdersMapper;
import com.geology.geolabsystem.tracking.mapper.ShipmentsMapper;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
import com.geology.geolabsystem.tracking.repository.ShipmentsRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceptionService {

    private final ShipmentsMapper shipmentsMapper;
    private final LabOrdersMapper labOrdersMapper;
    private final ShipmentsRepository shipmentsRepository;
    private final OrderControlService orderControlService;
    private final LabOrdersRepository labOrdersRepository;

    @Transactional
    public ShipmentResponseDto registerShipment (ShipmentRequestDto dto) {

        // 1. Найти или АВТОСОЗДАТЬ Order
        LabOrderEntity order = labOrdersRepository.findByOrderName(dto.getOrderName())
                .orElseGet(() -> {
                    LabOrderEntity newOrder = labOrdersMapper.toEntityFromShipment(dto);
                    newOrder.setStatus(OrderStatus.CREATED);
                    newOrder.setAmount(0L);
                    return labOrdersRepository.save(newOrder);
                });

        ShipmentEntity shipment = shipmentsMapper.toEntity(dto);
        shipment.setLabOrderEntity(order); // Связываем с заказом

        ShipmentEntity saved = shipmentsRepository.save(shipment);

        // 3. Пересчитать баланс
        orderControlService.calculateBalance(dto.getOrderName());

        // 4. Вернуть Response (Маппер сам соберет данные из shipment и вложенного order)
        return shipmentsMapper.toResponseDto(saved);
    }

    public List<ShipmentResponseDto> getAllShipments() {
        return shipmentsRepository.findAll()
                .stream()
                .map(shipmentsMapper::toResponseDto)
                .toList();

    }

    public ShipmentResponseDto getShipmentsById(Long id) {
        return shipmentsRepository.findById(id)
                .map(shipmentsMapper::toResponseDto)
                .orElseThrow(() -> new RuntimeException("Поставка с ID " + id + " не найдена"));
    }
}
