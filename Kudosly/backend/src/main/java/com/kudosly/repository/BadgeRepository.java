package com.kudosly.repository;

import com.kudosly.model.Badge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends MongoRepository<Badge, String> {
    Optional<Badge> findByName(String name);
    
    @Query("{ 'badgeId': ?0 }")
    Optional<Badge> findByBadgeId(String badgeId);
    
    @Query("{ 'badgeId': { $in: ?0 } }")
    List<Badge> findByBadgeIdIn(List<String> badgeIds);
}
