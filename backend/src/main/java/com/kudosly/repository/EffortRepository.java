package com.kudosly.repository;

import com.kudosly.model.Effort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EffortRepository extends MongoRepository<Effort, String> {
    List<Effort> findByEmployeeId(String employeeId);
    
    List<Effort> findByEmployeeIdAndTimestampBetween(String employeeId, Date start, Date end);
    
    List<Effort> findByTimestampAfter(Date timestamp);
    
    List<Effort> findByEmployeeIdOrderByTimestampDesc(String employeeId, Pageable pageable);
    
    List<Effort> findByEffortType(String effortType);
    
    List<Effort> findBySource(String source);
    
    @Query("{ 'employeeId': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } }")
    List<Effort> findEffortsByEmployeeAndDateRange(String employeeId, Date startDate, Date endDate);
}
