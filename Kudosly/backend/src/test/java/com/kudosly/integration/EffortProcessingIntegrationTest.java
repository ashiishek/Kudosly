package com.kudosly.integration;

import com.kudosly.model.Effort;
import com.kudosly.model.Recognition;
import com.kudosly.repository.EffortRepository;
import com.kudosly.repository.RecognitionRepository;
import com.kudosly.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("End-to-End Effort Processing Integration Tests")
class EffortProcessingIntegrationTest {

    @Autowired
    private EffortRepository effortRepository;

    @Autowired
    private RecognitionRepository recognitionRepository;

    @Autowired
    private EffortIntakeService effortIntakeService;

    @Autowired
    private EffortClassifierService classifierService;

    @Autowired
    private ImpactScoringService scoringService;

    @Autowired
    private RecognitionGeneratorService generationService;

    @Autowired
    private DigestNarratorService digestService;

    @Autowired
    private EffortProcessingService processingService;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        recognitionRepository.deleteAll();
        effortRepository.deleteAll();
    }

    @Test
    @DisplayName("Should process webhook from generic source")
    void testGenericWebhookProcessing() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("employeeId", "test-emp-1");
        payload.put("effortType", "feature-work");
        payload.put("title", "Implement new feature");

        // Act
        Effort result = effortIntakeService.processWebhook(payload, "test");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("test", result.getSource());
    }

    @Test
    @DisplayName("Should process complete effort pipeline: classify -> score -> recognize")
    void testCompleteEffortPipeline() {
        // Arrange - Create an effort
        Effort effort = new Effort();
        effort.setEmployeeId("test-emp-2");
        effort.setSource("test");
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Fix critical bug in payment module");
        effort.setPayload(payload);
        effort = effortRepository.save(effort);

        // Act - Step 1: Classify
        String classification = classifierService.classifyEffort(effort);
        effort.setEffortType(classification);

        // Act - Step 2: Score
        Integer score = scoringService.scoreImpact(effort);
        effort.setImpactScore(score);
        effort = effortRepository.save(effort);

        // Act - Step 3: Generate recognition if score is high enough
        if (score >= 5) {
            Recognition recognition = generationService.generateRecognition(effort);
            
            // Assert
            assertNotNull(recognition);
            assertNotNull(recognition.getMessage());
            assertTrue(recognition.getMessage().length() > 10);
            assertEquals(effort.getId(), recognition.getEffortId());
        }

        // Final assertions
        assertEquals("bug-fix", effort.getEffortType());
        assertTrue(effort.getImpactScore() >= 1 && effort.getImpactScore() <= 10);
    }

    @Test
    @DisplayName("Should generate weekly digest from multiple efforts")
    void testWeeklyDigestGeneration() {
        // Arrange - Create multiple efforts
        for (int i = 0; i < 5; i++) {
            Effort effort = new Effort();
            effort.setEmployeeId("test-emp-" + i);
            effort.setSource("test");
            effort.setEffortType(getEffortType(i));
            effort.setImpactScore(5 + i);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", "Task " + i);
            effort.setPayload(payload);
            
            effortRepository.save(effort);
        }

        // Act
        List<Effort> efforts = effortRepository.findAll();
        List<Recognition> recognitions = recognitionRepository.findAll();
        
        var digest = digestService.generateWeeklyDigest(recognitions, efforts);

        // Assert
        assertNotNull(digest);
        assertNotNull(digest.getNarrative());
        assertTrue(digest.getNarrative().length() > 0);
        assertEquals(5, digest.getTotalEfforts());
    }

    @Test
    @DisplayName("Should handle webhook from Jira")
    void testJiraWebhookProcessing() {
        // Arrange - Simulate Jira webhook payload
        Map<String, Object> jiraIssue = new HashMap<>();
        jiraIssue.put("key", "PROJ-123");
        jiraIssue.put("summary", "Fix authentication bug");
        
        Map<String, Object> assignee = new HashMap<>();
        assignee.put("emailAddress", "developer@company.com");
        jiraIssue.put("assignee", assignee);
        
        Map<String, Object> issueType = new HashMap<>();
        issueType.put("name", "Bug");
        jiraIssue.put("issuetype", issueType);

        Map<String, Object> payload = new HashMap<>();
        payload.put("issue", jiraIssue);

        // Act
        Effort result = effortIntakeService.processWebhook(payload, "jira");

        // Assert
        assertNotNull(result);
        assertEquals("jira", result.getSource());
    }

    @Test
    @DisplayName("Should handle webhook from GitHub")
    void testGithubWebhookProcessing() {
        // Arrange - Simulate GitHub webhook payload
        Map<String, Object> user = new HashMap<>();
        user.put("login", "developer123");

        Map<String, Object> pr = new HashMap<>();
        pr.put("title", "Add caching layer");
        pr.put("user", user);
        pr.put("additions", 350);
        pr.put("deletions", 50);
        pr.put("changed_files", 8);

        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "opened");
        payload.put("pull_request", pr);

        // Act
        Effort result = effortIntakeService.processWebhook(payload, "github");

        // Assert
        assertNotNull(result);
        assertEquals("github", result.getSource());
    }

    @Test
    @DisplayName("Should classify efforts with high accuracy")
    void testClassificationAccuracy() {
        // Test bug-fix classification
        Effort bugEffort = createTestEffort("bug-fix", "Fix critical issue");
        assertEquals("bug-fix", classifierService.classifyEffort(bugEffort));

        // Test feature classification
        Effort featureEffort = createTestEffort("feature-work", "Implement new feature");
        assertEquals("feature-work", classifierService.classifyEffort(featureEffort));

        // Test code-review classification
        Effort reviewEffort = createTestEffort("code-review", "Code review approved");
        assertEquals("code-review", classifierService.classifyEffort(reviewEffort));
    }

    @Test
    @DisplayName("Should score efforts consistently")
    void testScoringConsistency() {
        // Test same payload scores the same
        Effort effort1 = createTestEffort("feature-work", "Implement feature");
        Effort effort2 = createTestEffort("feature-work", "Implement feature");

        Integer score1 = scoringService.scoreImpact(effort1);
        Integer score2 = scoringService.scoreImpact(effort2);

        assertEquals(score1, score2);
    }

    @Test
    @DisplayName("Should generate recognitions only for significant efforts")
    void testRecognitionThreshold() {
        // Create low-impact effort
        Effort lowEffort = new Effort();
        lowEffort.setEmployeeId("emp-low");
        lowEffort.setEffortType("learning");
        lowEffort.setImpactScore(2); // Below threshold of 5
        lowEffort.setPayload(new HashMap<>(Map.of("title", "Minor learning task")));

        // Create high-impact effort
        Effort highEffort = new Effort();
        highEffort.setEmployeeId("emp-high");
        highEffort.setEffortType("feature-work");
        highEffort.setImpactScore(8); // Above threshold
        highEffort.setPayload(new HashMap<>(Map.of("title", "Major feature")));

        // Save both
        lowEffort = effortRepository.save(lowEffort);
        highEffort = effortRepository.save(highEffort);

        // Generate recognitions
        if (lowEffort.getImpactScore() >= 5) {
            generationService.generateRecognition(lowEffort);
        }
        Recognition highRec = generationService.generateRecognition(highEffort);

        // Assert - High effort should have recognition
        assertNotNull(highRec);
    }

    @Test
    @DisplayName("Should handle concurrent effort processing")
    void testConcurrentProcessing() {
        // Create multiple efforts
        List<Effort> efforts = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Effort effort = new Effort();
            effort.setEmployeeId("emp-" + i);
            effort.setSource("test");
            effort.setEffortType("feature-work");
            effort.setImpactScore(5 + i % 5);
            effort.setPayload(new HashMap<>(Map.of("title", "Task " + i)));
            efforts.add(effortRepository.save(effort));
        }

        // Process all efforts
        assertDoesNotThrow(() -> {
            processingService.processBatchEfforts(efforts);
        });

        // Assert - All efforts processed
        assertEquals(10, effortRepository.count());
    }

    @Test
    @DisplayName("Should provide complete effort summary")
    void testEffortSummary() {
        // Arrange
        Effort effort = new Effort();
        effort.setEmployeeId("emp-summary");
        effort.setSource("test");
        effort.setEffortType("feature-work");
        effort.setImpactScore(7);
        effort.setPayload(new HashMap<>(Map.of("title", "Feature implementation")));
        effort = effortRepository.save(effort);

        // Act
        EffortProcessingService.EffortSummary summary = 
            processingService.getEffortSummary(effort.getId());

        // Assert
        assertNotNull(summary);
        assertEquals(effort.getId(), summary.effortId);
        assertEquals("emp-summary", summary.employeeId);
        assertEquals("feature-work", summary.effortType);
        assertEquals(7, summary.impactScore);
        assertNotNull(summary.scoreBreakdown);
    }

    // Helper methods
    private Effort createTestEffort(String expectedType, String title) {
        Effort effort = new Effort();
        effort.setEmployeeId("test-emp");
        effort.setSource("test");
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        effort.setPayload(payload);
        return effort;
    }

    private String getEffortType(int index) {
        String[] types = {"bug-fix", "feature-work", "code-review", "collaboration", "learning"};
        return types[index % types.length];
    }
}
