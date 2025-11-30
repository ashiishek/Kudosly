package com.kudosly.service;

import com.kudosly.model.Effort;
import com.kudosly.model.Recognition;
import com.kudosly.repository.EffortRepository;
import com.kudosly.repository.RecognitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Unified effort processing service that coordinates classification, scoring, and recognition
 * Orchestrates the complete effort-to-recognition pipeline
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EffortProcessingService {

    private final EffortRepository effortRepository;
    private final RecognitionRepository recognitionRepository;
    private final EffortClassifierService classifierService;
    private final ImpactScoringService scoringService;
    private final RecognitionGeneratorService generationService;
    private final BadgeService badgeService;

    /**
     * Process new effort through complete pipeline
     * - Classify effort type
     * - Score impact
     * - Generate recognition
     * - Award badges
     */
    @Async
    public void processNewEffort(Effort effort) {
        try {
            log.info("Starting effort processing pipeline for effort: {}", effort.getId());

            // Step 1: Classify effort
            String effortType = classifierService.classifyEffort(effort);
            effort.setEffortType(effortType);
            log.debug("Classified effort {} as: {}", effort.getId(), effortType);

            // Step 2: Score impact
            Integer impactScore = scoringService.scoreImpact(effort);
            effort.setImpactScore(impactScore);
            log.debug("Scored effort {} with impact: {}", effort.getId(), impactScore);

            // Step 3: Save updated effort
            effort = effortRepository.save(effort);

            // Step 4: Generate recognition if impact is significant
            if (impactScore >= 5) {
                Recognition recognition = generationService.generateRecognition(effort);
                log.info("Generated recognition {} for effort {}", recognition.getId(), effort.getId());

                // Step 5: Award badges if impact is high
                if (impactScore >= 7) {
                    badgeService.awardBadgeForEffort(effort, effortType, impactScore);
                    log.info("Awarded badge for effort: {}", effort.getId());
                }
            }

            log.info("Completed effort processing pipeline for effort: {}", effort.getId());

        } catch (Exception e) {
            log.error("Error processing effort: {}", effort.getId(), e);
        }
    }

    /**
     * Batch process multiple efforts
     */
    public void processBatchEfforts(List<Effort> efforts) {
        efforts.forEach(this::processNewEffort);
    }

    /**
     * Get complete effort summary with classification and score
     */
    public EffortSummary getEffortSummary(String effortId) {
        try {
            Effort effort = effortRepository.findById(effortId)
                .orElseThrow(() -> new IllegalArgumentException("Effort not found: " + effortId));

            Recognition recognition = recognitionRepository.findByEffortId(effortId)
                .orElse(null);

            int confidence = classifierService.getConfidenceScore(effort, effort.getEffortType());
            var scoreBreakdown = scoringService.getScoreBreakdown(effort);

            return new EffortSummary(
                effort.getId(),
                effort.getEmployeeId(),
                effort.getEffortType(),
                effort.getImpactScore(),
                confidence,
                scoreBreakdown,
                recognition,
                effort.getTimestamp()
            );
        } catch (Exception e) {
            log.error("Error getting effort summary: {}", effortId, e);
            throw new RuntimeException("Failed to get effort summary", e);
        }
    }

    /**
     * Summary data class for effort information
     */
    public static class EffortSummary {
        public final String effortId;
        public final String employeeId;
        public final String effortType;
        public final Integer impactScore;
        public final Integer confidenceScore;
        public final java.util.Map<String, Object> scoreBreakdown;
        public final Recognition recognition;
        public final java.util.Date timestamp;

        public EffortSummary(String effortId, String employeeId, String effortType,
                           Integer impactScore, Integer confidenceScore,
                           java.util.Map<String, Object> scoreBreakdown,
                           Recognition recognition, java.util.Date timestamp) {
            this.effortId = effortId;
            this.employeeId = employeeId;
            this.effortType = effortType;
            this.impactScore = impactScore;
            this.confidenceScore = confidenceScore;
            this.scoreBreakdown = scoreBreakdown;
            this.recognition = recognition;
            this.timestamp = timestamp;
        }
    }
}
