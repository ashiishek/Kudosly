package com.kudosly.repository;

import com.kudosly.model.EmployeeBadge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeBadgeRepository extends MongoRepository<EmployeeBadge, String> {
    List<EmployeeBadge> findByEmployeeId(String employeeId);
    
    Optional<EmployeeBadge> findByEmployeeIdAndBadgeId(String employeeId, String badgeId);
    
    List<EmployeeBadge> findByBadgeId(String badgeId);
}
