package com.geology.geolabsystem.tracking.controller;

import com.geology.geolabsystem.tracking.dto.request.DailyWorksRequestDto;
import com.geology.geolabsystem.tracking.dto.response.DailyWorksResponseDto;
import com.geology.geolabsystem.tracking.service.ProductionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-works")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionService productionService;

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<DailyWorksResponseDto>> registerBulkDailyWorks(
            @Valid @RequestBody List<DailyWorksRequestDto> listDto) {
        List<DailyWorksResponseDto> responses = productionService.registerDailyWorks(listDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/by-date")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<DailyWorksResponseDto>> getWorksByDate(
            @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<DailyWorksResponseDto> works = productionService.getWorksByDate(date);
        return ResponseEntity.ok(works);
    }

    @GetMapping
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<Page<DailyWorksResponseDto>> getAllDailyWorks(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<DailyWorksResponseDto> works = productionService.getAllDailyWorks(pageable);
        return ResponseEntity.ok(works);
    }
}