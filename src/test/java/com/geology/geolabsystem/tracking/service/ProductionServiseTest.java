package com.geology.geolabsystem.tracking.service;

import com.geology.geolabsystem.tracking.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.entity.DailyWorksEntity;
import com.geology.geolabsystem.tracking.entity.LabOrderEntity;
import com.geology.geolabsystem.tracking.entity.enums.OrderStatus;
import com.geology.geolabsystem.tracking.exception.OrderNotFoundException;
import com.geology.geolabsystem.tracking.mapper.DailyWorksMapper;
import com.geology.geolabsystem.tracking.repository.DailyWorksRepository;
import com.geology.geolabsystem.tracking.repository.LabOrdersRepository;
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
class ProductionServiceTest {

    @Mock
    private DailyWorksRepository dailyWorksRepository;
    @Mock
    private DailyWorksMapper mapper;
    @Mock
    private OrderControlService orderControlService;
    @Mock
    private LabOrdersRepository labOrdersRepository;

    @InjectMocks
    private ProductionService productionService;

    @Captor
    private ArgumentCaptor<List<DailyWorksEntity>> worksListCaptor;

    private LabOrderEntity existingOrder;
    private DailyWorksRequestDto requestDto;
    private DailyWorksResponseDto responseDto;

    @BeforeEach
    void setUp() {
        existingOrder = new LabOrderEntity();
        existingOrder.setOrderName("0924Д");
        existingOrder.setDescription("Мосты");
        existingOrder.setGeologistName("Иванов");
        existingOrder.setAmount(100L);
        existingOrder.setStatus(OrderStatus.IN_PROGRESS);

        requestDto = new DailyWorksRequestDto();
        requestDto.setOrderName("0924Д");
        requestDto.setDescription("Мосты");
        requestDto.setGeologistName("Иванов");
        requestDto.setAmount(10L);
        requestDto.setWorkDate(LocalDate.now());

        responseDto = new DailyWorksResponseDto();
        responseDto.setOrderName("0924Д");
        responseDto.setDescription("Мосты");
        responseDto.setGeologistName("Иванов");
        responseDto.setAmount(10L);
    }

    // ==================== registerDailyWorks ====================

    @Test
    @DisplayName("Успешная регистрация работ: заказ найден, баланс пересчитан")
    void registerDailyWorks_Success() {
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(existingOrder));

        DailyWorksEntity mockEntity = new DailyWorksEntity();
        when(mapper.toEntity(any(DailyWorksRequestDto.class))).thenReturn(mockEntity);
        when(dailyWorksRepository.saveAll(anyList())).thenReturn(List.of(mockEntity));
        when(mapper.toResponseDtoList(anyList())).thenReturn(List.of(responseDto));

        List<DailyWorksResponseDto> result = productionService.registerDailyWorks(List.of(requestDto));

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("0924Д", result.get(0).getOrderName())
        );

        verify(dailyWorksRepository, times(1)).saveAll(worksListCaptor.capture());
        assertEquals(1, worksListCaptor.getValue().size());

        verify(orderControlService, times(1)).calculateBalance(
                "0924Д", "Мосты", "Иванов"
        );
    }

    @Test
    @DisplayName("Ошибка: заказ не найден — OrderNotFoundException")
    void registerDailyWorks_OrderNotFound() {
        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        OrderNotFoundException ex = assertThrows(OrderNotFoundException.class, () ->
                productionService.registerDailyWorks(List.of(requestDto))
        );

        assertTrue(ex.getMessage().contains("Ошибка фиксации работ"));

        verifyNoInteractions(dailyWorksRepository);
        verifyNoInteractions(orderControlService);
    }

    @Test
    @DisplayName("Две работы на один заказ — calculateBalance вызывается один раз (Set)")
    void registerDailyWorks_SameOrder_SingleBalanceCall() {
        DailyWorksRequestDto secondDto = new DailyWorksRequestDto();
        secondDto.setOrderName("0924Д");
        secondDto.setDescription("Мосты");
        secondDto.setGeologistName("Иванов");
        secondDto.setAmount(5L);
        secondDto.setWorkDate(LocalDate.now());

        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(existingOrder));

        DailyWorksEntity entity1 = new DailyWorksEntity();
        DailyWorksEntity entity2 = new DailyWorksEntity();
        when(mapper.toEntity(any(DailyWorksRequestDto.class)))
                .thenReturn(entity1, entity2);
        when(dailyWorksRepository.saveAll(anyList())).thenReturn(List.of(entity1, entity2));
        when(mapper.toResponseDtoList(anyList())).thenReturn(List.of(responseDto, responseDto));

        List<DailyWorksResponseDto> result = productionService.registerDailyWorks(
                List.of(requestDto, secondDto));

        assertEquals(2, result.size());

        verify(dailyWorksRepository, times(1)).saveAll(worksListCaptor.capture());
        assertEquals(2, worksListCaptor.getValue().size());

        // один и тот же заказ в Set → пересчёт один раз
        verify(orderControlService, times(1)).calculateBalance(
                "0924Д", "Мосты", "Иванов"
        );
    }

    @Test
    @DisplayName("Работы на разные заказы — calculateBalance для каждого")
    void registerDailyWorks_DifferentOrders_MultipleBalanceCalls() {
        DailyWorksRequestDto anotherDto = new DailyWorksRequestDto();
        anotherDto.setOrderName("1024Д");
        anotherDto.setDescription("Тоннели");
        anotherDto.setGeologistName("Петров");
        anotherDto.setAmount(7L);
        anotherDto.setWorkDate(LocalDate.now());

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

        DailyWorksEntity entity1 = new DailyWorksEntity();
        DailyWorksEntity entity2 = new DailyWorksEntity();
        when(mapper.toEntity(any(DailyWorksRequestDto.class)))
                .thenReturn(entity1, entity2);
        when(dailyWorksRepository.saveAll(anyList())).thenReturn(List.of(entity1, entity2));
        when(mapper.toResponseDtoList(anyList())).thenReturn(List.of(responseDto, responseDto));

        productionService.registerDailyWorks(List.of(requestDto, anotherDto));

        verify(orderControlService, times(1)).calculateBalance(
                "0924Д", "Мосты", "Иванов");
        verify(orderControlService, times(1)).calculateBalance(
                "1024Д", "Тоннели", "Петров");
    }

    @Test
    @DisplayName("У всех работ в пачке проставляется единый workDayId")
    void registerDailyWorks_SameWorkDayIdForBatch() {
        DailyWorksRequestDto secondDto = new DailyWorksRequestDto();
        secondDto.setOrderName("0924Д");
        secondDto.setDescription("Мосты");
        secondDto.setGeologistName("Иванов");
        secondDto.setAmount(3L);
        secondDto.setWorkDate(LocalDate.now());

        when(labOrdersRepository.findByOrderNameAndDescriptionAndGeologistName(
                anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(existingOrder));

        DailyWorksEntity entity1 = new DailyWorksEntity();
        DailyWorksEntity entity2 = new DailyWorksEntity();
        when(mapper.toEntity(any(DailyWorksRequestDto.class)))
                .thenReturn(entity1, entity2);
        when(dailyWorksRepository.saveAll(anyList())).thenReturn(List.of(entity1, entity2));
        when(mapper.toResponseDtoList(anyList())).thenReturn(List.of(responseDto, responseDto));

        productionService.registerDailyWorks(List.of(requestDto, secondDto));

        verify(dailyWorksRepository).saveAll(worksListCaptor.capture());
        List<DailyWorksEntity> saved = worksListCaptor.getValue();

        assertAll(
                () -> assertNotNull(saved.get(0).getWorkDayId()),
                () -> assertNotNull(saved.get(1).getWorkDayId()),
                () -> assertEquals(saved.get(0).getWorkDayId(), saved.get(1).getWorkDayId())
        );
    }

    @Test
    @DisplayName("Передача пустого списка не вызывает работу с базой")
    void registerDailyWorks_EmptyList() {
        List<DailyWorksResponseDto> result = productionService.registerDailyWorks(Collections.emptyList());

        assertTrue(result.isEmpty());
        verifyNoInteractions(labOrdersRepository);
        verifyNoInteractions(dailyWorksRepository);
        verifyNoInteractions(orderControlService);
    }

    @Test
    @DisplayName("Передача null не вызывает работу с базой")
    void registerDailyWorks_NullList() {
        List<DailyWorksResponseDto> result = productionService.registerDailyWorks(null);

        assertTrue(result.isEmpty());
        verifyNoInteractions(labOrdersRepository);
        verifyNoInteractions(dailyWorksRepository);
        verifyNoInteractions(orderControlService);
    }

    // ==================== getWorksByDate ====================

    @Test
    @DisplayName("Получение работ по дате: есть результаты")
    void getWorksByDate_ReturnsList() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        DailyWorksEntity entity = new DailyWorksEntity();
        when(dailyWorksRepository.findByWorkDate(date)).thenReturn(List.of(entity));
        when(mapper.toResponseDtoList(anyList())).thenReturn(List.of(responseDto));

        List<DailyWorksResponseDto> result = productionService.getWorksByDate(date);

        assertEquals(1, result.size());
        verify(dailyWorksRepository, times(1)).findByWorkDate(date);
    }

    @Test
    @DisplayName("Получение работ по дате: пустой результат")
    void getWorksByDate_EmptyResult() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        when(dailyWorksRepository.findByWorkDate(date)).thenReturn(Collections.emptyList());
        when(mapper.toResponseDtoList(anyList())).thenReturn(Collections.emptyList());

        List<DailyWorksResponseDto> result = productionService.getWorksByDate(date);

        assertTrue(result.isEmpty());
    }

    // ==================== getAllDailyWorks ====================

    @Test
    @DisplayName("Получение всех работ: непустой список")
    void getAllDailyWorks_ReturnsList() {
        DailyWorksEntity entity = new DailyWorksEntity();
        when(dailyWorksRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponseDtoList(anyList())).thenReturn(List.of(responseDto));

        List<DailyWorksResponseDto> result = productionService.getAllDailyWorks();

        assertEquals(1, result.size());
        verify(dailyWorksRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Получение всех работ: пустой список")
    void getAllDailyWorks_EmptyList() {
        when(dailyWorksRepository.findAll()).thenReturn(Collections.emptyList());
        when(mapper.toResponseDtoList(anyList())).thenReturn(Collections.emptyList());

        List<DailyWorksResponseDto> result = productionService.getAllDailyWorks();

        assertTrue(result.isEmpty());
    }
}