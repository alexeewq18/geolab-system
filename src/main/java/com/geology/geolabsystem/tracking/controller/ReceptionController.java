package com.geology.geolabsystem.tracking.controller;

import com.geology.geolabsystem.tracking.dto.request.ShipmentRequestDto;
import com.geology.geolabsystem.tracking.dto.response.ShipmentResponseDto;
import com.geology.geolabsystem.tracking.service.ReceptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ReceptionController {

    private final ReceptionService receptionService;

    @PostMapping
    @PreAuthorize("hasRole('TEAM_LEAD')")
    public ResponseEntity<List<ShipmentResponseDto>> registerShipments(
            @Valid @RequestBody List<ShipmentRequestDto> dtos) {
        List<ShipmentResponseDto> response = receptionService.registerShipments(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<Page<ShipmentResponseDto>> getAllShipments(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<ShipmentResponseDto> shipments = receptionService.getAllShipments(pageable);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ShipmentResponseDto> getShipmentById(
            @Valid @PathVariable Long id) {
        ShipmentResponseDto shipment = receptionService.getShipmentsById(id);
        return ResponseEntity.ok(shipment);
    }
}