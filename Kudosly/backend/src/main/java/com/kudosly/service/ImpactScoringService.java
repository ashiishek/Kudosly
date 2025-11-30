package com.kudosly.service;

import com.kudosly.model.Effort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Service for scoring the impact of efforts on a 1-10 scale
 * Considers effort type, complexity, scope, and quality signals
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImpactScoringService {

    private static final Map<String, Integer> EFFORT_TYPE_BASE_SCORES = Map.ofEntries(
        Map.entry("bug-fix", 5),
        Map.entry("feature-work", 7),
        Map.entry("code-review", 4),
        Map.entry("collaboration", 3),
        Map.entry("mentoring", 6),
        Map.entry("learning", 2)
    );

    /**
     * Score the impact of an effort (1-10)
     */
    public Integer scoreImpact(Effort effort) {
        try {
            String effortType = effort.getEffortType();
            int baseScore = EFFORT_TYPE_BASE_SCORES.getOrDefault(effortType, 5);

            // Apply modifiers based on payload analysis
            int modifiedScore = baseScore;
            modifiedScore += analyzeComplexity(effort.getPayload());
            modifiedScore += analyzeScope(effort.getPayload());
            modifiedScore += analyzeQualitySignals(effort.getPayload());

            // Ensure score is within 1-10 range
            int finalScore = Math.max(1, Math.min(10, modifiedScore));
            log.debug("Scored effort {} with impact score: {}", effort.getId(), finalScore);
            
            return finalScore;
        } catch (Exception e) {
            log.error("Error scoring effort: {}", effort.getId(), e);
            return 5; // Default middle score
        }
    }

    /**
     * Analyze complexity indicators from payload
     */
    private int analyzeComplexity(Map<String, Object> payload) {
        int complexity = 0;

        // Check for high complexity indicators
        String payloadStr = payload.toString().toLowerCase();
        
        if (payloadStr.contains("refactor") || payloadStr.contains("architecture")) {
            complexity += 2;
        }
        if (payloadStr.contains("performance") || payloadStr.contains("optimization")) {
            complexity += 2;
        }
        if (payloadStr.contains("security") || payloadStr.contains("vulnerability")) {
            complexity += 2;
        }
        if (payloadStr.contains("database") || payloadStr.contains("migration")) {
            complexity += 2;
        }

        // Check lines of code or file count for GitHub
        if (payload.containsKey("pull_request")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
            Integer additions = (Integer) pr.get("additions");
            Integer deletions = (Integer) pr.get("deletions");
            
            if (additions != null && additions > 500) {
                complexity += 1;
            }
            if (deletions != null && deletions > 200) {
                complexity += 1;
            }
        }

        return Math.min(complexity, 3); // Cap at +3
    }

    /**
     * Analyze scope/breadth of impact
     */
    private int analyzeScope(Map<String, Object> payload) {
        int scope = 0;

        String payloadStr = payload.toString().toLowerCase();

        // Check for scope indicators
        if (payloadStr.contains("api") || payloadStr.contains("endpoint")) {
            scope += 1;
        }
        if (payloadStr.contains("multiple") || payloadStr.contains("several")) {
            scope += 1;
        }
        if (payloadStr.contains("cross-") || payloadStr.contains("team")) {
            scope += 1;
        }
        if (payloadStr.contains("breaking") || payloadStr.contains("migration")) {
            scope += 2;
        }

        // Check for multiple files changed in GitHub
        if (payload.containsKey("pull_request")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
            Integer changedFiles = (Integer) pr.get("changed_files");
            
            if (changedFiles != null && changedFiles > 5) {
                scope += 1;
            }
        }

        return Math.min(scope, 2); // Cap at +2
    }

    /**
     * Analyze quality signals
     */
    private int analyzeQualitySignals(Map<String, Object> payload) {
        int quality = 0;

        String payloadStr = payload.toString().toLowerCase();

        // Positive signals
        if (payloadStr.contains("test") || payloadStr.contains("testing")) {
            quality += 1;
        }
        if (payloadStr.contains("documentation") || payloadStr.contains("doc")) {
            quality += 1;
        }
        if (payloadStr.contains("approved")) {
            quality += 1;
        }
        if (payloadStr.contains("merged")) {
            quality += 1;
        }

        // GitHub PR review metrics
        if (payload.containsKey("pull_request")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
            Integer reviewComments = (Integer) pr.get("review_comments");
            Boolean merged = (Boolean) pr.get("merged");
            
            if (reviewComments != null && reviewComments > 3) {
                quality += 1;
            }
            if (merged != null && merged) {
                quality += 1;
            }
        }

        return Math.min(quality, 2); // Cap at +2
    }

    /**
     * Get scoring breakdown for transparency
     */
    public Map<String, Object> getScoreBreakdown(Effort effort) {
        try {
            String effortType = effort.getEffortType();
            int baseScore = EFFORT_TYPE_BASE_SCORES.getOrDefault(effortType, 5);
            int complexity = analyzeComplexity(effort.getPayload());
            int scope = analyzeScope(effort.getPayload());
            int quality = analyzeQualitySignals(effort.getPayload());
            
            int totalScore = Math.max(1, Math.min(10, baseScore + complexity + scope + quality));

            Map<String, Object> breakdown = new HashMap<>();
            breakdown.put("totalScore", totalScore);
            breakdown.put("baseScore", baseScore);
            breakdown.put("complexityBonus", complexity);
            breakdown.put("scopeBonus", scope);
            breakdown.put("qualityBonus", quality);
            breakdown.put("effortType", effortType);

            return breakdown;
        } catch (Exception e) {
            log.error("Error getting score breakdown", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get impact category based on score
     */
    public String getImpactCategory(int score) {
        if (score >= 9) return "transformational";
        if (score >= 7) return "significant";
        if (score >= 5) return "moderate";
        if (score >= 3) return "small";
        return "minimal";
    }
}
