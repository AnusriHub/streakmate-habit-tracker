package com.streakmate.repository;

import com.streakmate.model.DailyCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyCheckInRepository extends JpaRepository<DailyCheckIn, Long> {
    Optional<DailyCheckIn> findByParticipantIdAndCheckInDate(Long participantId, LocalDate date);
    boolean existsByParticipantIdAndCheckInDate(Long participantId, LocalDate date);
    List<DailyCheckIn> findByParticipantIdOrderByCheckInDateDesc(Long participantId);

    @Query("SELECT COUNT(d) FROM DailyCheckIn d WHERE d.participant.id = :participantId AND d.completed = true")
    long countCompletedByParticipantId(@Param("participantId") Long participantId);

    @Query("SELECT d FROM DailyCheckIn d WHERE d.participant.user.id = :userId ORDER BY d.checkInDate DESC")
    List<DailyCheckIn> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT d FROM DailyCheckIn d WHERE d.participant.user.id = :userId AND d.checkInDate = :date")
    List<DailyCheckIn> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
