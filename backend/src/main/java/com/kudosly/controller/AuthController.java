package com.kudosly.controller;

import com.kudosly.model.Employee;
import com.kudosly.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Login endpoint - authenticates user by email and password
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        // Find user by email
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(loginRequest.getEmail());

        if (employeeOpt.isEmpty()) {
            log.warn("Login failed: User not found for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid email or password"));
        }

        Employee employee = employeeOpt.get();

        // Simple password validation (in production, use BCrypt or similar)
        if (!validatePassword(loginRequest.getPassword(), employee)) {
            log.warn("Login failed: Invalid password for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid email or password"));
        }

        log.info("Login successful for email: {}", loginRequest.getEmail());

        // Generate token (simple token generation, use JWT in production)
        String token = generateToken(employee);

        // Return response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", employee.getId());
        userMap.put("name", employee.getName());
        userMap.put("email", employee.getEmail());
        userMap.put("role", employee.getRole());
        userMap.put("team", employee.getTeam());

        response.put("user", userMap);

        return ResponseEntity.ok(response);
    }

    /**
     * Register endpoint - creates new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        log.info("Registration attempt for email: {}", registerRequest.getEmail());

        // Check if user already exists
        Optional<Employee> existingEmployee = employeeRepository.findByEmail(registerRequest.getEmail());
        if (existingEmployee.isPresent()) {
            log.warn("Registration failed: User already exists with email: {}", registerRequest.getEmail());
            return ResponseEntity.status(409).body(new ErrorResponse("User already exists"));
        }

        // Create new employee
        Employee newEmployee = new Employee();
        newEmployee.setName(registerRequest.getName());
        newEmployee.setEmail(registerRequest.getEmail());
        newEmployee.setPassword(registerRequest.getPassword()); // In production, hash this
        newEmployee.setTeam(registerRequest.getTeam() != null ? registerRequest.getTeam() : "General");
        newEmployee.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : "Employee");
        newEmployee.setIsActive(true);

        // Save employee
        Employee savedEmployee = employeeRepository.save(newEmployee);
        log.info("Registration successful for email: {}", registerRequest.getEmail());

        // Generate token
        String token = generateToken(savedEmployee);

        // Return response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", savedEmployee.getId());
        userMap.put("name", savedEmployee.getName());
        userMap.put("email", savedEmployee.getEmail());
        userMap.put("role", savedEmployee.getRole());
        userMap.put("team", savedEmployee.getTeam());

        response.put("user", userMap);

        return ResponseEntity.ok(response);
    }

    /**
     * Verify token endpoint
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).body(new ErrorResponse("Missing token"));
        }

        // Remove "Bearer " prefix if present
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        // Find user by token (simple implementation)
        Optional<Employee> employeeOpt = employeeRepository.findByToken(actualToken);

        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid token"));
        }

        Employee employee = employeeOpt.get();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", employee.getId());
        userMap.put("name", employee.getName());
        userMap.put("email", employee.getEmail());
        userMap.put("role", employee.getRole());
        userMap.put("team", employee.getTeam());

        return ResponseEntity.ok(userMap);
    }

    /**
     * Simple password validation (in production, use proper hashing)
     */
    private boolean validatePassword(String rawPassword, Employee employee) {
        // For demo purposes, compare directly
        // In production: use BCrypt, bcryptjs, or similar
        return rawPassword != null && rawPassword.equals(employee.getPassword());
    }

    /**
     * Generate simple token (in production, use JWT)
     */
    private String generateToken(Employee employee) {
        // Simple token generation using email + timestamp
        // In production: use JWT with proper signing
        long timestamp = System.currentTimeMillis();
        String token = java.util.Base64.getEncoder()
            .encodeToString((employee.getEmail() + ":" + timestamp + ":" + employee.getId()).getBytes());
        
        // Store token in employee record
        employee.setToken(token);
        employeeRepository.save(employee);
        
        return token;
    }

    // Request DTOs
    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {}

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        private String team;
        private String role;

        public RegisterRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getTeam() { return team; }
        public void setTeam(String team) { this.team = team; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
