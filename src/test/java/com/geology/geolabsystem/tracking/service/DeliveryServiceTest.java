package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.DispatchRequestDto;
import com.geology.geolabsystem.tracking.entity.DispatchEntity;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import com.geology.geolabsystem.tracking.exception.IncompleteDeliveryDataException;
import com.geology.geolabsystem.tracking.exception.InsufficientBalanceException;
import com.geology.geolabsystem.tracking.exception.OrderNotFoundException;
import com.geology.geolabsystem.tracking.repository.DispatchesRepository;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
import com.geology.geolabsystem.tracking.mapper.DispatchesMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {
    @Mock
    private DispatchesRepository dispatchesRepository;
    @Mock
    private LabOrdersRepository labOrdersRepository;
    @Mock
    private DispatchesMapper mapper;
    @Mock
    private OrderControlService orderControlService;

    @InjectMocks
    private DeliveryService deliveryService;

    @Captor
    private ArgumentCaptor<List<DispatchEntity>> dispatchListCaptor;

    private LabOrderEntity testOrder;
    private DispatchRequestDto dispatchDto;

    @BeforeEach
    void setUp() {
        testOrder = new LabOrderEntity();
        testOrder.setOrderName("0924Д");
        testOrder.setDescription("Мосты");
        testOrder.setGeologistName("Иванов");
        testOrder.setAmount(100L);

        dispatchDto = new DispatchRequestDto();
        dispatchDto.setOrderName("0924Д");
        dispatchDto.setDescription("Мосты");
        dispatchDto.setGeologistName("Иванов");
        dispatchDto.setAmount(30L);
        dispatchDto.setDispatchDate(LocalDate.now());
    }

    @Test
    @DisplayName("Успешная регистрация отгрузки и пересчет баланса")
    void registerDispatches_Success() {

        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(testOrder));

        DispatchEntity mockEntity = new DispatchEntity();
        when(mapper.toEntity(any(DispatchRequestDto.class))).thenReturn(mockEntity);
        when(dispatchesRepository.saveAll(anyList())).thenReturn(List.of(mockEntity));

        deliveryService.registerDispatches(List.of(dispatchDto));

        verify(dispatchesRepository, times(1)).saveAll(dispatchListCaptor.capture());
        assertEquals(1, dispatchListCaptor.getValue().size());

        verify(orderControlService, times(1)).calculateBalance(
                dispatchDto.getOrderName(),
                dispatchDto.getDescription(),
                dispatchDto.getGeologistName()
        );
    }

    @Test
    @DisplayName("Ошибка: Недостаточно монолитов для переброски")
    void registerDispatches_InsufficientBalance() {

        testOrder.setAmount(20L);
        dispatchDto.setAmount(50L);

        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(testOrder));

        IncompleteDeliveryDataException ex = assertThrows(IncompleteDeliveryDataException.class, () ->
                deliveryService.registerDispatches(List.of(dispatchDto))
        );

        assertAll(
                () -> assertTrue(ex.getMessage().contains("Ошибка переброски")),
                () -> assertTrue(ex.getMessage().contains("Недостаточно монолитов")),
                () -> assertTrue(ex.getMessage().contains("20")),
                () -> assertTrue(ex.getMessage().contains("50"))
        );

        verifyNoInteractions(dispatchesRepository);
        verifyNoInteractions(orderControlService);
    }

    @Test
    @DisplayName("Ошибка: Заказ для отгрузки не найден в базе")
    void registerDispatches_OrderNotFound() {

        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                deliveryService.registerDispatches(List.of(dispatchDto))
        );

        verifyNoInteractions(dispatchesRepository);
    }

    @Test
    @DisplayName("Передача пустого списка не должна вызывать работу с базой")
    void registerDispatches_EmptyList() {

        deliveryService.registerDispatches(Collections.emptyList());

        verifyNoInteractions(labOrdersRepository);
        verifyNoInteractions(dispatchesRepository);
    }

    @Test
    @DisplayName("Передача null должна обрабатываться корректно")
    void registerDispatches_NullList() {

        deliveryService.registerDispatches(null);

        verifyNoInteractions(labOrdersRepository);
        verifyNoInteractions(dispatchesRepository);
    }
}
