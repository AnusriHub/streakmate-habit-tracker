package com.streakmate.service;

import com.streakmate.model.ChallengeParticipant;
import com.streakmate.model.DailyCheckIn;
import com.streakmate.repository.DailyCheckInRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StreakService – streak calculation logic")
class StreakServiceTest {

    @Mock  DailyCheckInRepository checkInRepository;
    @InjectMocks StreakService streakService;

    private final Long PARTICIPANT_ID = 1L;

    private DailyCheckIn checkIn(LocalDate date) {
        DailyCheckIn ci = new DailyCheckIn();
        ci.setCheckInDate(date);
        ci.setCompleted(true);
        ChallengeParticipant p = new ChallengeParticipant();
        p.setId(PARTICIPANT_ID);
        ci.setParticipant(p);
        return ci;
    }

    @Test
    @DisplayName("Returns 0 when no check-ins exist")
    void currentStreak_noCheckIns() {
        when(checkInRepository.findByParticipantIdOrderByCheckInDateDesc(PARTICIPANT_ID))
                .thenReturn(List.of());
        assertThat(streakService.calculateCurrentStreak(PARTICIPANT_ID)).isZero();
    }

    @Test
    @DisplayName("Returns 0 when last check-in was 2+ days ago")
    void currentStreak_broken() {
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        when(checkInRepository.findByParticipantIdOrderByCheckInDateDesc(PARTICIPANT_ID))
                .thenReturn(List.of(checkIn(twoDaysAgo)));
        assertThat(streakService.calculateCurrentStreak(PARTICIPANT_ID)).isZero();
    }

    @Test
    @DisplayName("Calculates consecutive streak correctly")
    void currentStreak_consecutive() {
        LocalDate today = LocalDate.now();
        List<DailyCheckIn> checkIns = List.of(
                checkIn(today),
                checkIn(today.minusDays(1)),
                checkIn(today.minusDays(2)),
                checkIn(today.minusDays(3))
        );
        when(checkInRepository.findByParticipantIdOrderByCheckInDateDesc(PARTICIPANT_ID))
                .thenReturn(checkIns);
        assertThat(streakService.calculateCurrentStreak(PARTICIPANT_ID)).isEqualTo(4);
    }

    @Test
    @DisplayName("Stops counting at first gap")
    void currentStreak_stopsAtGap() {
        LocalDate today = LocalDate.now();
        // gap on day 3 (today-3 is missing)
        List<DailyCheckIn> checkIns = List.of(
                checkIn(today),
                checkIn(today.minusDays(1)),
                checkIn(today.minusDays(2)),
                // gap here
                checkIn(today.minusDays(4))
        );
        when(checkInRepository.findByParticipantIdOrderByCheckInDateDesc(PARTICIPANT_ID))
                .thenReturn(checkIns);
        assertThat(streakService.calculateCurrentStreak(PARTICIPANT_ID)).isEqualTo(3);
    }

    @Test
    @DisplayName("Calculates longest streak correctly")
    void longestStreak_correctlyIdentified() {
        LocalDate today = LocalDate.now();
        // Two runs: 3-day and 5-day. 5-day should win.
        List<DailyCheckIn> checkIns = List.of(
                checkIn(today),
                checkIn(today.minusDays(1)),
                checkIn(today.minusDays(2)),
                // gap
                checkIn(today.minusDays(10)),
                checkIn(today.minusDays(11)),
                checkIn(today.minusDays(12)),
                checkIn(today.minusDays(13)),
                checkIn(today.minusDays(14))
        );
        when(checkInRepository.findByParticipantIdOrderByCheckInDateDesc(PARTICIPANT_ID))
                .thenReturn(checkIns);
        assertThat(streakService.calculateLongestStreak(PARTICIPANT_ID)).isEqualTo(5);
    }

    @Test
    @DisplayName("Single check-in gives streak of 1")
    void singleCheckIn_streakIsOne() {
        when(checkInRepository.findByParticipantIdOrderByCheckInDateDesc(PARTICIPANT_ID))
                .thenReturn(List.of(checkIn(LocalDate.now())));
        assertThat(streakService.calculateCurrentStreak(PARTICIPANT_ID)).isEqualTo(1);
        assertThat(streakService.calculateLongestStreak(PARTICIPANT_ID)).isEqualTo(1);
    }
}
