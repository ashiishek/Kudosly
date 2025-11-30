package com.kudosly.controller;

import com.kudosly.model.Effort;
import com.kudosly.repository.EffortRepository;
import com.kudosly.service.EffortIntakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for effort events
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/efforts")
@RequiredArgsConstructor
public class EffortController {

    private final EffortIntakeService effortIntakeService;
    private final EffortRepository effortRepository;

    /**
     * Get all efforts with pagination
     */
    @GetMapping
    public ResponseEntity<List<Effort>> getAllEfforts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching all efforts - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        List<Effort> efforts = effortRepository.findAll(pageable).getContent();
        
        return ResponseEntity.ok(efforts);
    }

    /**
     * Webhook endpoint for effort events
     */
    @PostMapping("/webhook")
    public ResponseEntity<Effort> receiveEffortEvent(
            @RequestParam String employeeId,
            @RequestParam String source,
            @RequestBody Map<String, Object> payload) {
        
        log.info("Received effort event for employee: {} from source: {}", employeeId, source);
        
        Effort effort = effortIntakeService.processEffortEvent(employeeId, source, payload);
        
        return ResponseEntity.ok(effort);
    }

    /**
     * Get efforts for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Effort>> getUserEfforts(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<Effort> efforts = effortRepository.findByEmployeeIdOrderByTimestampDesc(userId, pageable);
        
        return ResponseEntity.ok(efforts);
    }

    /**
     * Get efforts within a date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Effort>> getEffortsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        Date startDateTime = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateTime = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        List<Effort> efforts = effortRepository.findByTimestampAfter(startDateTime);
        
        return ResponseEntity.ok(efforts);
    }

    /**
     * Get effort statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getEffortStats(
            @RequestParam(required = false) String userId) {
        
        Map<String, Object> stats = new HashMap<>();
        
        if (userId != null) {
            List<Effort> userEfforts = effortRepository.findByEmployeeId(userId);
            stats.put("totalEfforts", userEfforts.size());
            stats.put("averageImpact", userEfforts.stream()
                    .mapToInt(e -> e.getImpactScore() != null ? e.getImpactScore() : 0)
                    .average()
                    .orElse(0.0));
        } else {
            long totalEfforts = effortRepository.count();
            stats.put("totalEfforts", totalEfforts);
        }
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Create a test effort event
     */
    @PostMapping("/test")
    public ResponseEntity<Effort> createTestEffort(
            @RequestParam String employeeId,
            @RequestParam(defaultValue = "git") String source) {
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "test_action");
        payload.put("description", "Test effort for validation");
        
        Effort effort = effortIntakeService.processEffortEvent(employeeId, source, payload);
        
        return ResponseEntity.ok(effort);
    }
}
