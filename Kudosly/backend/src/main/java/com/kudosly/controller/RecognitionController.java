package com.kudosly.controller;

import com.kudosly.model.Recognition;
import com.kudosly.repository.RecognitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for recognitions
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/recognitions")
@RequiredArgsConstructor
public class RecognitionController {

    private final RecognitionRepository recognitionRepository;

    /**
     * Get a specific recognition by ID
     */
    @GetMapping("/{recognitionId}")
    public ResponseEntity<Recognition> getRecognition(@PathVariable String recognitionId) {
        log.info("Fetching recognition: {}", recognitionId);
        
        Optional<Recognition> recognition = recognitionRepository.findById(recognitionId);
        
        if (recognition.isPresent()) {
            return ResponseEntity.ok(recognition.get());
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Get recognitions for an employee with pagination
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recognition>> getRecognitions(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Fetching recognitions for employee: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        List<Recognition> recognitions = recognitionRepository
            .findRecognitionsByEmployee(userId, pageable);
        
        return ResponseEntity.ok(recognitions);
    }

    /**
     * Get recent recognitions (top 10)
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<Recognition>> getRecentRecognitions(@PathVariable String userId) {
        log.info("Fetching recent recognitions for employee: {}", userId);
        
        List<Recognition> recognitions = recognitionRepository
            .findTop10ByEmployeeIdOrderByTimestampDesc(userId);
        
        return ResponseEntity.ok(recognitions);
    }

    /**
     * Get paginated recognition feed (all recognitions system-wide)
     */
    @GetMapping("/feed")
    public ResponseEntity<List<Recognition>> getRecognitionFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching recognition feed");
        
        Pageable pageable = PageRequest.of(page, size);
        List<Recognition> recognitions = recognitionRepository.findAll(pageable).getContent();
        
        return ResponseEntity.ok(recognitions);
    }
}
