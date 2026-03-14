package com.geology.geolabsystem.tracking.controller;


import com.geology.geolabsystem.tracking.dto.request.DispatchRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DispatchResponseDto;
import com.geology.geolabsystem.tracking.service.LogisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
public class LogisticsController {

    private final LogisticsService logisticsService;

    //    Зарегистрировать отправку
    @PostMapping
    public ResponseEntity<DispatchResponseDto> registerDispatch(@RequestBody DispatchRequestDto dto) {
        DispatchResponseDto response = logisticsService.registerDispatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Получить отправки по заказу
    @GetMapping
    public ResponseEntity<List<DispatchResponseDto>> getDispatches(
            @RequestParam(required = false) String labOrder,
            @RequestParam(required = false) String geologistName) {
        List<DispatchResponseDto> dispatch = logisticsService.getAllDispatch(labOrder, geologistName);
        return ResponseEntity.ok(dispatch);
    }

    // Получить отправку по айди
    @GetMapping("/{id}")
    public ResponseEntity<DispatchResponseDto> getDispatchById(@PathVariable Long id) {
        DispatchResponseDto order = logisticsService.getDispatchById(id);
        return ResponseEntity.ok(order);
    }

}