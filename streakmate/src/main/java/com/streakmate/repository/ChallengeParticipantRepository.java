package com.streakmate.repository;

import com.streakmate.model.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
    Optional<ChallengeParticipant> findByUserIdAndChallengeId(Long userId, Long challengeId);
    boolean existsByUserIdAndChallengeId(Long userId, Long challengeId);
    List<ChallengeParticipant> findByUserId(Long userId);
    List<ChallengeParticipant> findByChallengeId(Long challengeId);

    @Query("SELECT cp FROM ChallengeParticipant cp WHERE cp.user.id = :userId AND cp.challenge.status = 'ACTIVE'")
    List<ChallengeParticipant> findActiveByUserId(@Param("userId") Long userId);
}
