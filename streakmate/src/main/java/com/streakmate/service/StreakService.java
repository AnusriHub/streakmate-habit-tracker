package com.streakmate.service;

import com.streakmate.model.DailyCheckIn;
import com.streakmate.repository.DailyCheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final DailyCheckInRepository checkInRepository;

    /**
     * Calculates the current streak for a participant.
     * A streak continues when the latest check-in is today or yesterday.
     */
    public int calculateCurrentStreak(Long participantId) {

        List<DailyCheckIn> checkIns =
                checkInRepository.findByParticipantIdOrderByCheckInDateDesc(
                        participantId
                );

        if (checkIns.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate latestCheckIn = checkIns.get(0).getCheckInDate();

        if (!latestCheckIn.equals(today)
                && !latestCheckIn.equals(yesterday)) {
            return 0;
        }

        int streak = 1;

        for (int i = 1; i < checkIns.size(); i++) {

            LocalDate currentDate =
                    checkIns.get(i - 1).getCheckInDate();

            LocalDate previousDate =
                    checkIns.get(i).getCheckInDate();

            if (previousDate.equals(currentDate.minusDays(1))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    /**
     * Calculates the longest streak ever achieved by a participant.
     */
    public int calculateLongestStreak(Long participantId) {

        List<DailyCheckIn> checkIns =
                checkInRepository.findByParticipantIdOrderByCheckInDateDesc(
                        participantId
                );

        if (checkIns.isEmpty()) {
            return 0;
        }

        List<LocalDate> dates = checkIns.stream()
                .map(DailyCheckIn::getCheckInDate)
                .distinct()
                .sorted()
                .toList();

        return calculateLongestConsecutiveDays(dates);
    }

    /**
     * Calculates the longest streak across all challenges for a user.
     */
    public int calculateGlobalLongestStreak(Long userId) {

        List<DailyCheckIn> checkIns =
                checkInRepository.findAllByUserId(userId);

        if (checkIns.isEmpty()) {
            return 0;
        }

        List<LocalDate> dates = checkIns.stream()
                .map(DailyCheckIn::getCheckInDate)
                .distinct()
                .sorted()
                .toList();

        return calculateLongestConsecutiveDays(dates);
    }

    /**
     * Calculates the current streak across all challenges for a user.
     * Check-ins from different challenges are combined by date.
     */
    public int calculateGlobalCurrentStreak(Long userId) {

        List<DailyCheckIn> checkIns =
                checkInRepository.findAllByUserId(userId);

        if (checkIns.isEmpty()) {
            return 0;
        }

        List<LocalDate> dates = checkIns.stream()
                .map(DailyCheckIn::getCheckInDate)
                .distinct()
                .sorted((first, second) -> second.compareTo(first))
                .toList();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate latest = dates.get(0);

        if (!latest.equals(today) && !latest.equals(yesterday)) {
            return 0;
        }

        int streak = 1;

        for (int i = 1; i < dates.size(); i++) {

            LocalDate currentDate = dates.get(i - 1);
            LocalDate previousDate = dates.get(i);

            if (previousDate.equals(currentDate.minusDays(1))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    private int calculateLongestConsecutiveDays(List<LocalDate> dates) {

        if (dates.isEmpty()) {
            return 0;
        }

        int longest = 1;
        int current = 1;

        for (int i = 1; i < dates.size(); i++) {

            if (dates.get(i).equals(dates.get(i - 1).plusDays(1))) {
                current++;
                longest = Math.max(longest, current);
            } else {
                current = 1;
            }
        }

        return longest;
    }
}