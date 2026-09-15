package com.streakmate.service;

import com.streakmate.dto.DashboardDto;
import com.streakmate.model.Challenge;
import com.streakmate.model.ChallengeParticipant;
import com.streakmate.repository.DailyCheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ChallengeService challengeService;
    private final StreakService streakService;
    private final BadgeService badgeService;
    private final DailyCheckInRepository checkInRepository;

    public DashboardDto buildDashboard(Long userId) {

        List<ChallengeParticipant> allParticipations =
                challengeService.getParticipationsByUser(userId);

        List<ChallengeParticipant> activeParticipations =
                challengeService.getActiveParticipationsByUser(userId);

        long totalCheckIns =
                checkInRepository.findAllByUserId(userId).size();

        long completedChallenges =
                allParticipations.stream()
                        .filter(p ->
                                p.getChallenge().getStatus()
                                        == Challenge.ChallengeStatus.COMPLETED
                        )
                        .count();

        double overallCompletion =
                allParticipations.stream()
                        .mapToDouble(
                                p -> challengeService
                                        .getCompletionPercentage(p.getId())
                        )
                        .average()
                        .orElse(0.0);

        return DashboardDto.builder()
                .currentStreak(
                        streakService.calculateGlobalCurrentStreak(userId)
                )
                .longestStreak(
                        streakService.calculateGlobalLongestStreak(userId)
                )
                .completionPercentage(overallCompletion)
                .activeChallenges(activeParticipations.size())
                .completedChallenges((int) completedChallenges)
                .totalCheckIns((int) totalCheckIns)
                .activeParticipations(activeParticipations)
                .recentActivity(
                        challengeService.getRecentActivity(userId)
                )
                .badges(
                        badgeService.getUserBadges(userId)
                )
                .build();
    }
}