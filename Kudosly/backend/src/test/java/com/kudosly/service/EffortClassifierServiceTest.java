package com.kudosly.service;

import com.kudosly.model.Effort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Effort Classifier Service Tests")
class EffortClassifierServiceTest {

    @InjectMocks
    private EffortClassifierService classifierService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should classify bug-fix efforts correctly")
    void testClassifyBugFix() {
        // Arrange
        Effort effort = new Effort();
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Fix critical bug in authentication module");
        effort.setPayload(payload);

        // Act
        String classification = classifierService.classifyEffort(effort);

        // Assert
        assertEquals("bug-fix", classification);
    }

    @Test
    @DisplayName("Should classify feature-work efforts correctly")
    void testClassifyFeatureWork() {
        // Arrange
        Effort effort = new Effort();
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Implement new dashboard feature");
        effort.setPayload(payload);

        // Act
        String classification = classifierService.classifyEffort(effort);

        // Assert
        assertEquals("feature-work", classification);
    }

    @Test
    @DisplayName("Should classify code-review efforts correctly")
    void testClassifyCodeReview() {
        // Arrange
        Effort effort = new Effort();
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Code review: Approved PR-123");
        effort.setPayload(payload);

        // Act
        String classification = classifierService.classifyEffort(effort);

        // Assert
        assertEquals("code-review", classification);
    }

    @Test
    @DisplayName("Should classify collaboration efforts correctly")
    void testClassifyCollaboration() {
        // Arrange
        Effort effort = new Effort();
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Team discussion and pair programming session");
        effort.setPayload(payload);

        // Act
        String classification = classifierService.classifyEffort(effort);

        // Assert
        assertEquals("collaboration", classification);
    }

    @Test
    @DisplayName("Should calculate confidence score correctly")
    void testConfidenceScore() {
        // Arrange
        Effort effort = new Effort();
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Fix bug and patch issue");
        effort.setPayload(payload);

        // Act
        int confidence = classifierService.getConfidenceScore(effort, "bug-fix");

        // Assert
        assertTrue(confidence >= 0 && confidence <= 100);
        assertTrue(confidence > 50); // Should be high confidence
    }

    @Test
    @DisplayName("Should use explicit effort type if provided")
    void testExplicitEffortType() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("mentoring");
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Some bug fix");
        effort.setPayload(payload);

        // Act
        String classification = classifierService.classifyEffort(effort);

        // Assert
        assertEquals("mentoring", classification);
    }

    @Test
    @DisplayName("Should extract text from Jira payload")
    void testJiraPayloadExtraction() {
        // Arrange
        Effort effort = new Effort();
        Map<String, Object> jiraIssue = new HashMap<>();
        jiraIssue.put("summary", "Fix login page bug");
        jiraIssue.put("description", "Users cannot log in from mobile");
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("issue", jiraIssue);
        effort.setPayload(payload);

        // Act
        String classification = classifierService.classifyEffort(effort);

        // Assert
        assertEquals("bug-fix", classification);
    }

    @Test
    @DisplayName("Should default to collaboration for unknown types")
    void testDefaultClassification() {
        // Arrange
        Effort effort = new Effort();
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Random unrelated text xyz");
        effort.setPayload(payload);

        // Act
        String classification = classifierService.classifyEffort(effort);

        // Assert
        assertEquals("collaboration", classification);
    }

    @Test
    @DisplayName("Should handle null payload gracefully")
    void testNullPayload() {
        // Arrange
        Effort effort = new Effort();
        effort.setPayload(new HashMap<>());

        // Act & Assert
        assertDoesNotThrow(() -> classifierService.classifyEffort(effort));
    }
}
