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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceptionServiceTest {

    @Mock
    private ShipmentsMapper shipmentsMapper;
    @Mock
    private LabOrdersMapper labOrdersMapper;
    @Mock
    private ShipmentsRepository shipmentsRepository;
    @Mock
    private LabOrdersRepository labOrdersRepository;
    @Mock
    private OrderControlService orderControlService;

    @InjectMocks
    private ReceptionService receptionService;

    @Captor
    private ArgumentCaptor<List<ShipmentEntity>> shipmentListCaptor;

    private LabOrderEntity existingOrder;
    private ShipmentRequestDto shipmentDto;
    private ShipmentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        existingOrder = new LabOrderEntity();
        existingOrder.setOrderName("0924Д");
        existingOrder.setDescription("Мосты");
        existingOrder.setGeologistName("Иванов");
        existingOrder.setAmount(100L);
        existingOrder.setStatus(OrderStatus.IN_PROGRESS);

        shipmentDto = new ShipmentRequestDto();
        shipmentDto.setOrderName("0924Д");
        shipmentDto.setDescription("Мосты");
        shipmentDto.setGeologistName("Иванов");
        shipmentDto.setAmount(30L);
        shipmentDto.setShipmentDate(LocalDate.now());

        responseDto = new ShipmentResponseDto();
        responseDto.setOrderName("0924Д");
        responseDto.setDescription("Мосты");
        responseDto.setGeologistName("Иванов");
        responseDto.setAmount(30L);
    }

    @Test
    @DisplayName("Успешная регистрация поставки: заказ уже существует в базе")
    void registerShipments_ExistingOrder_Success() {
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(existingOrder));

        ShipmentEntity mockEntity = new ShipmentEntity();
        when(shipmentsMapper.toEntity(any(ShipmentRequestDto.class))).thenReturn(mockEntity);
        when(shipmentsRepository.saveAll(anyList())).thenReturn(List.of(mockEntity));
        when(shipmentsMapper.toResponseDto(any(ShipmentEntity.class))).thenReturn(responseDto);

        List<ShipmentResponseDto> result = receptionService.registerShipments(List.of(shipmentDto));

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("0924Д", result.get(0).getOrderName())
        );

        verify(shipmentsRepository, times(1)).saveAll(shipmentListCaptor.capture());
        assertEquals(1, shipmentListCaptor.getValue().size());

        verify(labOrdersRepository, never()).save(any(LabOrderEntity.class));

        verify(orderControlService, times(1)).calculateBalance(
                "0924Д", "Мосты", "Иванов"
        );
    }

    @Test
    @DisplayName("Успешная регистрация поставки: заказ не найден — создаётся новый")
    void registerShipments_NewOrderCreated() {
        LabOrderEntity newOrder = new LabOrderEntity();
        newOrder.setOrderName("0924Д");
        newOrder.setDescription("Мосты");
        newOrder.setGeologistName("Иванов");
        newOrder.setStatus(OrderStatus.CREATED);
        newOrder.setAmount(0L);

        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(labOrdersMapper.toEntityFromShipment(any(ShipmentRequestDto.class))).thenReturn(newOrder);
        when(labOrdersRepository.save(any(LabOrderEntity.class))).thenReturn(newOrder);

        ShipmentEntity mockEntity = new ShipmentEntity();
        when(shipmentsMapper.toEntity(any(ShipmentRequestDto.class))).thenReturn(mockEntity);
        when(shipmentsRepository.saveAll(anyList())).thenReturn(List.of(mockEntity));
        when(shipmentsMapper.toResponseDto(any(ShipmentEntity.class))).thenReturn(responseDto);

        List<ShipmentResponseDto> result = receptionService.registerShipments(List.of(shipmentDto));

        assertEquals(1, result.size());

        ArgumentCaptor<LabOrderEntity> orderCaptor = ArgumentCaptor.forClass(LabOrderEntity.class);
        verify(labOrdersRepository, times(1)).save(orderCaptor.capture());
        LabOrderEntity captured = orderCaptor.getValue();
        assertAll(
                () -> assertEquals(OrderStatus.CREATED, captured.getStatus()),
                () -> assertEquals(0L, captured.getAmount())
        );

        verify(orderControlService, times(1)).calculateBalance(
                "0924Д", "Мосты", "Иванов"
        );
    }

    @Test
    @DisplayName("Несколько поставок на один заказ — calculateBalance вызывается один раз")
    void registerShipments_DuplicateOrderDetails_SingleBalanceCall() {
        ShipmentRequestDto secondDto = new ShipmentRequestDto();
        secondDto.setOrderName("0924Д");
        secondDto.setDescription("Мосты");
        secondDto.setGeologistName("Иванов");
        secondDto.setAmount(20L);
        secondDto.setShipmentDate(LocalDate.now());

        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(existingOrder));

        ShipmentEntity entity1 = new ShipmentEntity();
        ShipmentEntity entity2 = new ShipmentEntity();
        when(shipmentsMapper.toEntity(any(ShipmentRequestDto.class)))
                .thenReturn(entity1, entity2);
        when(shipmentsRepository.saveAll(anyList())).thenReturn(List.of(entity1, entity2));
        when(shipmentsMapper.toResponseDto(any(ShipmentEntity.class))).thenReturn(responseDto);

        List<ShipmentResponseDto> result = receptionService.registerShipments(
                List.of(shipmentDto, secondDto));

        assertEquals(2, result.size());

        verify(shipmentsRepository, times(1)).saveAll(shipmentListCaptor.capture());
        assertEquals(2, shipmentListCaptor.getValue().size());

        verify(orderControlService, times(1)).calculateBalance(
                "0924Д", "Мосты", "Иванов"
        );
    }

    @Test
    @DisplayName("Поставки на разные заказы — calculateBalance вызывается для каждого")
    void registerShipments_DifferentOrders_MultipleBalanceCalls() {
        ShipmentRequestDto anotherDto = new ShipmentRequestDto();
        anotherDto.setOrderName("1024Д");
        anotherDto.setDescription("Тоннели");
        anotherDto.setGeologistName("Петров");
        anotherDto.setAmount(15L);
        anotherDto.setShipmentDate(LocalDate.now());

        LabOrderEntity anotherOrder = new LabOrderEntity();
        anotherOrder.setOrderName("1024Д");
        anotherOrder.setDescription("Тоннели");
        anotherOrder.setGeologistName("Петров");
        anotherOrder.setAmount(50L);
        anotherOrder.setStatus(OrderStatus.IN_PROGRESS);

        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                eq("0924Д"), eq("Мосты"), eq("Иванов")))
                .thenReturn(Optional.of(existingOrder));
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                eq("1024Д"), eq("Тоннели"), eq("Петров")))
                .thenReturn(Optional.of(anotherOrder));

        ShipmentEntity entity1 = new ShipmentEntity();
        ShipmentEntity entity2 = new ShipmentEntity();
        when(shipmentsMapper.toEntity(any(ShipmentRequestDto.class)))
                .thenReturn(entity1, entity2);
        when(shipmentsRepository.saveAll(anyList())).thenReturn(List.of(entity1, entity2));
        when(shipmentsMapper.toResponseDto(any(ShipmentEntity.class))).thenReturn(responseDto);

        receptionService.registerShipments(List.of(shipmentDto, anotherDto));

        verify(orderControlService, times(1)).calculateBalance(
                "0924Д", "Мосты", "Иванов");
        verify(orderControlService, times(1)).calculateBalance(
                "1024Д", "Тоннели", "Петров");
    }

    @Test
    @DisplayName("У каждой поставки проставляется единый shippingId")
    void registerShipments_SameShippingIdForBatch() {
        ShipmentRequestDto secondDto = new ShipmentRequestDto();
        secondDto.setOrderName("0924Д");
        secondDto.setDescription("Мосты");
        secondDto.setGeologistName("Иванов");
        secondDto.setAmount(10L);
        secondDto.setShipmentDate(LocalDate.now());

        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(existingOrder));

        ShipmentEntity entity1 = new ShipmentEntity();
        ShipmentEntity entity2 = new ShipmentEntity();
        when(shipmentsMapper.toEntity(any(ShipmentRequestDto.class)))
                .thenReturn(entity1, entity2);
        when(shipmentsRepository.saveAll(anyList())).thenReturn(List.of(entity1, entity2));
        when(shipmentsMapper.toResponseDto(any(ShipmentEntity.class))).thenReturn(responseDto);

        receptionService.registerShipments(List.of(shipmentDto, secondDto));

        verify(shipmentsRepository).saveAll(shipmentListCaptor.capture());
        List<ShipmentEntity> saved = shipmentListCaptor.getValue();

        assertAll(
                () -> assertNotNull(saved.get(0).getShippingId()),
                () -> assertNotNull(saved.get(1).getShippingId()),
                () -> assertEquals(saved.get(0).getShippingId(), saved.get(1).getShippingId())
        );
    }

    @Test
    @DisplayName("Передача пустого списка не вызывает работу с базой")
    void registerShipments_EmptyList() {
        List<ShipmentResponseDto> result = receptionService.registerShipments(Collections.emptyList());

        assertTrue(result.isEmpty());
        verifyNoInteractions(labOrdersRepository);
        verifyNoInteractions(shipmentsRepository);
        verifyNoInteractions(orderControlService);
    }

    @Test
    @DisplayName("Передача null не вызывает работу с базой")
    void registerShipments_NullList() {
        List<ShipmentResponseDto> result = receptionService.registerShipments(null);

        assertTrue(result.isEmpty());
        verifyNoInteractions(labOrdersRepository);
        verifyNoInteractions(shipmentsRepository);
        verifyNoInteractions(orderControlService);
    }

    @Test
    @DisplayName("Получение всех поставок: непустой список")
    void getAllShipments_ReturnsList() {
        Pageable pageable = PageRequest.of(0, 10);
        ShipmentEntity entity = new ShipmentEntity();
        Page<ShipmentEntity> page = new PageImpl<>(List.of(entity));

        when(shipmentsRepository.findAll(pageable)).thenReturn(page);
        when(shipmentsMapper.toResponseDto(any(ShipmentEntity.class))).thenReturn(responseDto);

        Page<ShipmentResponseDto> result = receptionService.getAllShipments(pageable);

        assertEquals(1, result.getTotalElements());
        verify(shipmentsRepository, times(1)).findAll(pageable);
        verify(shipmentsMapper, times(1)).toResponseDto(any(ShipmentEntity.class));
    }

    @Test
    @DisplayName("Получение всех поставок: пустой список")
    void getAllShipments_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ShipmentEntity> emptyPage = new PageImpl<>(List.of());

        when(shipmentsRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<ShipmentResponseDto> result = receptionService.getAllShipments(pageable);

        assertTrue(result.isEmpty());
        verify(shipmentsMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Успешное получение поставки по ID")
    void getShipmentsById_Success() {
        ShipmentEntity entity = new ShipmentEntity();
        when(shipmentsRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(shipmentsMapper.toResponseDto(any(ShipmentEntity.class))).thenReturn(responseDto);

        ShipmentResponseDto result = receptionService.getShipmentsById(1L);

        assertNotNull(result);
        assertEquals("0924Д", result.getOrderName());
        verify(shipmentsRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Ошибка: поставка по ID не найдена")
    void getShipmentsById_NotFound() {
        when(shipmentsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFoundException.class, () ->
                receptionService.getShipmentsById(999L)
        );

        verify(shipmentsMapper, never()).toResponseDto(any());
    }
}