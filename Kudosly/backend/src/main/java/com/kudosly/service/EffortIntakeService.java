package com.kudosly.service;

import com.kudosly.model.Effort;
import com.kudosly.model.Recognition;
import com.kudosly.repository.EffortRepository;
import com.kudosly.repository.RecognitionRepository;
import com.kudosly.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EffortIntakeService {

    private final EffortRepository effortRepository;
    private final EmployeeRepository employeeRepository;
    private final AIEffortAnalyzerService aiAnalyzerService;
    private final RecognitionGeneratorService recognitionService;
    private final EffortProcessingService effortProcessingService;

    private static final String JIRA_WEBHOOK_SECRET = System.getenv("JIRA_WEBHOOK_SECRET");
    private static final String GITHUB_WEBHOOK_SECRET = System.getenv("GITHUB_WEBHOOK_SECRET");
    private static final String SLACK_WEBHOOK_SECRET = System.getenv("SLACK_WEBHOOK_SECRET");

    /**
     * Process incoming effort event from various sources
     */
    public Effort processEffortEvent(String employeeId, String source, Map<String, Object> payload) {
        log.info("Processing effort event for employee: {} from source: {}", employeeId, source);
        
        // Create effort record
        Effort effort = new Effort();
        effort.setEmployeeId(employeeId);
        effort.setSource(source);
        effort.setPayload(payload);
        effort.setTimestamp(new Date());
        
        // Save raw effort
        effort = effortRepository.save(effort);
        
        // Analyze effort asynchronously
        analyzeAndRecognizeEffort(effort);
        
        return effort;
    }

    /**
     * Process webhook payload and create Effort (NEW)
     */
    public Effort processWebhook(Map<String, Object> payload, String source) {
        log.info("Processing webhook from source: {}", source);

        try {
            Effort effort = normalizePayload(payload, source);
            
            if (effort == null) {
                log.warn("Failed to normalize payload from source: {}", source);
                return null;
            }

            // Save effort to database
            Effort savedEffort = effortRepository.save(effort);
            log.info("Saved effort: {} from source: {}", savedEffort.getId(), source);

            // Async processing (classify, score, recognize, award badges)
            processEffortAsync(savedEffort);

            return savedEffort;
        } catch (Exception e) {
            log.error("Error processing webhook from source: {}", source, e);
            throw new RuntimeException("Webhook processing failed", e);
        }
    }

    /**
     * Normalize webhook payload to standard Effort format
     */
    private Effort normalizePayload(Map<String, Object> payload, String source) {
        switch (source.toLowerCase()) {
            case "jira":
                return normalizeJiraPayload(payload);
            case "github":
                return normalizeGithubPayload(payload);
            case "bitbucket":
                return normalizeBitbucketPayload(payload);
            case "slack":
                return normalizeSlackPayload(payload);
            case "test":
                return normalizeTestPayload(payload);
            default:
                log.warn("Unknown webhook source: {}", source);
                return null;
        }
    }

    /**
     * Normalize Jira webhook payload
     */
    private Effort normalizeJiraPayload(Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> issue = (Map<String, Object>) payload.get("issue");
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) issue.get("assignee");
            
            if (issue == null || user == null) return null;

            String effortType = detectJiraEffortType(payload);
            String assigneeEmail = (String) user.getOrDefault("emailAddress", "");

            Effort effort = new Effort();
            effort.setSource("jira");
            effort.setEmployeeId(findEmployeeIdByEmail(assigneeEmail));
            effort.setEffortType(effortType);
            effort.setPayload(payload);
            effort.setTimestamp(new Date());

            return effort;
        } catch (Exception e) {
            log.error("Error normalizing Jira payload", e);
            return null;
        }
    }

    /**
     * Detect Jira effort type from webhook payload
     */
    private String detectJiraEffortType(Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> issue = (Map<String, Object>) payload.get("issue");
            @SuppressWarnings("unchecked")
            Map<String, Object> issueType = (Map<String, Object>) issue.get("issuetype");
            String type = (String) issueType.get("name");
            
            if (type.contains("Bug")) return "bug-fix";
            if (type.contains("Feature")) return "feature-work";
            if (type.contains("Epic")) return "feature-work";
            if (type.contains("Task")) return "collaboration";
            
            return "collaboration";
        } catch (Exception e) {
            return "collaboration";
        }
    }

    /**
     * Normalize GitHub webhook payload
     */
    private Effort normalizeGithubPayload(Map<String, Object> payload) {
        try {
            String action = (String) payload.get("action");
            @SuppressWarnings("unchecked")
            Map<String, Object> pullRequest = (Map<String, Object>) payload.get("pull_request");
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) pullRequest.get("user");
            
            String effortType = detectGithubEffortType(action, payload);
            String username = (String) user.get("login");

            Effort effort = new Effort();
            effort.setSource("github");
            effort.setEmployeeId(findEmployeeIdByGithubUsername(username));
            effort.setEffortType(effortType);
            effort.setPayload(payload);
            effort.setTimestamp(new Date());

            return effort;
        } catch (Exception e) {
            log.error("Error normalizing GitHub payload", e);
            return null;
        }
    }

    /**
     * Detect GitHub effort type from webhook
     */
    private String detectGithubEffortType(String action, Map<String, Object> payload) {
        if ("opened".equals(action) || "reopened".equals(action)) {
            return "feature-work";
        } else if ("closed".equals(action)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
            Boolean merged = (Boolean) pr.get("merged");
            return merged ? "feature-work" : "collaboration";
        } else if ("synchronize".equals(action)) {
            return "collaboration";
        }
        return "collaboration";
    }

    /**
     * Normalize Bitbucket webhook payload
     */
    private Effort normalizeBitbucketPayload(Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> pullRequest = (Map<String, Object>) payload.get("pullrequest");
            @SuppressWarnings("unchecked")
            Map<String, Object> author = (Map<String, Object>) pullRequest.get("author");
            @SuppressWarnings("unchecked")
            Map<String, Object> userMap = (Map<String, Object>) author.get("user");
            
            String username = (String) userMap.get("username");

            Effort effort = new Effort();
            effort.setSource("bitbucket");
            effort.setEmployeeId(findEmployeeIdByGithubUsername(username));
            effort.setEffortType("feature-work");
            effort.setPayload(payload);
            effort.setTimestamp(new Date());

            return effort;
        } catch (Exception e) {
            log.error("Error normalizing Bitbucket payload", e);
            return null;
        }
    }

    /**
     * Normalize Slack webhook payload
     */
    private Effort normalizeSlackPayload(Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>) payload.get("event");
            String userId = (String) event.get("user");

            Effort effort = new Effort();
            effort.setSource("slack");
            effort.setEmployeeId(findEmployeeIdBySlackId(userId));
            effort.setEffortType("collaboration");
            effort.setPayload(payload);
            effort.setTimestamp(new Date());

            return effort;
        } catch (Exception e) {
            log.error("Error normalizing Slack payload", e);
            return null;
        }
    }

    /**
     * Normalize test payload
     */
    private Effort normalizeTestPayload(Map<String, Object> payload) {
        try {
            String employeeId = (String) payload.get("employeeId");
            String effortType = (String) payload.get("effortType");

            Effort effort = new Effort();
            effort.setSource("test");
            effort.setEmployeeId(employeeId != null ? employeeId : "user-001");
            effort.setEffortType(effortType != null ? effortType : "collaboration");
            effort.setPayload(payload);
            effort.setTimestamp(new Date());

            return effort;
        } catch (Exception e) {
            log.error("Error normalizing test payload", e);
            return null;
        }
    }

    /**
     * Verify webhook signature for security
     */
    public boolean verifyWebhookSignature(Map<String, Object> payload, String signature, String source) {
        try {
            String secret = getWebhookSecret(source);
            if (secret == null || secret.isEmpty()) {
                log.warn("No webhook secret configured for source: {}", source);
                return true;
            }

            String expectedSignature = generateHmacSignature(payload.toString(), secret);
            boolean isValid = expectedSignature.equals(signature);

            if (!isValid) {
                log.warn("Invalid webhook signature for source: {}", source);
            }

            return isValid;
        } catch (Exception e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }

    /**
     * Generate HMAC signature for verification
     */
    private String generateHmacSignature(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    /**
     * Get webhook secret for source
     */
    private String getWebhookSecret(String source) {
        switch (source.toLowerCase()) {
            case "jira":
                return JIRA_WEBHOOK_SECRET;
            case "github":
                return GITHUB_WEBHOOK_SECRET;
            case "slack":
                return SLACK_WEBHOOK_SECRET;
            default:
                return null;
        }
    }

    /**
     * Async processing of effort
     */
    @Async
    public void processEffortAsync(Effort effort) {
        try {
            log.info("Starting async processing for effort: {}", effort.getId());
            if (effortProcessingService != null) {
                effortProcessingService.processNewEffort(effort);
            } else {
                analyzeAndRecognizeEffort(effort);
            }
        } catch (Exception e) {
            log.error("Error in async effort processing", e);
        }
    }

    /**
     * Find employee ID by email
     */
    private String findEmployeeIdByEmail(String email) {
        return employeeRepository.findByEmail(email)
            .map(emp -> emp.getId())
            .orElse(null);
    }

    /**
     * Find employee ID by GitHub username
     */
    private String findEmployeeIdByGithubUsername(String username) {
        return "user-" + username.hashCode();
    }

    /**
     * Find employee ID by Slack ID
     */
    private String findEmployeeIdBySlackId(String slackId) {
        return "user-" + slackId.hashCode();
    }

    /**
     * Analyze effort and generate recognition
     */
    private void analyzeAndRecognizeEffort(Effort effort) {
        try {
            // Use AI to classify and score effort
            Effort analyzedEffort = aiAnalyzerService.analyzeEffort(effort);
            
            // Update effort with classification
            effortRepository.save(analyzedEffort);
            
            // Generate recognition if impact is significant
            if (analyzedEffort.getImpactScore() != null && analyzedEffort.getImpactScore() >= 6) {
                Recognition recognition = recognitionService.generateRecognition(analyzedEffort);
                log.info("Generated recognition: {} for effort: {}", recognition.getId(), effort.getId());
            }
            
        } catch (Exception e) {
            log.error("Error analyzing effort: {}", effort.getId(), e);
        }
    }
}
