package com.kudosly.service;

import com.kudosly.model.Effort;
import com.kudosly.model.Recognition;
import com.kudosly.repository.RecognitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service to generate personalized recognition messages powered by templates and AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecognitionGeneratorService {

    private final RecognitionRepository recognitionRepository;

    private static final Map<String, String[]> RECOGNITION_TEMPLATES = Map.ofEntries(
        Map.entry("bug-fix", new String[]{
            "Outstanding debugging! Your fix on {effort} addresses the root cause effectively. Your problem-solving skills are invaluable.",
            "Excellent troubleshooting on {effort}! You identified and resolved this critical issue with precision.",
            "Amazing bug fix on {effort}! Your technical expertise prevented customer impact and improved system stability.",
            "Brilliant diagnostics on {effort}! Your quick resolution kept our product running smoothly.",
            "Great debugging work on {effort}! Your attention to detail caught subtle issues others might have missed."
        }),
        Map.entry("feature-work", new String[]{
            "Fantastic feature on {effort}! Your implementation is solid, well-architected, and ready for production.",
            "Outstanding work on {effort}! Your code quality and technical vision move the product forward significantly.",
            "Excellent delivery on {effort}! Your feature enhances user experience and adds real business value.",
            "Impressive development on {effort}! Your execution shows mastery of the technology and thoughtful design.",
            "Great work building {effort}! Your contributions demonstrate strong technical skills and team impact."
        }),
        Map.entry("code-review", new String[]{
            "Fantastic code review on {effort}! Your detailed feedback strengthens our codebase and mentors team members.",
            "Excellent review work on {effort}! Your insights caught potential issues and improved code quality.",
            "Outstanding peer review on {effort}! Your constructive comments help maintain our technical standards.",
            "Great code review on {effort}! Your expertise and guidance improve the entire team's work.",
            "Impressive review on {effort}! Your thorough analysis and feedback raise the bar for code quality."
        }),
        Map.entry("collaboration", new String[]{
            "Amazing teamwork on {effort}! Your collaborative spirit and support strengthen the entire team.",
            "Excellent collaboration on {effort}! Your willingness to help others succeed is truly appreciated.",
            "Outstanding partnership on {effort}! Great job working across boundaries to achieve shared goals.",
            "Great teamwork on {effort}! Your ability to work effectively with others drives better outcomes.",
            "Impressive collaboration on {effort}! Your support and communication make everyone more productive."
        }),
        Map.entry("learning", new String[]{
            "Congratulations on learning {effort}! Your growth mindset and commitment to improvement are inspiring.",
            "Excellent skill development on {effort}! Your dedication to learning strengthens our team capabilities.",
            "Outstanding effort on {effort}! Continuous learning and skill expansion are hallmarks of great engineers.",
            "Great progress on {effort}! Your initiative to expand your knowledge demonstrates leadership.",
            "Impressive learning on {effort}! Your commitment to development positions you for greater impact."
        }),
        Map.entry("mentoring", new String[]{
            "Outstanding mentoring on {effort}! Thank you for investing time in helping others succeed and grow.",
            "Excellent guidance on {effort}! Your mentorship shapes talent and strengthens the entire organization.",
            "Fantastic mentoring on {effort}! Your knowledge transfer and patience are invaluable to our team.",
            "Great job mentoring on {effort}! Your willingness to help others develop is truly appreciated.",
            "Impressive mentoring on {effort}! Your investment in others' growth builds a stronger team."
        })
    );

    private static final Map<String, String[]> IMPACT_PHRASES = Map.ofEntries(
        Map.entry("transformational", new String[]{
            "This makes a transformational impact on our product and strategy.",
            "This is truly game-changing work that will reshape how we operate.",
            "This fundamentally improves our competitive position and capability.",
            "This has strategic significance and will drive long-term success.",
            "This breakthrough work sets a new standard for our team."
        }),
        Map.entry("significant", new String[]{
            "This has significant impact on our product quality and reliability.",
            "This meaningfully improves our development velocity and efficiency.",
            "This strengthens our technical foundation for future growth.",
            "This creates lasting value that benefits the entire organization.",
            "This substantially advances our engineering capabilities."
        }),
        Map.entry("moderate", new String[]{
            "This contributes positively to our codebase and product evolution.",
            "This helps move our projects forward with quality and care.",
            "This adds value to our product and improves user experience.",
            "This improves our team's capabilities and knowledge base.",
            "This supports our goals and strengthens our technical assets."
        }),
        Map.entry("small", new String[]{
            "Thank you for your consistent contributions to our team's success.",
            "We appreciate your effort and commitment to quality work.",
            "Great work on this task - it helps us keep moving forward.",
            "Thanks for being a reliable team member and contributor.",
            "Your steady effort contributes to our collective progress."
        }),
        Map.entry("minimal", new String[]{
            "Thanks for your participation and effort on this task.",
            "We appreciate you being part of the team and contributing.",
            "Good work - every contribution counts toward our success.",
            "Thank you for your engagement and team spirit.",
            "Keep up the good work as we grow together."
        })
    );

    private static final Map<String, String> BADGE_MAPPING = Map.ofEntries(
        Map.entry("feature-work", "🚀"),
        Map.entry("bug-fix", "🔧"),
        Map.entry("code-review", "👀"),
        Map.entry("collaboration", "🤝"),
        Map.entry("learning", "📚"),
        Map.entry("mentoring", "👨‍🏫")
    );

    /**
     * Generate recognition from analyzed effort
     */
    public Recognition generateRecognition(Effort effort) {
        log.info("Generating recognition for effort: {}", effort.getId());
        
        try {
            String effortType = effort.getEffortType();
            Integer impactScore = effort.getImpactScore();
            
            Recognition recognition = new Recognition();
            recognition.setEmployeeId(effort.getEmployeeId());
            recognition.setEffortId(effort.getId());
            recognition.setImpactScore(impactScore);
            recognition.setCategory(effortType);
            recognition.setTimestamp(new Date());
            
            // Generate message based on effort type and impact
            String message = generateMessage(effort);
            recognition.setMessage(message);
            
            // Assign badge
            String badge = assignBadge(effortType);
            recognition.setBadge(badge);
            
            Recognition saved = recognitionRepository.save(recognition);
            log.info("Successfully generated recognition: {}", saved.getId());
            
            return saved;
        } catch (Exception e) {
            log.error("Error generating recognition for effort: {}", effort.getId(), e);
            throw new RuntimeException("Failed to generate recognition", e);
        }
    }

    /**
     * Generate personalized appreciation message
     */
    private String generateMessage(Effort effort) {
        try {
            String effortType = effort.getEffortType();
            Integer impactScore = effort.getImpactScore();
            
            // Get impact category
            String impactCategory = getImpactCategory(impactScore);
            
            // Select base template
            String[] templates = RECOGNITION_TEMPLATES.getOrDefault(effortType, RECOGNITION_TEMPLATES.get("collaboration"));
            String baseMessage = selectRandomTemplate(templates);
            
            // Extract effort description
            String effortDescription = extractEffortDescription(effort.getPayload());
            baseMessage = baseMessage.replace("{effort}", effortDescription);
            
            // Add impact phrase for significant work
            if ("transformational".equals(impactCategory) || "significant".equals(impactCategory)) {
                String[] impactPhrases = IMPACT_PHRASES.get(impactCategory);
                String impactPhrase = selectRandomTemplate(impactPhrases);
                baseMessage += " " + impactPhrase;
            }
            
            return baseMessage;
        } catch (Exception e) {
            log.error("Error generating message for effort: {}", effort.getId(), e);
            return "Thank you for your outstanding contribution to the team!";
        }
    }

    /**
     * Extract effort description from payload
     */
    private String extractEffortDescription(Map<String, Object> payload) {
        try {
            // Try common payload fields in order of preference
            if (payload.containsKey("title")) {
                Object title = payload.get("title");
                if (title != null) return title.toString();
            }
            if (payload.containsKey("summary")) {
                Object summary = payload.get("summary");
                if (summary != null) return summary.toString();
            }
            
            // Jira issue
            if (payload.containsKey("issue")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> issue = (Map<String, Object>) payload.get("issue");
                Object summary = issue.get("summary");
                if (summary != null) return summary.toString();
            }
            
            // GitHub PR
            if (payload.containsKey("pull_request")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
                Object title = pr.get("title");
                if (title != null) return title.toString();
            }
            
            // Git commit
            if (payload.containsKey("commit")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> commit = (Map<String, Object>) payload.get("commit");
                Object message = commit.get("message");
                if (message != null) return message.toString().split("\n")[0];
            }

            return "your contribution";
        } catch (Exception e) {
            log.debug("Error extracting effort description", e);
            return "your contribution";
        }
    }

    /**
     * Get impact category from score
     */
    private String getImpactCategory(Integer score) {
        if (score == null) score = 5;
        
        if (score >= 9) return "transformational";
        if (score >= 7) return "significant";
        if (score >= 5) return "moderate";
        if (score >= 3) return "small";
        return "minimal";
    }

    /**
     * Assign appropriate badge based on effort type
     */
    private String assignBadge(String effortType) {
        return BADGE_MAPPING.getOrDefault(effortType, "⭐");
    }

    /**
     * Select random template from array
     */
    private String selectRandomTemplate(String[] templates) {
        if (templates == null || templates.length == 0) {
            return "Great work on your contribution!";
        }
        return templates[new Random().nextInt(templates.length)];
    }

    /**
     * Generate personalized recognition message with recipient name and badges
     */
    public String generatePersonalizedMessage(Effort effort, String recipientName, String awardBadges) {
        String baseMessage = generateMessage(effort);

        StringBuilder message = new StringBuilder();
        message.append("Hey ").append(recipientName).append("! ").append(baseMessage);

        if (awardBadges != null && !awardBadges.isEmpty()) {
            message.append("\n\nYou've earned: ").append(awardBadges);
        }

        return message.toString();
    }

    /**
     * Generate bulk recognition for multiple efforts
     */
    public List<Recognition> generateBulkRecognitions(List<Effort> efforts) {
        List<Recognition> recognitions = new ArrayList<>();
        
        for (Effort effort : efforts) {
            try {
                Recognition recognition = generateRecognition(effort);
                recognitions.add(recognition);
            } catch (Exception e) {
                log.error("Error generating recognition for effort: {}", effort.getId(), e);
            }
        }

        return recognitions;
    }
}
