package com.geology.geolabsystem.tracking.controller;

import com.geology.geolabsystem.tracking.dto.request.DispatchRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DispatchResponseDto;
import com.geology.geolabsystem.tracking.service.DeliveryService;
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
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    @PreAuthorize("hasRole('TEAM_LEAD')")
    public ResponseEntity<List<DispatchResponseDto>> registerDispatches(@Valid @RequestBody List<DispatchRequestDto> dtos) {
        List<DispatchResponseDto> responses = deliveryService.registerDispatches(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<Page<DispatchResponseDto>> getDispatches(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<DispatchResponseDto> dispatch = deliveryService.getAllDispatch(pageable);
        return ResponseEntity.ok(dispatch);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<DispatchResponseDto> getDispatchById(@Valid @PathVariable Long id) {
        DispatchResponseDto order = deliveryService.getDispatchById(id);
        return ResponseEntity.ok(order);
    }

}