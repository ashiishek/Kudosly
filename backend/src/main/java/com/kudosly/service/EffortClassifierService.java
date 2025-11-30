package com.kudosly.service;

import com.kudosly.model.Effort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Service for classifying efforts using AI and keyword analysis
 * Categorizes efforts into: bug-fix, feature-work, code-review, collaboration, mentoring, learning
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EffortClassifierService {

    private static final Map<String, String[]> EFFORT_KEYWORDS = Map.ofEntries(
        Map.entry("bug-fix", new String[]{"bug", "fix", "issue", "error", "crash", "defect", "patch"}),
        Map.entry("feature-work", new String[]{"feature", "enhancement", "epic", "story", "implement", "build", "develop"}),
        Map.entry("code-review", new String[]{"review", "approved", "requested changes", "comment", "cr", "peer review"}),
        Map.entry("collaboration", new String[]{"discuss", "meeting", "sync", "pair", "together", "help", "support"}),
        Map.entry("mentoring", new String[]{"mentor", "guide", "teach", "onboard", "junior", "training", "guidance"}),
        Map.entry("learning", new String[]{"learn", "study", "course", "training", "skill", "development", "education"})
    );

    /**
     * Classify effort based on content and metadata
     */
    public String classifyEffort(Effort effort) {
        try {
            // Check if explicit effort type is provided
            if (effort.getEffortType() != null && !effort.getEffortType().isEmpty()) {
                return validateEffortType(effort.getEffortType());
            }

            // Extract text from payload
            String text = extractTextFromPayload(effort.getPayload());
            
            // Classify based on keywords
            String classification = classifyByKeywords(text);
            
            log.debug("Classified effort {} as: {}", effort.getId(), classification);
            return classification;
        } catch (Exception e) {
            log.error("Error classifying effort: {}", effort.getId(), e);
            return "collaboration"; // default fallback
        }
    }

    /**
     * Extract text from various payload formats
     */
    private String extractTextFromPayload(Map<String, Object> payload) {
        StringBuilder text = new StringBuilder();

        // Jira payload
        if (payload.containsKey("issue")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> issue = (Map<String, Object>) payload.get("issue");
            text.append(issue.getOrDefault("summary", "")).append(" ");
            text.append(issue.getOrDefault("description", "")).append(" ");
        }

        // GitHub payload
        if (payload.containsKey("pull_request")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
            text.append(pr.getOrDefault("title", "")).append(" ");
            text.append(pr.getOrDefault("body", "")).append(" ");
        }

        if (payload.containsKey("commit")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> commit = (Map<String, Object>) payload.get("commit");
            text.append(commit.getOrDefault("message", "")).append(" ");
        }

        // Slack payload
        if (payload.containsKey("event")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>) payload.get("event");
            text.append(event.getOrDefault("text", "")).append(" ");
        }

        // Generic text field
        text.append(payload.getOrDefault("text", "")).append(" ");
        text.append(payload.getOrDefault("title", "")).append(" ");
        text.append(payload.getOrDefault("description", "")).append(" ");

        return text.toString().toLowerCase();
    }

    /**
     * Classify by keyword matching
     */
    private String classifyByKeywords(String text) {
        Map<String, Integer> scores = new HashMap<>();
        
        EFFORT_KEYWORDS.forEach((category, keywords) -> {
            int score = 0;
            for (String keyword : keywords) {
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
                if (pattern.matcher(text).find()) {
                    score += 10;
                }
            }
            scores.put(category, score);
        });

        // Return category with highest score
        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("collaboration");
    }

    /**
     * Validate effort type
     */
    private String validateEffortType(String effortType) {
        if (EFFORT_KEYWORDS.containsKey(effortType)) {
            return effortType;
        }
        return "collaboration";
    }

    /**
     * Get confidence score for classification (0-100)
     */
    public int getConfidenceScore(Effort effort, String classification) {
        try {
            String text = extractTextFromPayload(effort.getPayload());
            String[] keywords = EFFORT_KEYWORDS.get(classification);
            
            if (keywords == null) {
                return 30; // Low confidence for unknown types
            }

            int matches = 0;
            for (String keyword : keywords) {
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
                if (pattern.matcher(text).find()) {
                    matches++;
                }
            }

            // Calculate confidence (0-100)
            return Math.min(100, (matches * 100) / keywords.length);
        } catch (Exception e) {
            log.error("Error calculating confidence score", e);
            return 50;
        }
    }
}
