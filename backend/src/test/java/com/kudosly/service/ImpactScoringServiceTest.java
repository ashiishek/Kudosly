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

@DisplayName("Impact Scoring Service Tests")
class ImpactScoringServiceTest {

    @InjectMocks
    private ImpactScoringService scoringService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should score feature-work efforts higher than collaboration")
    void testFeatureWorkScore() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("feature-work");
        effort.setPayload(new HashMap<>());

        // Act
        Integer score = scoringService.scoreImpact(effort);

        // Assert
        assertTrue(score > 5);
    }

    @Test
    @DisplayName("Should apply complexity bonus for performance work")
    void testComplexityBonus() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("feature-work");
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", "Performance optimization and refactoring");
        effort.setPayload(payload);

        // Act
        Integer score = scoringService.scoreImpact(effort);

        // Assert
        assertTrue(score >= 7);
    }

    @Test
    @DisplayName("Should apply scope bonus for large changes")
    void testScopeBonus() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("feature-work");
        
        Map<String, Object> prData = new HashMap<>();
        prData.put("changed_files", 10);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("pull_request", prData);
        effort.setPayload(payload);

        // Act
        Integer score = scoringService.scoreImpact(effort);

        // Assert
        assertTrue(score >= 7);
    }

    @Test
    @DisplayName("Should apply quality bonus for tested code")
    void testQualityBonus() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("feature-work");
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", "Includes comprehensive tests and documentation");
        effort.setPayload(payload);

        // Act
        Integer score = scoringService.scoreImpact(effort);

        // Assert
        assertTrue(score >= 8);
    }

    @Test
    @DisplayName("Should keep score within 1-10 range")
    void testScoreRangeUpperBound() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("feature-work");
        Map<String, Object> prData = new HashMap<>();
        prData.put("changed_files", 50);
        prData.put("additions", 5000);
        prData.put("deletions", 1000);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("pull_request", prData);
        payload.put("text", "Performance optimization, refactoring, security");
        effort.setPayload(payload);

        // Act
        Integer score = scoringService.scoreImpact(effort);

        // Assert
        assertTrue(score >= 1 && score <= 10);
    }

    @Test
    @DisplayName("Should return minimum score of 1")
    void testMinimumScore() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("learning");
        effort.setPayload(new HashMap<>());

        // Act
        Integer score = scoringService.scoreImpact(effort);

        // Assert
        assertTrue(score >= 1);
    }

    @Test
    @DisplayName("Should categorize scores correctly")
    void testImpactCategories() {
        // Assert
        assertEquals("transformational", scoringService.getImpactCategory(9));
        assertEquals("transformational", scoringService.getImpactCategory(10));
        assertEquals("significant", scoringService.getImpactCategory(7));
        assertEquals("significant", scoringService.getImpactCategory(8));
        assertEquals("moderate", scoringService.getImpactCategory(5));
        assertEquals("moderate", scoringService.getImpactCategory(6));
        assertEquals("small", scoringService.getImpactCategory(3));
        assertEquals("small", scoringService.getImpactCategory(4));
        assertEquals("minimal", scoringService.getImpactCategory(1));
        assertEquals("minimal", scoringService.getImpactCategory(2));
    }

    @Test
    @DisplayName("Should provide detailed score breakdown")
    void testScoreBreakdown() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("feature-work");
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", "Performance optimization");
        effort.setPayload(payload);

        // Act
        Map<String, Object> breakdown = scoringService.getScoreBreakdown(effort);

        // Assert
        assertNotNull(breakdown);
        assertTrue(breakdown.containsKey("totalScore"));
        assertTrue(breakdown.containsKey("baseScore"));
        assertTrue(breakdown.containsKey("complexityBonus"));
        assertTrue(breakdown.containsKey("scopeBonus"));
        assertTrue(breakdown.containsKey("qualityBonus"));
        assertTrue(breakdown.containsKey("effortType"));
    }

    @Test
    @DisplayName("Should handle null impact score")
    void testNullScore() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("bug-fix");
        effort.setPayload(new HashMap<>());

        // Act & Assert
        assertDoesNotThrow(() -> scoringService.scoreImpact(effort));
    }

    @Test
    @DisplayName("Should detect security improvements")
    void testSecurityDetection() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("bug-fix");
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", "Security vulnerability fix in authentication");
        effort.setPayload(payload);

        // Act
        Integer score = scoringService.scoreImpact(effort);

        // Assert
        assertTrue(score >= 6);
    }

    @Test
    @DisplayName("Should detect database work")
    void testDatabaseDetection() {
        // Arrange
        Effort effort = new Effort();
        effort.setEffortType("feature-work");
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", "Database migration and optimization");
        effort.setPayload(payload);

        // Act
        Integer score = scoringService.scoreImpact(effort);

        // Assert
        assertTrue(score >= 7);
    }
}
