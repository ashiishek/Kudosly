package com.kudosly.service;

import com.kudosly.model.Badge;
import com.kudosly.model.EmployeeBadge;
import com.kudosly.model.Effort;
import com.kudosly.repository.BadgeRepository;
import com.kudosly.repository.EffortRepository;
import com.kudosly.repository.EmployeeBadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final EmployeeBadgeRepository employeeBadgeRepository;
    private final EffortRepository effortRepository;

    /**
     * Get all available badges
     */
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    /**
     * Get badges earned by an employee
     */
    public List<Badge> getBadgesByEmployee(String employeeId) {
        log.info("Getting badges for employee: {}", employeeId);
        List<EmployeeBadge> earnedBadges = employeeBadgeRepository.findByEmployeeId(employeeId);
        log.info("Found {} employee badges", earnedBadges.size());
        
        List<String> badgeIds = earnedBadges.stream()
                .map(EmployeeBadge::getBadgeId)
                .collect(Collectors.toList());
        
        log.info("Badge IDs: {}", badgeIds);
        
        if (badgeIds.isEmpty()) {
            log.info("No badge IDs found, returning empty list");
            return Collections.emptyList();
        }
        
        List<Badge> badges = badgeRepository.findByBadgeIdIn(badgeIds);
        log.info("Found {} badges", badges.size());
        
        return badges;
    }

    /**
     * Award a badge to an employee
     */
    public EmployeeBadge awardBadge(String employeeId, String badgeId) {
        log.info("Awarding badge {} to employee {}", badgeId, employeeId);
        
        // Check if already earned
        Optional<EmployeeBadge> existing = employeeBadgeRepository.findByEmployeeIdAndBadgeId(employeeId, badgeId);
        if (existing.isPresent()) {
            log.info("Badge {} already earned by employee {}", badgeId, employeeId);
            return existing.get();
        }
        
        EmployeeBadge employeeBadge = new EmployeeBadge();
        employeeBadge.setEmployeeId(employeeId);
        employeeBadge.setBadgeId(badgeId);
        employeeBadge.setEarnedDate(new Date());
        employeeBadge.setProgressPercentage(100);
        
        return employeeBadgeRepository.save(employeeBadge);
    }

    /**
     * Evaluate badge criteria and award if eligible
     */
    public void evaluateBadgeCriteria(String employeeId) {
        log.info("Evaluating badge criteria for employee {}", employeeId);
        
        List<Badge> allBadges = getAllBadges();
        for (Badge badge : allBadges) {
            if (shouldAwardBadge(employeeId, badge)) {
                awardBadge(employeeId, badge.getBadgeId());
                log.info("Badge {} awarded to employee {}", badge.getName(), employeeId);
            }
        }
    }

    /**
     * Check if an employee qualifies for a badge based on criteria
     */
    private boolean shouldAwardBadge(String employeeId, Badge badge) {
        Map<String, Object> criteria = badge.getCriteria();
        
        switch (badge.getBadgeId()) {
            case "collaboration-hero":
                return checkCollaborationHero(employeeId, criteria);
            case "problem-solver":
                return checkProblemSolver(employeeId, criteria);
            case "knowledge-sharer":
                return checkKnowledgeSharer(employeeId, criteria);
            case "consistency-champion":
                return checkConsistencyChampion(employeeId, criteria);
            case "innovation-spark":
                return checkInnovationSpark(employeeId, criteria);
            case "team-player":
                return checkTeamPlayer(employeeId, criteria);
            default:
                return false;
        }
    }

    private boolean checkCollaborationHero(String employeeId, Map<String, Object> criteria) {
        int minEfforts = (Integer) criteria.getOrDefault("minCollaborationEfforts", 10);
        List<Effort> efforts = effortRepository.findByEmployeeId(employeeId);
        long collaborationCount = efforts.stream()
                .filter(e -> "collaboration".equals(e.getEffortType()))
                .count();
        return collaborationCount >= minEfforts;
    }

    private boolean checkProblemSolver(String employeeId, Map<String, Object> criteria) {
        int minBugFixes = (Integer) criteria.getOrDefault("minBugFixes", 5);
        int minImpactScore = (Integer) criteria.getOrDefault("minImpactScore", 8);
        List<Effort> efforts = effortRepository.findByEmployeeId(employeeId);
        long qualifyingFixes = efforts.stream()
                .filter(e -> "bug-fix".equals(e.getEffortType()))
                .filter(e -> e.getImpactScore() != null && e.getImpactScore() >= minImpactScore)
                .count();
        return qualifyingFixes >= minBugFixes;
    }

    private boolean checkKnowledgeSharer(String employeeId, Map<String, Object> criteria) {
        int minMentoring = (Integer) criteria.getOrDefault("minMentoringEfforts", 5);
        int minCodeReviews = (Integer) criteria.getOrDefault("minCodeReviews", 10);
        List<Effort> efforts = effortRepository.findByEmployeeId(employeeId);
        
        long mentoringCount = efforts.stream()
                .filter(e -> "mentoring".equals(e.getEffortType()))
                .count();
        
        long codeReviewCount = efforts.stream()
                .filter(e -> "code-review".equals(e.getEffortType()))
                .count();
        
        return mentoringCount >= minMentoring && codeReviewCount >= minCodeReviews;
    }

    private boolean checkConsistencyChampion(String employeeId, Map<String, Object> criteria) {
        int minDailyEfforts = (Integer) criteria.getOrDefault("minDailyEfforts", 3);
        List<Effort> efforts = effortRepository.findByEmployeeId(employeeId);
        
        if (efforts.isEmpty()) {
            return false;
        }
        
        // Check if at least 30 days of consecutive efforts
        long effortCount = efforts.size();
        return effortCount >= (minDailyEfforts * 30);
    }

    private boolean checkInnovationSpark(String employeeId, Map<String, Object> criteria) {
        int minFeatures = (Integer) criteria.getOrDefault("minInnovativeFeatures", 3);
        int minImpactScore = (Integer) criteria.getOrDefault("minImpactScore", 9);
        List<Effort> efforts = effortRepository.findByEmployeeId(employeeId);
        
        long innovativeFeatures = efforts.stream()
                .filter(e -> "feature-work".equals(e.getEffortType()))
                .filter(e -> e.getImpactScore() != null && e.getImpactScore() >= minImpactScore)
                .count();
        
        return innovativeFeatures >= minFeatures;
    }

    private boolean checkTeamPlayer(String employeeId, Map<String, Object> criteria) {
        int minTeamEfforts = (Integer) criteria.getOrDefault("minTeamEfforts", 20);
        double positiveRatio = (Double) criteria.getOrDefault("positiveCollaborationRatio", 0.8);
        
        List<Effort> efforts = effortRepository.findByEmployeeId(employeeId);
        long teamEfforts = efforts.stream()
                .filter(e -> "collaboration".equals(e.getEffortType()))
                .count();
        
        return teamEfforts >= minTeamEfforts;
    }

    /**
     * Get badge progress for an employee
     */
    public Map<String, Object> getBadgeProgress(String employeeId, String badgeId) {
        Map<String, Object> progress = new HashMap<>();
        
        Badge badge = badgeRepository.findById(badgeId).orElse(null);
        if (badge == null) {
            return progress;
        }
        
        Optional<EmployeeBadge> earned = employeeBadgeRepository.findByEmployeeIdAndBadgeId(employeeId, badgeId);
        progress.put("earned", earned.isPresent());
        
        if (earned.isPresent()) {
            progress.put("earnedDate", earned.get().getEarnedDate());
            progress.put("progress", 100);
        } else {
            progress.put("progress", calculateBadgeProgress(employeeId, badge));
        }
        
        return progress;
    }

    /**
     * Award badge for effort based on type and impact score
     */
    public void awardBadgeForEffort(Effort effort, String effortType, Integer impactScore) {
        log.info("Awarding badge for effort {} of type {} with impact {}", effort.getId(), effortType, impactScore);
        
        String badgeId = null;
        switch (effortType) {
            case "bug-fix":
                if (impactScore >= 8) {
                    badgeId = "problem-solver";
                }
                break;
            case "feature-work":
                if (impactScore >= 9) {
                    badgeId = "innovation-spark";
                }
                break;
            case "code-review":
                if (impactScore >= 7) {
                    badgeId = "knowledge-sharer";
                }
                break;
            case "collaboration":
                if (impactScore >= 7) {
                    badgeId = "collaboration-hero";
                }
                break;
            case "mentoring":
                if (impactScore >= 8) {
                    badgeId = "knowledge-sharer";
                }
                break;
        }
        
        if (badgeId != null) {
            awardBadge(effort.getEmployeeId(), badgeId);
        }
    }

    private int calculateBadgeProgress(String employeeId, Badge badge) {
        List<Effort> efforts = effortRepository.findByEmployeeId(employeeId);
        
        switch (badge.getBadgeId()) {
            case "collaboration-hero":
                long collabCount = efforts.stream()
                        .filter(e -> "collaboration".equals(e.getEffortType()))
                        .count();
                int minCollab = (Integer) badge.getCriteria().getOrDefault("minCollaborationEfforts", 10);
                return Math.min((int) (collabCount * 100 / minCollab), 99);
                
            case "problem-solver":
                long bugFixCount = efforts.stream()
                        .filter(e -> "bug-fix".equals(e.getEffortType()))
                        .count();
                int minBugs = (Integer) badge.getCriteria().getOrDefault("minBugFixes", 5);
                return Math.min((int) (bugFixCount * 100 / minBugs), 99);
                
            default:
                return 0;
        }
    }
}
