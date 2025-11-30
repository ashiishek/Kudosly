package com.kudosly.repository;

import com.kudosly.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByToken(String token);
}
