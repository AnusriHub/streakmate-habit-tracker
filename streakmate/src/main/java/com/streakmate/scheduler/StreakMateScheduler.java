package com.streakmate.scheduler;

import com.streakmate.model.*;
import com.streakmate.repository.*;
import com.streakmate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StreakMateScheduler {

    private final UserRepository userRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final DailyCheckInRepository checkInRepository;
    private final ChallengeService challengeService;
    private final BadgeService badgeService;
    private final EmailService emailService;
    private final StreakService streakService;

    /**
     * Daily reminder at 8 PM — sends email to users who haven't checked in today.
     */
    @Scheduled(cron = "0 0 20 * * *")
    public void sendDailyReminders() {
        log.info("Running daily reminder scheduler...");
        LocalDate today = LocalDate.now();
        List<ChallengeParticipant> activeParticipants =
                participantRepository.findAll().stream()
                        .filter(p -> p.getChallenge().getStatus() == Challenge.ChallengeStatus.ACTIVE)
                        .filter(p -> !p.getChallenge().getStartDate().isAfter(today))
                        .filter(p -> !p.getChallenge().getEndDate().isBefore(today))
                        .toList();

        for (ChallengeParticipant p : activeParticipants) {
            boolean checkedIn = checkInRepository.existsByParticipantIdAndCheckInDate(p.getId(), today);
            if (!checkedIn) {
                emailService.sendReminderEmail(
                        p.getUser().getEmail(),
                        p.getUser().getUsername(),
                        p.getChallenge().getTitle()
                );
            }
        }
        log.info("Daily reminders dispatched for {} participants", activeParticipants.size());
    }

    /**
     * Weekly summary every Sunday at 9 AM.
     */
    @Scheduled(cron = "0 0 9 * * SUN")
    public void sendWeeklySummary() {
        log.info("Running weekly summary scheduler...");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            int streak = streakService.calculateGlobalCurrentStreak(user.getId());
            long totalCheckIns = checkInRepository.findAllByUserId(user.getId()).size();
            emailService.sendWeeklyReport(
                    user.getEmail(),
                    user.getUsername(),
                    streak,
                    totalCheckIns > 0 ? Math.min(100, (totalCheckIns / 7.0) * 100) : 0,
                    0,
                    1
            );
        }
    }

    /**
     * Badge processing every day at midnight.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void processBadges() {
        log.info("Processing badges...");
        userRepository.findAll().forEach(badgeService::evaluateAndAwardBadges);
    }

    /**
     * Expire old challenges daily at 1 AM.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void expireChallenges() {
        challengeService.expireOldChallenges();
    }
}
