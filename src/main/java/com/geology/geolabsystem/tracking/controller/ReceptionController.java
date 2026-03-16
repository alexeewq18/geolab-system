package com.geology.geolabsystem.tracking.controller;

import com.geology.geolabsystem.tracking.dto.request.ShipmentRequestDto;
import com.geology.geolabsystem.tracking.dto.response.ShipmentResponseDto;
import com.geology.geolabsystem.tracking.service.ReceptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ReceptionController {

    private final ReceptionService receptionService;

    @PostMapping
    public ResponseEntity<List<ShipmentResponseDto>> registerShipments(
            @Valid @RequestBody List<ShipmentRequestDto> dtos) {
        List<ShipmentResponseDto> response = receptionService.registerShipments(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ShipmentResponseDto>> getAllShipments() {
        List<ShipmentResponseDto> shipments = receptionService.getAllShipments();
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDto> getShipmentById(
            @Valid @PathVariable Long id) {
        ShipmentResponseDto shipment = receptionService.getShipmentsById(id);
        return ResponseEntity.ok(shipment);
    }
}