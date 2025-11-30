package com.kudosly.repository;

import com.kudosly.model.Recognition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecognitionRepository extends MongoRepository<Recognition, String> {
    List<Recognition> findByEmployeeIdOrderByTimestampDesc(String employeeId);
    
    List<Recognition> findTop10ByEmployeeIdOrderByTimestampDesc(String employeeId);
    
    List<Recognition> findByEmployeeIdOrderByTimestampDesc(String employeeId, Pageable pageable);
    
    List<Recognition> findByEmployeeIdAndTimestampBetween(String employeeId, Date startDate, Date endDate);
    
    Optional<Recognition> findByEffortId(String effortId);
    
    @Query("{ 'employeeId': ?0 }")
    List<Recognition> findRecognitionsByEmployee(String employeeId, Pageable pageable);
}
