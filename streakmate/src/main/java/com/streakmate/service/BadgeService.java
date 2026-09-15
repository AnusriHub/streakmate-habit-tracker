package com.streakmate.service;

import com.streakmate.model.Badge;
import com.streakmate.model.User;
import com.streakmate.model.UserBadge;
import com.streakmate.repository.BadgeRepository;
import com.streakmate.repository.DailyCheckInRepository;
import com.streakmate.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final DailyCheckInRepository checkInRepository;
    private final StreakService streakService;

    @Transactional
    public void evaluateAndAwardBadges(User user) {

        long totalCheckIns =
                checkInRepository.findAllByUserId(user.getId()).size();

        int longestStreak =
                streakService.calculateGlobalLongestStreak(user.getId());

        List<Badge> checkInBadges =
                badgeRepository.findByTypeAndThresholdLessThanEqual(
                        Badge.BadgeType.CHECK_IN_COUNT,
                        (int) totalCheckIns
                );

        for (Badge badge : checkInBadges) {
            awardIfNotOwned(user, badge);
        }

        List<Badge> streakBadges =
                badgeRepository.findByTypeAndThresholdLessThanEqual(
                        Badge.BadgeType.STREAK_DAYS,
                        longestStreak
                );

        for (Badge badge : streakBadges) {
            awardIfNotOwned(user, badge);
        }
    }

    private void awardIfNotOwned(User user, Badge badge) {

        boolean alreadyOwned =
                userBadgeRepository.existsByUserIdAndBadgeId(
                        user.getId(),
                        badge.getId()
                );

        if (alreadyOwned) {
            return;
        }

        UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(badge)
                .build();

        userBadgeRepository.save(userBadge);

        log.info(
                "Badge '{}' awarded to {}",
                badge.getBadgeName(),
                user.getUsername()
        );
    }

    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId);
    }
}