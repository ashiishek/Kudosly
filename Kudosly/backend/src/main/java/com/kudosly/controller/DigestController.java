package com.kudosly.controller;

import com.kudosly.model.WeeklyDigest;
import com.kudosly.service.WeeklyDigestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * REST controller for weekly digests
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/digest")
@RequiredArgsConstructor
public class DigestController {

    private final WeeklyDigestService weeklyDigestService;

    /**
     * Get all weekly digests with pagination
     */
    @GetMapping
    public ResponseEntity<java.util.List<WeeklyDigest>> getAllDigests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching all weekly digests - page: {}, size: {}", page, size);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        // Assuming WeeklyDigestService has a repository method
        java.util.List<WeeklyDigest> digests = weeklyDigestService.getAllDigests(pageable);
        
        return ResponseEntity.ok(digests);
    }

    /**
     * Get the latest weekly digest for an employee
     */
    @GetMapping("/latest/{userId}")
    public ResponseEntity<WeeklyDigest> getLatestDigest(@PathVariable String userId) {
        log.info("Fetching latest weekly digest for employee: {}", userId);
        
        Optional<WeeklyDigest> digest = weeklyDigestService.getLatestDigest(userId);
        
        if (digest.isPresent()) {
            return ResponseEntity.ok(digest.get());
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Get weekly digest for an employee for a specific week
     */
    @GetMapping("/{userId}")
    public ResponseEntity<WeeklyDigest> getWeeklyDigest(
            @PathVariable String userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date weekStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date weekEnd) {
        
        log.info("Fetching weekly digest for employee: {} from {} to {}", userId, weekStart, weekEnd);
        
        // Default to current week if dates not provided
        if (weekStart == null || weekEnd == null) {
            Calendar cal = Calendar.getInstance();
            weekEnd = cal.getTime();
            cal.add(Calendar.DAY_OF_WEEK, -7);
            weekStart = cal.getTime();
        }
        
        WeeklyDigest digest = weeklyDigestService.generateDigest(userId, weekStart, weekEnd);
        
        return ResponseEntity.ok(digest);
    }

    /**
     * Trigger digest generation manually
     */
    @PostMapping("/{userId}/generate")
    public ResponseEntity<WeeklyDigest> generateDigest(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date weekStart,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date weekEnd) {
        
        log.info("Generating weekly digest for employee: {} from {} to {}", userId, weekStart, weekEnd);
        
        WeeklyDigest digest = weeklyDigestService.generateDigest(userId, weekStart, weekEnd);
        
        return ResponseEntity.ok(digest);
    }
}
