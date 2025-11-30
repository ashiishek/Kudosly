package com.kudosly.service;

import com.kudosly.model.Badge;
import com.kudosly.model.Effort;
import com.kudosly.model.Recognition;
import com.kudosly.model.WeeklyDigest;
import com.kudosly.repository.EffortRepository;
import com.kudosly.repository.RecognitionRepository;
import com.kudosly.repository.WeeklyDigestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to generate weekly digest for employees
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyDigestService {

    private final EffortRepository effortRepository;
    private final RecognitionRepository recognitionRepository;
    private final WeeklyDigestRepository weeklyDigestRepository;

    /**
     * Get all weekly digests with pagination
     */
    public List<WeeklyDigest> getAllDigests(org.springframework.data.domain.Pageable pageable) {
        log.info("Fetching all weekly digests");
        return weeklyDigestRepository.findAll(pageable).getContent();
    }

    /**
     * Get the latest digest for an employee
     */
    public Optional<WeeklyDigest> getLatestDigest(String employeeId) {
        log.info("Fetching latest digest for employee: {}", employeeId);
        return weeklyDigestRepository.findTopByEmployeeIdOrderByWeekEndDesc(employeeId);
    }

    /**
     * Generate weekly digest for an employee
     */
    public WeeklyDigest generateDigest(String employeeId, Date weekStart, Date weekEnd) {
        log.info("Generating weekly digest for employee: {} for week: {} - {}", 
                 employeeId, weekStart, weekEnd);
        
        // Fetch efforts for the week
        List<Effort> weekEfforts = effortRepository
            .findByEmployeeIdAndTimestampBetween(employeeId, weekStart, weekEnd);
        
        // Fetch recognitions for the week
        List<Recognition> weekRecognitions = recognitionRepository
            .findByEmployeeIdOrderByTimestampDesc(employeeId)
            .stream()
            .filter(r -> r.getTimestamp().after(weekStart) && r.getTimestamp().before(weekEnd))
            .collect(Collectors.toList());
        
        // Create digest
        WeeklyDigest digest = new WeeklyDigest();
        digest.setEmployeeId(employeeId);
        digest.setWeekStart(weekStart);
        digest.setWeekEnd(weekEnd);
        
        // Generate summary
        String summary = generateSummary(weekEfforts, weekRecognitions);
        digest.setSummary(summary);
        
        // Top recognitions
        List<String> topRecognitions = weekRecognitions.stream()
            .sorted(Comparator.comparing(Recognition::getImpactScore).reversed())
            .limit(5)
            .map(Recognition::getId)
            .collect(Collectors.toList());
        digest.setTopRecognitions(topRecognitions);
        
        // Calculate collaboration score
        double collabScore = calculateCollaborationScore(weekEfforts);
        digest.setCollaborationScore(collabScore);
        
        // Learning wins
        List<String> learningWins = extractLearningWins(weekEfforts);
        digest.setLearningWins(learningWins);
        
        return weeklyDigestRepository.save(digest);
    }

    /**
     * Generate AI summary of the week
     */
    private String generateSummary(List<Effort> efforts, List<Recognition> recognitions) {
        if (efforts.isEmpty()) {
            return "This week was quiet. Looking forward to your contributions next week!";
        }
        
        int totalEfforts = efforts.size();
        long highImpact = efforts.stream()
            .filter(e -> e.getImpactScore() != null && e.getImpactScore() >= 8)
            .count();
        
        Map<String, Long> effortsByType = efforts.stream()
            .collect(Collectors.groupingBy(Effort::getEffortType, Collectors.counting()));
        
        String dominantType = effortsByType.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("contributions");
        
        return String.format(
            "This week you made %d contributions with %d high-impact efforts! " +
            "Your focus on %s demonstrates strong technical skills and dedication. " +
            "You earned %d recognitions for your outstanding work. Keep up the excellent momentum!",
            totalEfforts, highImpact, dominantType, recognitions.size()
        );
    }

    /**
     * Calculate collaboration score
     */
    private double calculateCollaborationScore(List<Effort> efforts) {
        long collabEfforts = efforts.stream()
            .filter(e -> "collaboration".equals(e.getEffortType()) || 
                        "code-review".equals(e.getEffortType()) ||
                        "mentoring".equals(e.getEffortType()))
            .count();
        
        if (efforts.isEmpty()) return 0.0;
        
        double ratio = (double) collabEfforts / efforts.size();
        return Math.min(10.0, ratio * 20.0); // Scale to 0-10
    }

    /**
     * Extract learning achievements
     */
    private List<String> extractLearningWins(List<Effort> efforts) {
        return efforts.stream()
            .filter(e -> "learning".equals(e.getEffortType()))
            .map(e -> (String) e.getPayload().getOrDefault("description", "Completed learning activity"))
            .collect(Collectors.toList());
    }

    /**
     * Scheduled job to generate digests every Friday
     */
    @Scheduled(cron = "0 0 18 * * FRI") // Every Friday at 6 PM
    public void generateWeeklyDigests() {
        log.info("Starting weekly digest generation job");
        
        Calendar cal = Calendar.getInstance();
        Date weekEnd = cal.getTime();
        cal.add(Calendar.DAY_OF_WEEK, -7);
        Date weekStart = cal.getTime();
        
        // In production, fetch all employee IDs and generate digests
        // For demo, this would be triggered manually or for specific employees
        
        log.info("Weekly digest generation completed");
    }
}
