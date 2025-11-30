package com.kudosly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kudosly.service.EffortIntakeService;
import com.kudosly.model.Effort;
import java.util.Map;

/**
 * Webhook Controller for receiving effort events from external tools
 * Supports: Jira, Bitbucket, GitHub, Slack, Microsoft Teams
 */
@RestController
@RequestMapping("/api/v1/webhooks")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class WebhookController {

    @Autowired
    private EffortIntakeService effortIntakeService;

    /**
     * Generic webhook endpoint for receiving effort events
     * 
     * @param payload Event payload from external tool
     * @param source Integration source (jira, github, slack, etc.)
     * @param signature Webhook signature for verification
     * @return Created Effort object
     */
    @PostMapping("/efforts")
    public ResponseEntity<Effort> receiveEffortWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestParam(defaultValue = "unknown") String source,
            @RequestHeader(value = "X-Webhook-Signature", required = false) String signature) {
        
        try {
            // Verify webhook signature
            if (signature != null && !signature.isEmpty()) {
                if (!effortIntakeService.verifyWebhookSignature(payload, signature, source)) {
                    return ResponseEntity.status(401).build();
                }
            }

            // Process webhook and create effort
            Effort effort = effortIntakeService.processWebhook(payload, source);
            
            return ResponseEntity.ok(effort);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Jira-specific webhook endpoint
     */
    @PostMapping("/jira")
    public ResponseEntity<Effort> receiveJiraWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Atlassian-Webhook-Signature", required = false) String signature) {
        
        return receiveEffortWebhook(payload, "jira", signature);
    }

    /**
     * Bitbucket-specific webhook endpoint
     */
    @PostMapping("/bitbucket")
    public ResponseEntity<Effort> receiveBitbucketWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Hook-UUID", required = false) String signature) {
        
        return receiveEffortWebhook(payload, "bitbucket", signature);
    }

    /**
     * GitHub-specific webhook endpoint
     */
    @PostMapping("/github")
    public ResponseEntity<Effort> receiveGithubWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {
        
        return receiveEffortWebhook(payload, "github", signature);
    }

    /**
     * Slack-specific webhook endpoint
     */
    @PostMapping("/slack")
    public ResponseEntity<Effort> receiveSlackWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Slack-Request-Timestamp", required = false) String timestamp,
            @RequestHeader(value = "X-Slack-Signature", required = false) String signature) {
        
        return receiveEffortWebhook(payload, "slack", signature);
    }

    /**
     * Health check endpoint for webhook verification
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Webhook Handler",
            "timestamp", java.time.Instant.now().toString()
        ));
    }

    /**
     * Test endpoint for webhook testing (development only)
     */
    @PostMapping("/test")
    public ResponseEntity<Effort> testWebhook(@RequestBody Map<String, Object> payload) {
        try {
            Effort effort = effortIntakeService.processWebhook(payload, "test");
            return ResponseEntity.ok(effort);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
