package com.geology.geolabsystem.tracking.controller;


import com.geology.geolabsystem.tracking.dto.request.LabOrderRequestDto;
import com.geology.geolabsystem.tracking.dto.response.LabOrderResponseDto;
import com.geology.geolabsystem.tracking.service.OrderControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderControlService orderControlService;

    //Добавление заказа
    @PostMapping
    public ResponseEntity<LabOrderResponseDto> createOrder(@RequestBody LabOrderRequestDto dto) {
        LabOrderResponseDto response = orderControlService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //    Получение списка заказов
    @GetMapping
    public ResponseEntity<List<LabOrderResponseDto>> getAllOrders() {
        List<LabOrderResponseDto> orders = orderControlService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    //    Получение заказа по ID
    @GetMapping("/{id}")
    public ResponseEntity<LabOrderResponseDto> getOrderById(@PathVariable Long id) {
        LabOrderResponseDto order = orderControlService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
}