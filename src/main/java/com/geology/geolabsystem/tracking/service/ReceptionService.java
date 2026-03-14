package com.geology.geolabsystem.tracking.service;


import com.geology.geolabsystem.tracking.dto.request.ShipmentRequestDto;
import com.geology.geolabsystem.tracking.dto.response.ShipmentResponseDto;
import com.geology.geolabsystem.tracking.entity.ShipmentEntity;
import com.geology.geolabsystem.tracking.mapper.ShipmentsMapper;
import com.geology.geolabsystem.tracking.repository.ShipmentsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceptionService {

    private final ShipmentsMapper mapper;
    private final ShipmentsRepository shipmentsRepository;
    private final OrderControlService orderControlService;

    @Transactional
    public ShipmentResponseDto registerShipment (ShipmentRequestDto dto) {
        ShipmentEntity entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        ShipmentEntity saved = shipmentsRepository.save(entity);

        Long newBalance = orderControlService.calculateBalance(saved.getLabOrderEntity().getOrderName());

        ShipmentResponseDto response = mapper.toResponseDto(saved);
        response.setAmount(newBalance);
        return response;
    }

    public List<ShipmentResponseDto> getAllShipments() {
        return shipmentsRepository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

    }

    public ShipmentResponseDto getShipmentsById(Long id) {
        ShipmentEntity entity = shipmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Поставка с таким id не найдена: " + id));
        return mapper.toResponseDto(entity);
    }
}
