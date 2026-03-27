package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.DispatchRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DispatchResponseDto;
import com.geology.geolabsystem.tracking.entity.DispatchEntity;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import com.geology.geolabsystem.tracking.exception.DeliveryNotFoundException;
import com.geology.geolabsystem.tracking.exception.IncompleteDeliveryDataException;
import com.geology.geolabsystem.tracking.exception.OrderNotFoundException;
import com.geology.geolabsystem.tracking.mapper.DispatchesMapper;
import com.geology.geolabsystem.tracking.repository.DispatchesRepository;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
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
public class DeliveryService {

    private final DispatchesRepository dispatchesRepository;
    private final DispatchesMapper mapper;
    private final OrderControlService orderControlService;
    private final LabOrdersRepository labOrdersRepository;

    @Transactional
    public List<DispatchResponseDto> registerDispatches(List<DispatchRequestDto> dtos) {

        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }

        log.info("Регистрация отправки: {} заказов", dtos.size());

        List<DispatchEntity> dispatchesToSave = new ArrayList<>();

        String currentSendingId = java.util.UUID.randomUUID().toString();
        for (DispatchRequestDto dispatchDto : dtos) {
            LabOrderEntity order = labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                            dispatchDto.getOrderName(),
                            dispatchDto.getDescription(),
                            dispatchDto.getGeologistName()
                    )
                    .orElseThrow(() -> new OrderNotFoundException(
                            "",
                            dispatchDto.getOrderName(),
                            dispatchDto.getDescription(),
                            dispatchDto.getGeologistName()
                    ));

            if (order.getAmount() < dispatchDto.getAmount()) {

                log.error("Недостаточно монолитов для заказа {}: доступно {}, запрошено {}",
                        order.getOrderName(), order.getAmount(), dispatchDto.getAmount());

                throw new IncompleteDeliveryDataException(
                        order.getOrderName(),
                        order.getDescription(),
                        order.getGeologistName(),
                        order.getAmount(),
                        dispatchDto.getAmount()
                );
            }

            DispatchEntity dispatch = mapper.toEntity(dispatchDto);
            dispatch.setLabOrderEntity(order);
            dispatch.setSendingId(currentSendingId);
            dispatchesToSave.add(dispatch);
        }

        List<DispatchEntity> savedDispatches = dispatchesRepository.saveAll(dispatchesToSave);

        dtos.stream()
                .map(dto -> List.of(dto.getOrderName(), dto.getDescription(), dto.getGeologistName()))
                .distinct()
                .forEach(triple -> orderControlService.calculateBalance(
                        triple.get(0),
                        triple.get(1),
                        triple.get(2)
                ));

        log.info("Отправка зарегистрирована: {} записей", savedDispatches.size());
        return savedDispatches.stream()
                .map(mapper::toResponseDto)
                .toList();

    }

    public Page<DispatchResponseDto> getAllDispatch(Pageable pageable) {
        return dispatchesRepository.findAll(pageable)
                .map(mapper::toResponseDto);
    }

    public DispatchResponseDto getDispatchById(Long id) {
        DispatchEntity entity = dispatchesRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
        return mapper.toResponseDto(entity);
    }
}

