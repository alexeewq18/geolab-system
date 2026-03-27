package com.geology.geolabsystem.tracking.controller;

import com.geology.geolabsystem.tracking.dto.request.LabOrderRequestDto;
import com.geology.geolabsystem.tracking.dto.response.LabOrderResponseDto;
import com.geology.geolabsystem.tracking.dto.response.OrderSummaryResponseDto;
import com.geology.geolabsystem.tracking.service.OrderControlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderControlService orderControlService;

    @PostMapping
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<LabOrderResponseDto> createOrder(@Valid @RequestBody LabOrderRequestDto dto) {
        LabOrderResponseDto response = orderControlService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<Page<LabOrderResponseDto>> getAllOrders(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderControlService.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<LabOrderResponseDto> getOrderById(@Valid @PathVariable Long id) {
        LabOrderResponseDto order = orderControlService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/by-details")
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<LabOrderResponseDto> getOrderByDetails(
            @RequestParam String orderName,
            @RequestParam String description,
            @RequestParam String geologistName) {
        LabOrderResponseDto order = orderControlService.getOrderByDetails(
                orderName, description, geologistName
        );
        return ResponseEntity.ok(order);
    }

    @GetMapping("/with-balance")
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<Page<LabOrderResponseDto>> getOrdersWithPositiveBalance(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderControlService.getOrdersWithPositiveBalance(pageable));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('CHIEF')")
    public ResponseEntity<OrderSummaryResponseDto> getSummary() {
        return ResponseEntity.ok(orderControlService.getSummary());
    }
}
