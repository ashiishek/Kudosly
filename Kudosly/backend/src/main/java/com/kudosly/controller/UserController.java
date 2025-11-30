package com.kudosly.controller;

import com.kudosly.dto.EmployeeDTO;
import com.kudosly.model.Employee;
import com.kudosly.model.Recognition;
import com.kudosly.repository.EmployeeRepository;
import com.kudosly.repository.RecognitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for user management and feed
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final RecognitionRepository recognitionRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Get a specific user by ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<EmployeeDTO> getUser(@PathVariable String userId) {
        log.info("Fetching user: {}", userId);
        
        Employee employee = employeeRepository.findById(userId).orElse(null);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        
        EmployeeDTO dto = convertToDTO(employee);
        return ResponseEntity.ok(dto);
    }

    /**
     * Get all users
     */
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllUsers(
            @RequestParam(required = false) String team,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching users");
        
        List<Employee> employees;
        if (team != null) {
            employees = employeeRepository.findAll().stream()
                    .filter(e -> team.equals(e.getTeam()))
                    .collect(Collectors.toList());
        } else {
            employees = employeeRepository.findAll();
        }
        
        List<EmployeeDTO> dtos = employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Create a new user
     */
    @PostMapping
    public ResponseEntity<EmployeeDTO> createUser(@RequestBody EmployeeDTO employeeDTO) {
        log.info("Creating new user: {}", employeeDTO.getEmail());
        
        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setTeam(employeeDTO.getTeam());
        employee.setRole(employeeDTO.getRole());
        employee.setJoinDate(employeeDTO.getJoinDate());
        
        Employee saved = employeeRepository.save(employee);
        
        return ResponseEntity.ok(convertToDTO(saved));
    }

    /**
     * Get user feed with all recognitions and stats
     */
    @GetMapping("/{userId}/feed")
    public ResponseEntity<Map<String, Object>> getUserFeed(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Fetching user feed for employee: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        List<Recognition> recognitions = recognitionRepository
            .findRecognitionsByEmployee(userId, pageable);
        
        // Calculate stats
        long totalRecognitions = recognitionRepository.findByEmployeeIdOrderByTimestampDesc(userId).size();
        long highImpact = recognitions.stream()
            .filter(r -> r.getImpactScore() != null && r.getImpactScore() >= 8)
            .count();
        
        Map<String, Object> feed = new HashMap<>();
        feed.put("recognitions", recognitions);
        feed.put("totalRecognitions", totalRecognitions);
        feed.put("highImpactCount", highImpact);
        feed.put("averageImpactScore", calculateAverageImpact(recognitions));
        
        return ResponseEntity.ok(feed);
    }

    /**
     * Get user statistics
     */
    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String userId) {
        log.info("Fetching stats for employee: {}", userId);
        
        List<Recognition> recognitions = recognitionRepository
                .findByEmployeeIdOrderByTimestampDesc(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecognitions", recognitions.size());
        stats.put("averageImpactScore", calculateAverageImpact(recognitions));
        stats.put("highImpactCount", recognitions.stream()
                .filter(r -> r.getImpactScore() != null && r.getImpactScore() >= 8)
                .count());
        
        return ResponseEntity.ok(stats);
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getGithubUsername(),
                employee.getSlackId(),
                employee.getTeam(),
                employee.getRole(),
                employee.getJoinDate(),
                employee.getIsActive(),
                employee.getDepartment(),
                employee.getManager(),
                employee.getAvatar(),
                employee.getBio(),
                employee.getSkills(),
                employee.getRecognitionCount(),
                employee.getBadgeCount(),
                employee.getTotalEffortScore(),
                employee.getLastActivityDate()
        );
    }

    private double calculateAverageImpact(List<Recognition> recognitions) {
        if (recognitions.isEmpty()) return 0.0;
        
        return recognitions.stream()
            .filter(r -> r.getImpactScore() != null)
            .mapToInt(Recognition::getImpactScore)
            .average()
            .orElse(0.0);
    }
}
