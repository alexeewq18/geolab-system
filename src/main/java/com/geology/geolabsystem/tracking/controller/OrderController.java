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

    @PostMapping
    public ResponseEntity<LabOrderResponseDto> createOrder(@RequestBody LabOrderRequestDto dto) {
        LabOrderResponseDto response = orderControlService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LabOrderResponseDto>> getAllOrders() {
        List<LabOrderResponseDto> orders = orderControlService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabOrderResponseDto> getOrderById(@PathVariable Long id) {
        LabOrderResponseDto order = orderControlService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/with-balance")
    public ResponseEntity<List<LabOrderResponseDto>> getOrdersWithPositiveBalance() {
        List<LabOrderResponseDto> orders = orderControlService.getAllOrders()
                .stream()
                .filter(order -> order.getAmount() > 0)  // только с остатком
                .toList();
        return ResponseEntity.ok(orders);
    }
}