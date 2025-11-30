package com.kudosly.controller;

import com.kudosly.model.Badge;
import com.kudosly.model.EmployeeBadge;
import com.kudosly.service.BadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for badges
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    /**
     * Get all available badges
     */
    @GetMapping
    public ResponseEntity<List<Badge>> getAllBadges() {
        log.info("Fetching all badges");
        
        List<Badge> badges = badgeService.getAllBadges();
        
        return ResponseEntity.ok(badges);
    }

    /**
     * Get badges earned by an employee
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Badge>> getEmployeeBadges(@PathVariable String userId) {
        log.info("Fetching badges for employee: {}", userId);
        
        List<Badge> badges = badgeService.getBadgesByEmployee(userId);
        
        return ResponseEntity.ok(badges);
    }

    /**
     * Award a badge to an employee (internal endpoint)
     */
    @PostMapping("/award")
    public ResponseEntity<EmployeeBadge> awardBadge(
            @RequestParam String employeeId,
            @RequestParam String badgeId) {
        
        log.info("Awarding badge {} to employee {}", badgeId, employeeId);
        
        EmployeeBadge awardedBadge = badgeService.awardBadge(employeeId, badgeId);
        
        return ResponseEntity.ok(awardedBadge);
    }

    /**
     * Get badge progress for an employee
     */
    @GetMapping("/{badgeId}/user/{userId}")
    public ResponseEntity<Map<String, Object>> getBadgeProgress(
            @PathVariable String badgeId,
            @PathVariable String userId) {
        
        log.info("Fetching progress for badge {} for employee {}", badgeId, userId);
        
        Map<String, Object> progress = badgeService.getBadgeProgress(userId, badgeId);
        
        return ResponseEntity.ok(progress);
    }

    /**
     * Evaluate badge criteria for an employee
     */
    @PostMapping("/evaluate/{userId}")
    public ResponseEntity<String> evaluateBadgeCriteria(@PathVariable String userId) {
        
        log.info("Evaluating badge criteria for employee {}", userId);
        
        badgeService.evaluateBadgeCriteria(userId);
        
        return ResponseEntity.ok("Badge criteria evaluated for employee: " + userId);
    }
}
