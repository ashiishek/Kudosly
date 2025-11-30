package com.kudosly.repository;

import com.kudosly.model.WeeklyDigest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface WeeklyDigestRepository extends MongoRepository<WeeklyDigest, String> {
    Optional<WeeklyDigest> findByEmployeeIdAndWeekStartAndWeekEnd(String employeeId, Date weekStart, Date weekEnd);
    Optional<WeeklyDigest> findTopByEmployeeIdOrderByWeekEndDesc(String employeeId);
}
