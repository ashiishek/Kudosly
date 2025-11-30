package com.kudosly.service;

import com.kudosly.model.Effort;
import com.kudosly.model.Recognition;
import com.kudosly.model.WeeklyDigest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating weekly digest narratives
 * Creates engaging summaries of team efforts, recognitions, and achievements
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DigestNarratorService {

    private static final String[] DIGEST_OPENERS = new String[]{
        "Here's a snapshot of what made this week amazing:",
        "Check out the incredible work from your team this week:",
        "This week, your team accomplished some great things:",
        "Let's celebrate the wins from this week:",
        "Here's what your team has been up to:"
    };

    private static final Map<String, String[]> CATEGORY_INTROS = Map.ofEntries(
        Map.entry("bug-fix", new String[]{
            "Bug fixes that kept our product stable:",
            "Quality improvements and bug fixes:",
            "Issues resolved this week:"
        }),
        Map.entry("feature-work", new String[]{
            "New features shipped:",
            "Feature development highlights:",
            "Exciting new capabilities released:"
        }),
        Map.entry("code-review", new String[]{
            "Code review contributions:",
            "Quality assurance through peer review:",
            "Feedback that improved our codebase:"
        }),
        Map.entry("collaboration", new String[]{
            "Great teamwork moments:",
            "Collaboration highlights:",
            "Team support and partnership:"
        }),
        Map.entry("learning", new String[]{
            "Growth and learning achievements:",
            "Skill development this week:",
            "Learning milestones:"
        }),
        Map.entry("mentoring", new String[]{
            "Knowledge sharing and mentoring:",
            "Team development contributions:",
            "Guidance and support provided:"
        })
    );

    private static final String[] DIGEST_CLOSERS = new String[]{
        "Great work this week! Keep the momentum going.",
        "Your team is doing amazing work. Keep it up!",
        "Another fantastic week of teamwork and innovation.",
        "Excellent progress toward our goals this week.",
        "Keep celebrating these wins - you've earned it!"
    };

    /**
     * Generate weekly digest narrative
     */
    public WeeklyDigest generateWeeklyDigest(List<Recognition> recognitions, List<Effort> efforts) {
        try {
            WeeklyDigest digest = new WeeklyDigest();
            digest.setWeekStartDate(getWeekStartDate());
            digest.setWeekEndDate(new Date());
            digest.setTotalEfforts(efforts.size());
            digest.setTotalRecognitions(recognitions.size());

            // Generate narrative summary
            String narrative = generateNarrative(recognitions, efforts);
            digest.setNarrative(narrative);

            // Calculate metrics
            digest.setHighlights(extractHighlights(recognitions));
            digest.setMetrics(calculateMetrics(efforts, recognitions));
            digest.setTopContributors(extractTopContributors(efforts));

            log.info("Generated weekly digest with {} efforts and {} recognitions",
                efforts.size(), recognitions.size());
            
            return digest;
        } catch (Exception e) {
            log.error("Error generating weekly digest", e);
            return null;
        }
    }

    /**
     * Generate narrative summary of the week
     */
    private String generateNarrative(List<Recognition> recognitions, List<Effort> efforts) {
        StringBuilder narrative = new StringBuilder();

        // Opening
        String opener = selectRandom(DIGEST_OPENERS);
        narrative.append(opener).append("\n\n");

        // Group efforts by type
        Map<String, List<Effort>> effortsByType = efforts.stream()
            .collect(Collectors.groupingBy(Effort::getEffortType));

        // Add effort summaries by type
        for (Map.Entry<String, List<Effort>> entry : effortsByType.entrySet()) {
            String effortType = entry.getKey();
            List<Effort> effortList = entry.getValue();

            String intro = selectRandom(CATEGORY_INTROS.getOrDefault(effortType,
                new String[]{"Notable contributions:"}));
            narrative.append("**").append(intro).append("**\n");

            // Add top 3 efforts of this type
            effortList.stream().limit(3).forEach(effort -> {
                String description = extractEffortDescription(effort);
                narrative.append("- ").append(description).append("\n");
            });

            if (effortList.size() > 3) {
                narrative.append("- Plus ").append(effortList.size() - 3).append(" more\n");
            }
            narrative.append("\n");
        }

        // Recognition summary
        if (!recognitions.isEmpty()) {
            narrative.append("**Recognition highlights:**\n");
            recognitions.stream().limit(5).forEach(rec -> {
                narrative.append("- ").append(rec.getMessage()).append("\n");
            });
            narrative.append("\n");
        }

        // Closing
        String closer = selectRandom(DIGEST_CLOSERS);
        narrative.append(closer);

        return narrative.toString();
    }

    /**
     * Extract key highlights from recognitions
     */
    private List<String> extractHighlights(List<Recognition> recognitions) {
        return recognitions.stream()
            .filter(r -> r.getImpactScore() != null && r.getImpactScore() >= 8)
            .map(Recognition::getMessage)
            .limit(5)
            .collect(Collectors.toList());
    }

    /**
     * Calculate metrics for the week
     */
    private Map<String, Object> calculateMetrics(List<Effort> efforts, List<Recognition> recognitions) {
        Map<String, Object> metrics = new HashMap<>();

        // Effort type breakdown
        Map<String, Long> effortCounts = efforts.stream()
            .collect(Collectors.groupingBy(Effort::getEffortType, Collectors.counting()));
        metrics.put("effortTypeBreakdown", effortCounts);

        // Average impact score
        double avgImpact = efforts.stream()
            .map(Effort::getImpactScore)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        metrics.put("averageImpactScore", Math.round(avgImpact * 10.0) / 10.0);

        // Recognition rate
        double recognitionRate = recognitions.size() * 100.0 / Math.max(1, efforts.size());
        metrics.put("recognitionRate", Math.round(recognitionRate * 10.0) / 10.0);

        // Total team members involved
        Set<String> teamMembers = efforts.stream()
            .map(Effort::getEmployeeId)
            .collect(Collectors.toSet());
        metrics.put("activeContributors", teamMembers.size());

        return metrics;
    }

    /**
     * Extract top contributors
     */
    private List<String> extractTopContributors(List<Effort> efforts) {
        return efforts.stream()
            .collect(Collectors.groupingBy(Effort::getEmployeeId, Collectors.counting()))
            .entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * Extract effort description
     */
    private String extractEffortDescription(Effort effort) {
        try {
            Map<String, Object> payload = effort.getPayload();
            
            if (payload.containsKey("title")) {
                return payload.get("title").toString();
            }
            if (payload.containsKey("summary")) {
                return payload.get("summary").toString();
            }
            
            return "Effort: " + effort.getEffortType();
        } catch (Exception e) {
            return "Effort: " + effort.getEffortType();
        }
    }

    /**
     * Get week start date (Monday of current week)
     */
    private Date getWeekStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * Select random item from array
     */
    private String selectRandom(String[] items) {
        return items[new Random().nextInt(items.length)];
    }

    /**
     * Generate personalized digest intro
     */
    public String generatePersonalizedIntro(String teamName, int effortCount, int recognitionCount) {
        StringBuilder intro = new StringBuilder();
        intro.append("Hi ").append(teamName).append(" team! 🎉\n\n");
        intro.append("This week was awesome! Here's what happened:\n");
        intro.append("- ").append(effortCount).append(" efforts logged\n");
        intro.append("- ").append(recognitionCount).append(" recognitions given\n");
        intro.append("\nLet's dive in!\n\n");
        
        return intro.toString();
    }

    /**
     * Generate digest section for specific effort type
     */
    public String generateCategorySection(String effortType, List<Effort> efforts) {
        StringBuilder section = new StringBuilder();
        
        String intro = selectRandom(CATEGORY_INTROS.getOrDefault(effortType,
            new String[]{"Notable contributions:"}));
        section.append("**").append(intro).append("**\n");

        efforts.forEach(effort -> {
            String description = extractEffortDescription(effort);
            section.append("- ").append(description);
            if (effort.getImpactScore() != null) {
                section.append(" (Impact: ").append(effort.getImpactScore()).append("/10)");
            }
            section.append("\n");
        });

        return section.toString();
    }

    /**
     * Generate metric summary
     */
    public String generateMetricsSummary(Map<String, Object> metrics) {
        StringBuilder summary = new StringBuilder();
        summary.append("\n**Weekly Metrics:**\n");
        
        if (metrics.containsKey("averageImpactScore")) {
            summary.append("- Average Impact Score: ")
                .append(metrics.get("averageImpactScore")).append("/10\n");
        }
        if (metrics.containsKey("recognitionRate")) {
            summary.append("- Recognition Rate: ")
                .append(metrics.get("recognitionRate")).append("%\n");
        }
        if (metrics.containsKey("activeContributors")) {
            summary.append("- Active Contributors: ")
                .append(metrics.get("activeContributors")).append("\n");
        }

        return summary.toString();
    }
}
