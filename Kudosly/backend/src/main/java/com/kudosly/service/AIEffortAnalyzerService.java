package com.kudosly.service;

import com.kudosly.model.Effort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * AI-powered effort analyzer using OpenAI GPT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIEffortAnalyzerService {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    /**
     * Analyze effort using AI to classify type and score impact
     */
    public Effort analyzeEffort(Effort effort) {
        log.info("Analyzing effort: {} from source: {}", effort.getId(), effort.getSource());
        
        try {
            // Build prompt for AI
            String prompt = buildClassificationPrompt(effort);
            
            // Call OpenAI API (simplified - in production use OpenAI SDK)
            // For demo purposes, using rule-based classification
            classifyEffort(effort);
            
            return effort;
        } catch (Exception e) {
            log.error("Error analyzing effort with AI", e);
            // Fallback to rule-based classification
            classifyEffort(effort);
            return effort;
        }
    }

    /**
     * Build AI prompt for effort classification
     */
    private String buildClassificationPrompt(Effort effort) {
        return String.format("""
            Classify the following event into an effort type: bug-fix, feature-work, collaboration, mentoring, code-review, learning.
            Return JSON with fields: effort_type, impact_score (1-10), explanation.
            
            Event:
            Source: %s
            Payload: %s
            """, effort.getSource(), effort.getPayload().toString());
    }

    /**
     * Rule-based classification (fallback or demo mode)
     */
    private void classifyEffort(Effort effort) {
        String source = effort.getSource();
        
        switch (source) {
            case "jira" -> {
                effort.setEffortType("feature-work");
                effort.setImpactScore(7);
            }
            case "git" -> {
                String action = (String) effort.getPayload().getOrDefault("action", "commit");
                if ("pr_merged".equals(action)) {
                    effort.setEffortType("feature-work");
                    effort.setImpactScore(8);
                } else if ("code_review".equals(action)) {
                    effort.setEffortType("code-review");
                    effort.setImpactScore(7);
                } else {
                    effort.setEffortType("bug-fix");
                    effort.setImpactScore(6);
                }
            }
            case "slack", "teams" -> {
                effort.setEffortType("collaboration");
                effort.setImpactScore(6);
            }
            case "lms" -> {
                effort.setEffortType("learning");
                effort.setImpactScore(7);
            }
            case "calendar" -> {
                effort.setEffortType("collaboration");
                effort.setImpactScore(5);
            }
            default -> {
                effort.setEffortType("other");
                effort.setImpactScore(5);
            }
        }
    }
}
