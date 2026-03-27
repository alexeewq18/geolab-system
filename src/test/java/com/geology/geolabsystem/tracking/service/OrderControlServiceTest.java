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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControlServiceTest {

    @Mock
    private LabOrdersRepository labOrdersRepository;
    @Mock
    private DailyWorksRepository dailyWorksRepository;
    @Mock
    private ShipmentsRepository shipmentsRepository;
    @Mock
    private DispatchesRepository dispatchesRepository;
    @Mock
    private LabOrdersMapper mapper;

    @InjectMocks
    private OrderControlService orderControlService;

    @Captor
    private ArgumentCaptor<LabOrderEntity> orderCaptor;

    private LabOrderEntity testOrder;
    private LabOrderRequestDto requestDto;
    private LabOrderResponseDto responseDto;

    @BeforeEach
    void setUp() {
        testOrder = new LabOrderEntity();
        testOrder.setOrderName("0924Д");
        testOrder.setDescription("Мосты");
        testOrder.setGeologistName("Иванов");
        testOrder.setAmount(100L);
        testOrder.setStatus(OrderStatus.CREATED);

        requestDto = new LabOrderRequestDto();
        requestDto.setOrderName("0924Д");
        requestDto.setDescription("Мосты");
        requestDto.setGeologistName("Иванов");

        responseDto = new LabOrderResponseDto();
        responseDto.setOrderName("0924Д");
        responseDto.setDescription("Мосты");
        responseDto.setGeologistName("Иванов");
    }

    @Test
    @DisplayName("Успешное создание заказа: статус CREATED, amount = 0")
    void createOrder_Success() {
        when(mapper.toEntity(any(LabOrderRequestDto.class))).thenReturn(testOrder);
        when(labOrdersRepository.save(any(LabOrderEntity.class))).thenReturn(testOrder);
        when(mapper.toResponseDto(any(LabOrderEntity.class))).thenReturn(responseDto);

        LabOrderResponseDto result = orderControlService.createOrder(requestDto);

        assertNotNull(result);
        assertEquals("0924Д", result.getOrderName());

        verify(labOrdersRepository, times(1)).save(orderCaptor.capture());
        LabOrderEntity captured = orderCaptor.getValue();
        assertAll(
                () -> assertEquals(OrderStatus.CREATED, captured.getStatus()),
                () -> assertEquals(0L, captured.getAmount())
        );
    }

    @Test
    @DisplayName("Получение всех заказов: непустой список")
    void getAllOrders_ReturnsList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LabOrderEntity> page = new PageImpl<>(List.of(testOrder));

        when(labOrdersRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponseDto(any(LabOrderEntity.class))).thenReturn(responseDto);

        Page<LabOrderResponseDto> result = orderControlService.getAllOrders(pageable);

        assertEquals(1, result.getTotalElements());
        verify(labOrdersRepository, times(1)).findAll(pageable);
        verify(mapper, times(1)).toResponseDto(any(LabOrderEntity.class));
    }

    @Test
    @DisplayName("Получение всех заказов: пустой список")
    void getAllOrders_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LabOrderEntity> page = new PageImpl<>(List.of(testOrder));

        when(labOrdersRepository.findAll(pageable)).thenReturn(page);

        Page<LabOrderResponseDto> result = orderControlService.getAllOrders(pageable);

        assertTrue(result.isEmpty());
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Успешное получение заказа по ID")
    void getOrderById_Success() {
        when(labOrdersRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(mapper.toResponseDto(any(LabOrderEntity.class))).thenReturn(responseDto);

        LabOrderResponseDto result = orderControlService.getOrderById(1L);

        assertNotNull(result);
        assertEquals("0924Д", result.getOrderName());
        verify(labOrdersRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Ошибка: заказ по ID не найден")
    void getOrderById_NotFound() {
        when(labOrdersRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderControlService.getOrderById(999L)
        );

        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Успешное получение заказа по деталям")
    void getOrderByDetails_Success() {
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(testOrder));
        when(mapper.toResponseDto(any(LabOrderEntity.class))).thenReturn(responseDto);

        LabOrderResponseDto result = orderControlService.getOrderByDetails(
                "0924Д", "Мосты", "Иванов");

        assertNotNull(result);
        assertEquals("Мосты", result.getDescription());
    }

    @Test
    @DisplayName("Ошибка: заказ по деталям не найден")
    void getOrderByDetails_NotFound() {
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderControlService.getOrderByDetails("0924Д", "Мосты", "Иванов")
        );

        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Пересчёт баланса: положительный остаток, статус IN_PROGRESS")
    void calculateBalance_PositiveBalance_InProgress() {
        stubSums(100L, 30L, 20L);
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(testOrder));

        Long balance = orderControlService.calculateBalance("0924Д", "Мосты", "Иванов");

        assertAll(
                () -> assertEquals(50L, balance),
                () -> assertEquals(50L, testOrder.getAmount()),
                () -> assertEquals(OrderStatus.IN_PROGRESS, testOrder.getStatus())
        );
    }

    @Test
    @DisplayName("Пересчёт баланса: нулевой остаток при наличии операций, статус COMPLETED")
    void calculateBalance_ZeroBalance_Completed() {
        stubSums(100L, 80L, 20L);
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(testOrder));

        Long balance = orderControlService.calculateBalance("0924Д", "Мосты", "Иванов");

        assertAll(
                () -> assertEquals(0L, balance),
                () -> assertEquals(0L, testOrder.getAmount()),
                () -> assertEquals(OrderStatus.COMPLETED, testOrder.getStatus())
        );
    }

    @Test
    @DisplayName("Пересчёт баланса: все суммы null — баланс 0, статус не меняется")
    void calculateBalance_AllNulls_StatusUnchanged() {
        stubSums(null, null, null);
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(testOrder));

        Long balance = orderControlService.calculateBalance("0924Д", "Мосты", "Иванов");

        assertAll(
                () -> assertEquals(0L, balance),
                () -> assertEquals(0L, testOrder.getAmount()),
                () -> assertEquals(OrderStatus.CREATED, testOrder.getStatus())
        );
    }

    @Test
    @DisplayName("Пересчёт баланса: только отгрузки, без работ и перебросок")
    void calculateBalance_OnlyShipped() {
        stubSums(50L, 0L, 0L);
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(testOrder));

        Long balance = orderControlService.calculateBalance("0924Д", "Мосты", "Иванов");

        assertAll(
                () -> assertEquals(50L, balance),
                () -> assertEquals(50L, testOrder.getAmount())
        );
    }

    @Test
    @DisplayName("Ошибка пересчёта: заказ не найден в базе")
    void calculateBalance_OrderNotFound() {
        stubSums(100L, 30L, 20L);
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        OrderNotFoundException ex = assertThrows(OrderNotFoundException.class, () ->
                orderControlService.calculateBalance("0924Д", "Мосты", "Иванов")
        );

        assertTrue(ex.getMessage().contains("Ошибка пересчета"));
    }

    private void stubSums(Long shipped, Long worked, Long dispatched) {
        when(shipmentsRepository.sumAmountByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString())).thenReturn(shipped);
        when(dailyWorksRepository.sumAmountByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString())).thenReturn(worked);
        when(dispatchesRepository.sumAmountByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString())).thenReturn(dispatched);
    }
}