package com.streakmate.repository;

import com.streakmate.model.Challenge;
import com.streakmate.model.Challenge.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> findByChallengeCode(String code);
    boolean existsByChallengeCode(String code);
    List<Challenge> findByCreatorId(Long creatorId);
    List<Challenge> findByStatus(ChallengeStatus status);

    @Query("SELECT c FROM Challenge c WHERE c.endDate < :today AND c.status = 'ACTIVE'")
    List<Challenge> findExpiredChallenges(@Param("today") LocalDate today);
}
