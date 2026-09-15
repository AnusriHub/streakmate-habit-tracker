//package com.streakmate.config;
//
//import com.streakmate.model.*;
//import com.streakmate.repository.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DataLoader implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final ChallengeRepository challengeRepository;
//    private final ChallengeParticipantRepository participantRepository;
//    private final DailyCheckInRepository checkInRepository;
//    private final BadgeRepository badgeRepository;
//    private final UserBadgeRepository userBadgeRepository;
//
//    @Override
//    public void run(String... args) {
//
//        // Do not create sample data if users already exist
//        if (userRepository.count() > 0) {
//            log.info("Users already exist. Skipping sample data.");
//            return;
//        }
//
//        log.info("Creating sample data...");
//
//        loadBadges();
//        loadUsers();
//
//        log.info("Sample data created successfully.");
//    }
//
//    private void loadBadges() {
//
//        List<Badge> badges = List.of(
//
//                Badge.builder()
//                        .badgeName("First Step")
//                        .description("Complete your very first check-in")
//                        .icon("🌱")
//                        .threshold(1)
//                        .type(Badge.BadgeType.CHECK_IN_COUNT)
//                        .build(),
//
//                Badge.builder()
//                        .badgeName("Getting Started")
//                        .description("Complete 7 check-ins")
//                        .icon("⭐")
//                        .threshold(7)
//                        .type(Badge.BadgeType.CHECK_IN_COUNT)
//                        .build(),
//
//                Badge.builder()
//                        .badgeName("Dedicated")
//                        .description("Complete 30 check-ins")
//                        .icon("🏆")
//                        .threshold(30)
//                        .type(Badge.BadgeType.CHECK_IN_COUNT)
//                        .build(),
//
//                Badge.builder()
//                        .badgeName("Legend")
//                        .description("Complete 100 check-ins")
//                        .icon("💎")
//                        .threshold(100)
//                        .type(Badge.BadgeType.CHECK_IN_COUNT)
//                        .build(),
//
//                Badge.builder()
//                        .badgeName("Consistent")
//                        .description("Achieve a 7-day streak")
//                        .icon("🔥")
//                        .threshold(7)
//                        .type(Badge.BadgeType.STREAK_DAYS)
//                        .build(),
//
//                Badge.builder()
//                        .badgeName("Unstoppable")
//                        .description("Achieve a 30-day streak")
//                        .icon("⚡")
//                        .threshold(30)
//                        .type(Badge.BadgeType.STREAK_DAYS)
//                        .build()
//        );
//
//        badgeRepository.saveAll(badges);
//
//        log.info("Created {} badges.", badges.size());
//    }
//
//    private void loadUsers() {
//
//        // =========================
//        // USERS
//        // =========================
//
//        User alice = userRepository.save(
//                User.builder()
//                        .name("Alice Johnson")
//                        .email("alice@example.com")
//                        .username("alice_j")
//                        .build()
//        );
//
//        User bob = userRepository.save(
//                User.builder()
//                        .name("Bob Smith")
//                        .email("bob@example.com")
//                        .username("bobsmith")
//                        .build()
//        );
//
//        User priya = userRepository.save(
//                User.builder()
//                        .name("Priya Sharma")
//                        .email("priya@example.com")
//                        .username("priya_s")
//                        .build()
//        );
//
//        // =========================
//        // CHALLENGES
//        // =========================
//
//        LocalDate today = LocalDate.now();
//
//        Challenge dsaChallenge = challengeRepository.save(
//                Challenge.builder()
//                        .title("DSA 60-Day Challenge")
//                        .description(
//                                "Solve LeetCode problems daily. Target: NeetCode 150 completion."
//                        )
//                        .challengeCode("DSA60")
//                        .startDate(today.minusDays(10))
//                        .endDate(today.plusDays(49))
//                        .creator(alice)
//                        .status(Challenge.ChallengeStatus.ACTIVE)
//                        .build()
//        );
//
//        Challenge germanChallenge = challengeRepository.save(
//                Challenge.builder()
//                        .title("Learn German in 30 Days")
//                        .description(
//                                "Daily German practice using Duolingo and grammar exercises."
//                        )
//                        .challengeCode("GERMAN30")
//                        .startDate(today.minusDays(5))
//                        .endDate(today.plusDays(24))
//                        .creator(bob)
//                        .status(Challenge.ChallengeStatus.ACTIVE)
//                        .build()
//        );
//
//        Challenge readingChallenge = challengeRepository.save(
//                Challenge.builder()
//                        .title("Read 10 Pages Daily")
//                        .description(
//                                "Build a reading habit — 10 pages every day for 21 days."
//                        )
//                        .challengeCode("READING21")
//                        .startDate(today.minusDays(7))
//                        .endDate(today.plusDays(13))
//                        .creator(priya)
//                        .status(Challenge.ChallengeStatus.ACTIVE)
//                        .build()
//        );
//
//        // =========================
//        // PARTICIPANTS
//        // =========================
//
//        ChallengeParticipant aliceDSA = participantRepository.save(
//                ChallengeParticipant.builder()
//                        .user(alice)
//                        .challenge(dsaChallenge)
//                        .build()
//        );
//
//        ChallengeParticipant bobDSA = participantRepository.save(
//                ChallengeParticipant.builder()
//                        .user(bob)
//                        .challenge(dsaChallenge)
//                        .build()
//        );
//
//        ChallengeParticipant priyaDSA = participantRepository.save(
//                ChallengeParticipant.builder()
//                        .user(priya)
//                        .challenge(dsaChallenge)
//                        .build()
//        );
//
//        ChallengeParticipant bobGerman = participantRepository.save(
//                ChallengeParticipant.builder()
//                        .user(bob)
//                        .challenge(germanChallenge)
//                        .build()
//        );
//
//        ChallengeParticipant aliceGerman = participantRepository.save(
//                ChallengeParticipant.builder()
//                        .user(alice)
//                        .challenge(germanChallenge)
//                        .build()
//        );
//
//        ChallengeParticipant priyaReading = participantRepository.save(
//                ChallengeParticipant.builder()
//                        .user(priya)
//                        .challenge(readingChallenge)
//                        .build()
//        );
//
//        ChallengeParticipant aliceReading = participantRepository.save(
//                ChallengeParticipant.builder()
//                        .user(alice)
//                        .challenge(readingChallenge)
//                        .build()
//        );
//
//        // =========================
//        // DSA CHECK-INS
//        // =========================
//
//        // Alice - 10 consecutive days
//        for (int i = 9; i >= 0; i--) {
//
//            checkInRepository.save(
//                    DailyCheckIn.builder()
//                            .participant(aliceDSA)
//                            .checkInDate(today.minusDays(i))
//                            .note("Solved " + (3 - i % 3) + " LeetCode problems")
//                            .completed(true)
//                            .build()
//            );
//        }
//
//        // Bob - 8 check-ins
//        // Missed day 3 and day 7
//        for (int i = 9; i >= 0; i--) {
//
//            if (i == 3 || i == 7) {
//                continue;
//            }
//
//            checkInRepository.save(
//                    DailyCheckIn.builder()
//                            .participant(bobDSA)
//                            .checkInDate(today.minusDays(i))
//                            .note("Completed binary search problems")
//                            .completed(true)
//                            .build()
//            );
//        }
//
//        // Priya - 6 consecutive check-ins
//        for (int i = 5; i >= 0; i--) {
//
//            checkInRepository.save(
//                    DailyCheckIn.builder()
//                            .participant(priyaDSA)
//                            .checkInDate(today.minusDays(i))
//                            .note("Trees and graphs practice")
//                            .completed(true)
//                            .build()
//            );
//        }
//
//        // =========================
//        // GERMAN CHECK-INS
//        // =========================
//
//        for (int i = 4; i >= 0; i--) {
//
//            checkInRepository.save(
//                    DailyCheckIn.builder()
//                            .participant(bobGerman)
//                            .checkInDate(today.minusDays(i))
//                            .note("Duolingo + verb conjugation")
//                            .completed(true)
//                            .build()
//            );
//
//            if (i < 3) {
//
//                checkInRepository.save(
//                        DailyCheckIn.builder()
//                                .participant(aliceGerman)
//                                .checkInDate(today.minusDays(i))
//                                .note("Guten Morgen practice")
//                                .completed(true)
//                                .build()
//                );
//            }
//        }
//
//        // =========================
//        // READING CHECK-INS
//        // =========================
//
//        for (int i = 6; i >= 0; i--) {
//
//            checkInRepository.save(
//                    DailyCheckIn.builder()
//                            .participant(priyaReading)
//                            .checkInDate(today.minusDays(i))
//                            .note("Read Atomic Habits – chapter " + (7 - i))
//                            .completed(true)
//                            .build()
//            );
//
//            if (i % 2 == 0) {
//
//                checkInRepository.save(
//                        DailyCheckIn.builder()
//                                .participant(aliceReading)
//                                .checkInDate(today.minusDays(i))
//                                .note("The Pragmatic Programmer – great concepts!")
//                                .completed(true)
//                                .build()
//                );
//            }
//        }
//
//        // =========================
//        // STARTER BADGES
//        // =========================
//
//        Badge firstStep = badgeRepository
//                .findByBadgeName("First Step")
//                .orElseThrow(() ->
//                        new IllegalStateException("First Step badge was not created.")
//                );
//
//        Badge consistent = badgeRepository
//                .findByBadgeName("Consistent")
//                .orElseThrow(() ->
//                        new IllegalStateException("Consistent badge was not created.")
//                );
//
//        userBadgeRepository.save(
//                UserBadge.builder()
//                        .user(alice)
//                        .badge(firstStep)
//                        .build()
//        );
//
//        userBadgeRepository.save(
//                UserBadge.builder()
//                        .user(alice)
//                        .badge(consistent)
//                        .build()
//        );
//
//        userBadgeRepository.save(
//                UserBadge.builder()
//                        .user(bob)
//                        .badge(firstStep)
//                        .build()
//        );
//
//        userBadgeRepository.save(
//                UserBadge.builder()
//                        .user(priya)
//                        .badge(firstStep)
//                        .build()
//        );
//
//        log.info("Sample data loaded: 3 users, 3 challenges, check-ins, and badges.");
//    }
//}