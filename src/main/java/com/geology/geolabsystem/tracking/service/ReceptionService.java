package com.geology.geolabsystem.tracking.service;


import com.geology.geolabsystem.tracking.dto.request.ShipmentRequestDto;
import com.geology.geolabsystem.tracking.dto.response.ShipmentResponseDto;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import com.geology.geolabsystem.tracking.entity.ShipmentEntity;
import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import com.geology.geolabsystem.tracking.exception.ShipmentNotFoundException;
import com.geology.geolabsystem.tracking.mapper.LabOrdersMapper;
import com.geology.geolabsystem.tracking.mapper.ShipmentsMapper;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
import com.geology.geolabsystem.tracking.repository.ShipmentsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceptionService {

    private final ShipmentsMapper shipmentsMapper;
    private final LabOrdersMapper labOrdersMapper;
    private final ShipmentsRepository shipmentsRepository;
    private final OrderControlService orderControlService;
    private final LabOrdersRepository labOrdersRepository;

    @Transactional
    public List<ShipmentResponseDto> registerShipments(List<ShipmentRequestDto> dtos) {

        if (dtos == null || dtos.isEmpty()) return List.of();
        log.info("Регистрация поставки: {} заказов", dtos.size());

        List<ShipmentEntity> shipmentsToSave = new ArrayList<>();

        String currentShippingdId = java.util.UUID.randomUUID().toString();
        for (ShipmentRequestDto shipDto : dtos) {
            LabOrderEntity order = labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName
                            (shipDto.getOrderName(), shipDto.getDescription(), shipDto.getGeologistName())
                    .orElseGet(() -> {
                        LabOrderEntity newOrder = labOrdersMapper.toEntityFromShipment(shipDto);
                        newOrder.setStatus(OrderStatus.CREATED);
                        newOrder.setAmount(0L);
                        return labOrdersRepository.save(newOrder);
                    });

            ShipmentEntity shipment = shipmentsMapper.toEntity(shipDto);
            shipment.setLabOrderEntity(order);
            shipment.setShippingId(currentShippingdId);

            shipmentsToSave.add(shipment);
        }

        List<ShipmentEntity> savedShipments = shipmentsRepository.saveAll(shipmentsToSave);

        dtos.stream()
                .map(dto -> List.of(dto.getOrderName(), dto.getDescription(), dto.getGeologistName()))
                .distinct()
                .forEach(triple -> orderControlService.calculateBalance(
                        triple.get(0),
                        triple.get(1),
                        triple.get(2)
                ));

        return savedShipments.stream()
                .map(shipmentsMapper::toResponseDto)
                .toList();
    }

    public Page<ShipmentResponseDto> getAllShipments(Pageable pageable) {
        return shipmentsRepository.findAll(pageable)
                .map(shipmentsMapper::toResponseDto);
    }

    public ShipmentResponseDto getShipmentsById(Long id) {
        return shipmentsRepository.findById(id)
                .map(shipmentsMapper::toResponseDto)
                .orElseThrow(() -> new ShipmentNotFoundException(id));
    }
}
