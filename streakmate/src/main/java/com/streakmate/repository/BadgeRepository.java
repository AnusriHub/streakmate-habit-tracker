package com.streakmate.repository;

import com.streakmate.model.Badge;
import com.streakmate.model.Badge.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByBadgeName(String badgeName);
    List<Badge> findByType(BadgeType type);
    List<Badge> findByTypeAndThresholdLessThanEqual(BadgeType type, Integer threshold);
}
