package com.streakmate.controller;

import com.streakmate.config.SessionConfig;
import com.streakmate.model.User;
import com.streakmate.model.ChallengeParticipant;
import com.streakmate.service.BadgeService;
import com.streakmate.service.ChallengeService;
import com.streakmate.service.StreakService;
import com.streakmate.service.UserService;
import com.streakmate.repository.DailyCheckInRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final ChallengeService challengeService;
    private final StreakService streakService;
    private final BadgeService badgeService;
    private final DailyCheckInRepository checkInRepository;

    @GetMapping("/profile")
    public String myProfile(
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute(
                SessionConfig.SESSION_USER_ID
        );

        if (userId == null) {
            return "redirect:/login";
        }

        return buildProfile(userId, model);
    }

    @GetMapping("/profile/{username}")
    public String viewProfile(
            @PathVariable String username,
            HttpSession session,
            Model model) {

        if (session.getAttribute(SessionConfig.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(username);

        return buildProfile(user.getId(), model);
    }

    private String buildProfile(Long userId, Model model) {

        User user = userService.findById(userId);

        long totalCheckIns =
                checkInRepository.findAllByUserId(userId).size();

        int longestStreak =
                streakService.calculateGlobalLongestStreak(userId);

        int currentStreak =
                streakService.calculateGlobalCurrentStreak(userId);

        List<ChallengeParticipant> participations =
                challengeService.getParticipationsByUser(userId);

        var badges = badgeService.getUserBadges(userId);

        model.addAttribute("user", user);
        model.addAttribute("totalCheckIns", totalCheckIns);
        model.addAttribute("longestStreak", longestStreak);
        model.addAttribute("currentStreak", currentStreak);
        model.addAttribute(
                "totalChallenges",
                participations.size()
        );
        model.addAttribute("participations", participations);
        model.addAttribute("badges", badges);

        return "profile/profile";
    }
}