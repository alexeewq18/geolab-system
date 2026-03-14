package com.geology.geolabsystem.tracking.controller;

import com.geology.geolabsystem.tracking.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.service.ProductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

    @RestController
    @RequestMapping("/api/daily-works")
    @RequiredArgsConstructor
    public class DailyWorkController {

        private final ProductionService productionService;

        @PostMapping
        public ResponseEntity<DailyWorksResponseDto> registerDailyWork(@RequestBody DailyWorksRequestDto dto) {
            DailyWorksResponseDto response = productionService.registerDailyWorks(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PostMapping("/bulk")
        public ResponseEntity<List<DailyWorksResponseDto>> registerBulkDailyWorks(
                @RequestBody List<DailyWorksRequestDto> listDto) {
            List<DailyWorksResponseDto> responses = productionService.registerAllDailyWorks(listDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        }

        @GetMapping("/by-date")
        public ResponseEntity<List<DailyWorksResponseDto>> getWorksByDate(
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
            List<DailyWorksResponseDto> works = productionService.getWorksByDate(date);
            return ResponseEntity.ok(works);
        }

        @GetMapping
        public ResponseEntity<List<DailyWorksResponseDto>> getAllDailyWorks(
                @RequestParam(required = false) String orderName,
                @RequestParam(required = false) String geologistName) {
            List<DailyWorksResponseDto> works = productionService.getAllDailyWorks(orderName, geologistName);
            return ResponseEntity.ok(works);
        }
    }